import React from 'react';

/**
 * GoogleLoginButton
 * Triggers the Spring Security Google OAuth2 authorization flow.
 * Redirects browser window directly to backend OAuth2 initiation endpoint.
 */
const GoogleLoginButton = ({
  text = "Continue with Google",
  className = "",
  disabled = false,
  endpoint = "http://localhost:8080/oauth2/authorization/google",
}) => {
  const handleGoogleLogin = () => {
    if (disabled) return;
    // Direct browser navigation is required for OAuth2 authorization code redirection
    window.location.href = endpoint;
  };

  return (
    <button
      type="button"
      onClick={handleGoogleLogin}
      disabled={disabled}
      aria-label="Sign in with Google"
      className={`
        w-full
        flex
        items-center
        justify-center
        gap-3
        py-3
        px-4
        rounded-lg
        bg-[#13111c]
        hover:bg-[#1f1b2e]
        text-white
        font-semibold
        text-sm
        tracking-wide
        border
        border-purple-600/50
        hover:border-pink-500
        transition-all
        duration-300
        shadow-sm
        hover:shadow-[0_0_15px_rgba(236,72,153,0.35)]
        disabled:opacity-50
        disabled:cursor-not-allowed
        cursor-pointer
        ${className}
      `}
    >
      {/* Official Google 'G' SVG Icon */}
      <svg
        className="w-5 h-5 flex-shrink-0"
        viewBox="0 0 24 24"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          fill="#4285F4"
          d="M23.745 12.27c0-.7-.06-1.4-.19-2.07H12v4.51h6.6c-.29 1.52-1.14 2.82-2.4 3.68v3.05h3.88c2.27-2.09 3.66-5.17 3.66-9.17z"
        />
        <path
          fill="#34A853"
          d="M12 24c3.24 0 5.95-1.08 7.93-2.91l-3.88-3.05c-1.08.72-2.45 1.16-4.05 1.16-3.12 0-5.77-2.1-6.72-4.93H1.25v3.15C3.26 21.36 7.36 24 12 24z"
        />
        <path
          fill="#FBBC05"
          d="M5.28 14.27c-.25-.72-.38-1.49-.38-2.27s.13-1.55.38-2.27V6.58H1.25C.45 8.18 0 9.98 0 12s.45 3.82 1.25 5.42l4.03-3.15z"
        />
        <path
          fill="#EA4335"
          d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.36 0 3.26 2.64 1.25 6.58l4.03 3.15c.95-2.83 3.6-4.98 6.72-4.98z"
        />
      </svg>
      <span>{text}</span>
    </button>
  );
};

export default GoogleLoginButton;
