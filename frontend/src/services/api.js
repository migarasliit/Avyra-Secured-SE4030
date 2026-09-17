import axios from "axios";

/**
 * Utility to extract cookie value by name from document.cookie
 */
export function getCookie(name) {
  if (typeof document === "undefined") return null;
  const match = document.cookie.match(new RegExp("(^|;\\s*)(" + name + ")=([^;]*)"));
  return match ? decodeURIComponent(match[3]) : null;
}

// Global Axios defaults for credentials and CSRF
axios.defaults.withCredentials = true;
axios.defaults.xsrfCookieName = "XSRF-TOKEN";
axios.defaults.xsrfHeaderName = "X-XSRF-TOKEN";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor: Guarantees withCredentials and attaches X-XSRF-TOKEN header
api.interceptors.request.use(
  (config) => {
    config.withCredentials = true;

    // Extract XSRF-TOKEN from cookie (CookieCsrfTokenRepository withHttpOnlyFalse)
    const xsrfToken = getCookie("XSRF-TOKEN");
    if (xsrfToken) {
      config.headers["X-XSRF-TOKEN"] = xsrfToken;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: Handles 401 Unauthorized / session expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.warn("Session expired or unauthorized request.");
    }
    return Promise.reject(error);
  }
);

/**
 * Ensures CSRF token cookie is loaded from the backend
 */
export async function ensureCsrfToken() {
  try {
    await api.get("/api/auth/csrf");
  } catch (err) {
    console.error("Failed to initialize CSRF token:", err);
  }
}

export default api;
