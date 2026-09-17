import axios from "axios";

// Enable credentials globally so that all direct axios calls also send HttpOnly cookies
axios.defaults.withCredentials = true;

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor: guarantees withCredentials is set on every request
api.interceptors.request.use(
  (config) => {
    config.withCredentials = true;
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: handles 401 Unauthorized / session expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.warn("Session expired or unauthorized request.");
    }
    return Promise.reject(error);
  }
);

export default api;
