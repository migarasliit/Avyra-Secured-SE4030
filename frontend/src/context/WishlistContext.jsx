import React, { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "./AuthContext";

const WishlistContext = createContext();

export const useWishlist = () => useContext(WishlistContext);

export const WishlistProvider = ({ children }) => {
  const [wishlist, setWishlist] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [feedback, setFeedback] = useState(null);

  const navigate = useNavigate();
  const { isAuthenticated, loading: authLoading } = useAuth();

  // Fetch wishlist from backend (cookie-authenticated via the shared api instance)
  const fetchWishlist = async () => {
    if (!isAuthenticated) {
      setWishlist([]);
      setError("You need to log in to view your wishlist.");
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      const response = await api.get("/api/wishlist");
      setWishlist(response.data);
      setError(null);
    } catch (err) {
      setError(
        err.response?.data?.error || "Failed to load wishlist. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  // Add to wishlist
  const addToWishlist = async (gameId) => {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }
    try {
      await api.post(`/api/wishlist/${gameId}`);
      setFeedback("Game added to wishlist!");
      // Refetch wishlist to keep in sync
      await fetchWishlist();
    } catch (err) {
      setFeedback(
        err.response?.data?.error || "Failed to add to wishlist."
      );
    }
    setTimeout(() => setFeedback(null), 2500);
  };

  // Remove from wishlist
  const removeFromWishlist = async (gameId) => {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }
    try {
      await api.delete(`/api/wishlist/${gameId}`);
      setFeedback("Game removed from wishlist!");
      // Refetch wishlist to keep in sync
      await fetchWishlist();
    } catch (err) {
      setFeedback(
        err.response?.data?.error || "Failed to remove from wishlist."
      );
    }
    setTimeout(() => setFeedback(null), 2500);
  };

  // Check if game is in wishlist
  const isInWishlist = (gameId) => {
    return wishlist.some(item => item.gameId === gameId);
  };

  useEffect(() => {
    if (authLoading) return; // wait for AuthContext's initial /api/auth/me check
    if (isAuthenticated) {
      fetchWishlist();
    } else {
      setWishlist([]);
      setLoading(false);
    }
  }, [isAuthenticated, authLoading]);

  return (
    <WishlistContext.Provider value={{
      wishlist,
      loading,
      error,
      feedback,
      addToWishlist,
      removeFromWishlist,
      isInWishlist,
      refetchWishlist: fetchWishlist
    }}>
      {children}
    </WishlistContext.Provider>
  );
};
