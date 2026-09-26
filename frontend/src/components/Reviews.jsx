// src/components/Reviews.jsx
import React, { useState, useEffect } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const REVIEWS_PATH = "/api/reviews";

function Reviews() {
  const { isAuthenticated } = useAuth();
  const [reviews, setReviews] = useState([]);
  const [reviewText, setReviewText] = useState("");

  useEffect(() => {
    // Fetch all reviews (GET is public)
    api
      .get(REVIEWS_PATH)
      .then((res) => setReviews(res.data))
      .catch((err) => console.error("Failed to load reviews", err));
  }, []);

  const submitReview = async () => {
    if (!isAuthenticated) {
      alert("You must be logged in to submit a review");
      return;
    }

    try {
      const response = await api.post(REVIEWS_PATH, { text: reviewText });
      setReviews((prev) => [...prev, response.data]);
      setReviewText("");
    } catch (err) {
      console.error("Error adding review:", err);
      alert("Failed to add review. Check console for details.");
    }
  };

  const deleteReview = async (id) => {
    if (!isAuthenticated) {
      alert("You must be logged in to delete a review");
      return;
    }

    try {
      await api.delete(`${REVIEWS_PATH}/${id}`);
      setReviews((prev) => prev.filter((rev) => rev.id !== id));
    } catch (err) {
      console.error("Error deleting review:", err);
      alert("Failed to delete review. Check console for details.");
    }
  };

  return (
    <div>
      <h2>Reviews</h2>
      <ul>
        {reviews.map((rev) => (
          <li key={rev.id}>
            {rev.text}{" "}
            <button onClick={() => deleteReview(rev.id)}>Delete</button>
          </li>
        ))}
      </ul>
      <textarea
        value={reviewText}
        onChange={(e) => setReviewText(e.target.value)}
        placeholder="Write your review here..."
      ></textarea>
      <button onClick={submitReview}>Submit Review</button>
    </div>
  );
}

export default Reviews;
