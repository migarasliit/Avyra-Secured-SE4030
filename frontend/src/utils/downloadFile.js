// src/utils/downloadFile.js
import api from "../services/api";

export async function downloadFile(filename) {
  const response = await api.get(`/api/downloads/${filename}`, {
    responseType: "blob",
  });

  const url = window.URL.createObjectURL(response.data);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}
