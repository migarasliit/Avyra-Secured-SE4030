package backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/downloads")
public class DownloadController {

    @Value("${download.files.path}")
    private String downloadFilesPath;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable String filename,
            Authentication authentication) throws IOException {

        // 1. Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // --- SECURITY FIX: Strict Path Traversal Prevention ---
        // 1. Get the absolute, normalized base directory (e.g., F:/Avyra/frontend/public/downloads)
        Path baseDirectory = Paths.get(downloadFilesPath).toAbsolutePath().normalize();

        // 2. Resolve the requested filename and normalize it
        // (This automatically resolves any "../" or URL-encoded traversal tricks)
        Path targetPath = baseDirectory.resolve(filename).normalize();

        // 3. Verify the target path is STRICTLY within the base directory
        if (!targetPath.startsWith(baseDirectory)) {
            throw new SecurityException("Access Denied: Invalid file path requested.");
        }
        // ------------------------------------------------------

        File file = targetPath.toFile();

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}