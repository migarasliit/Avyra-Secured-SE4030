package backend.service.impl;

import backend.dto.ReviewDTO;
import backend.dto.ReviewRequestDTO;
import backend.model.Game;
import backend.model.Review;
import backend.model.User;
import backend.repository.GameRepository;
import backend.repository.ReviewRepository;
import backend.service.ReviewService;
import backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private UserService userService;

    @Override
    public List<ReviewDTO> getReviewsForGame(Long gameId) {
        return reviewRepo.findByGameId(gameId).stream()
                .map(r -> new ReviewDTO(
                        r.getId(),
                        r.getUser().getUsername(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()
                )).collect(Collectors.toList());
    }

    @Override
    @Transactional // Added transaction annotation
    public void addReview(ReviewRequestDTO dto) {
        User user = userService.getAuthenticatedUser();
        Game game = gameRepo.findById(dto.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Check if user already reviewed this game
        boolean alreadyReviewed = reviewRepo.existsByUserIdAndGameId(user.getId(), game.getId());
        if (alreadyReviewed) {
            throw new RuntimeException("You already reviewed this game.");
        }

        Review review = new Review(
                game,
                user,
                dto.getRating(),
                dto.getComment(),
                LocalDateTime.now()
        );

        reviewRepo.save(review);
    }

    @Override
    @Transactional // Added transaction annotation
    public void deleteReview(Long reviewId) {
        User user = userService.getAuthenticatedUser();

        // Fix: single query, ownership enforced in DB, 0 rows => not yours OR not found.
        // We return 404 either way to avoid revealing which review IDs exist.
        int deleted = reviewRepo.deleteByIdAndUser(reviewId, user);
        if (deleted == 0) {
            throw new EntityNotFoundException("Review not found");
        }

    }
}