import React, { createContext, useContext, useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Check if logged in by calling /api/auth/me (browser automatically sends HttpOnly cookie)
  const checkAuth = useCallback(async () => {
    try {
      const res = await api.get("/api/auth/me");
      setUser(res.data);
    } catch (err) {
      // Cookie is missing, expired, or invalid
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  // Run on mount: verify session
  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  // Login handler: Authenticate, backend sets HttpOnly cookie, verify user
  const login = async (credentials) => {
    if (credentials) {
      await api.post("/api/auth/login", credentials);
    }
    await checkAuth();
    navigate("/");
  };

  // Logout handler: Ask backend to expire HttpOnly cookie and clear state
  const logout = async () => {
    try {
      await api.post("/api/auth/logout");
    } catch (err) {
      console.error("Logout request failed:", err);
    } finally {
      setUser(null);
      navigate("/login");
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        logout,
        isAuthenticated: !!user,
        checkAuth,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

