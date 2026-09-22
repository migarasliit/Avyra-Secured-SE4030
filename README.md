# 🛡️ SE4030 Secure Software Development - Secured Version

This repository contains the security-enhanced version of the Avyra Game Download Platform, developed as part of the SE4030 Secure Software Development module assignment.

### 👥 Team Members
- [Member 1 Name] - [Index Number]
- [Member 2 Name] - [Index Number]
- [Member 3 Name] - [Index Number]
- [Member 4 Name] - [Index Number]

### 🔗 Project Links
- **Original Project:** https://github.com/3hal0n/Avyra
- **Modified Project:** https://github.com/migarasliit/Avyra-Secured-SE4030
- **YouTube Demonstration:** [Insert YouTube Link Here - Max 20 mins]

### 🛡️ Security Improvements (7+ Distinct Vulnerabilities Fixed)
1. **Insecure Direct Object Reference (IDOR)** - Fixed in Review deletion endpoint.
2. **Mass Assignment** - Fixed in Review creation endpoint.
3. **Stored Cross-Site Scripting (XSS)** - Fixed using OWASP Java Encoder.
4. **Path Traversal** - Fixed in file download endpoint.
5. **Cryptographic Failures** - Removed hardcoded secrets, migrated to environment variables.
6. **Using Components with Known Vulnerabilities** - Updated dependencies via OWASP Dependency-Check.
7. **Session Hijacking** - Migrated JWT storage from localStorage to HttpOnly Secure cookies.
8. **Security Misconfiguration** - Fixed overly permissive CORS settings and verbose error messages.

### 🔑 Additional Feature
- **OAuth/OpenID Connect Integration** - Added "Login with Google" functionality using Authorization Code Grant Type.

---
*(Original Project Documentation Below)*




# Avyra Game Download Platform
![React](https://img.shields.io/badge/Frontend-React-blue?logo=react)
![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-green?logo=springboot)
![Tailwind CSS](https://img.shields.io/badge/Style-TailwindCSS-38bdf8?logo=tailwindcss)
![PayPal](https://img.shields.io/badge/Payments-PayPal-00457C?logo=paypal)
![JWT](https://img.shields.io/badge/Auth-JWT-orange?logo=jsonwebtokens)
![GitHub last commit](https://img.shields.io/github/last-commit/3hal0n/Avyra)
![GitHub license](https://img.shields.io/github/license/3hal0n/Avyra)


Avyra is a Steam-inspired fullstack web application built with a **React** frontend and a **Java Spring Boot** backend. It provides a modern, interactive platform for users to browse, purchase, review, and download games, with features such as authentication, wishlist, cart, chatbot assistant, and more.

---
## Link
https://avyra-lac.vercel.app/

---

## Table of Contents

- [Features](#features)
- [Frontend Overview](#frontend-overview)
- [Backend Overview](#backend-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Environment Setup](#environment-setup)
- [How to Run](#how-to-run)
- [Screenshots](#screenshots)
- [License](#license)

---

## Features

- **User Authentication**: Register, login, and secure session management with JWT.
- **Game Catalog**: Browse, search, and filter games with detailed pages.
- **Wishlist & Cart**: Add games to wishlist or cart, manage quantities, and purchase.
- **PayPal Payment Integration**: Secure checkout and payment processing using PayPal.
- **Download System**: Access purchased games and download files securely.
- **Chatbot Assistant**: AI-powered chatbot for recommendations and support.
- **Reviews**: Submit, view, and delete reviews for games.
- **Profile Management**: View account info, change password, and see order/download history.
- **Responsive UI**: Modern, glassmorphic design with Tailwind CSS and custom animations.

---

## Frontend Overview

**Location:** [`frontend/`](frontend/)

- **Framework:** React (with Vite for fast development)
- **Styling:** Tailwind CSS, custom CSS for neon/glassmorphic effects
- **Routing:** React Router
- **State Management:** React Context (for Auth and Wishlist)
- **API Calls:** Axios
- **3D Models:** `@react-three/fiber` and `@react-three/drei` for animated login/register characters
- **Components:**
  - `Navbar`, `Footer`: Consistent navigation and branding
  - `ChatbotInterface`: AI assistant for game queries and recommendations
  - `GameList`, `GameDetails`: Game browsing and detail views
  - `Wishlist`, `Cart`, `Downloads`: Manage user collections and purchases
  - `Profile`: Tabbed user dashboard for overview, security, and library
  - `Reviews`: Add/delete reviews for games

**Notable Files:**
- [`src/pages/Profile.jsx`](frontend/src/pages/Profile.jsx): User dashboard with tabs for overview, security (password change), and library (wishlist/cart summary).
- [`src/pages/Chatbot.jsx`](frontend/src/pages/Chatbot.jsx): ChatGPT-style AI assistant interface.
- [`src/context/AuthContext.jsx`](frontend/src/context/AuthContext.jsx): Handles authentication state and API token management.
- [`src/context/WishlistContext.jsx`](frontend/src/context/WishlistContext.jsx): Manages wishlist state and API sync.
- [`src/utils/downloadFile.js`](frontend/src/utils/downloadFile.js): Secure file download utility.

---

## Backend Overview

**Location:** [`backend/`](backend/)

- **Framework:** Java Spring Boot
- **Database:** JPA/Hibernate (with entities for User, Game, Review, Wishlist, etc.)
- **Security:** JWT-based authentication, Spring Security
- **REST API:** Controllers for authentication, games, reviews, wishlist, cart, downloads, and chatbot
- **AI Integration:** Chatbot endpoint using Gemini API for AI-powered responses
- **File Handling:** Secure download endpoints for purchased games

**Notable Files:**
- [`src/main/java/backend/controller/ReviewController.java`](backend/src/main/java/backend/controller/ReviewController.java): Handles review CRUD operations.
- [`src/main/java/backend/controller/DownloadController.java`](backend/src/main/java/backend/controller/DownloadController.java): Secure file download for authenticated users.
- [`src/main/java/backend/service/ChatbotService.java`](backend/src/main/java/backend/service/ChatbotService.java): Integrates with Gemini API for chatbot responses.
- [`src/main/java/backend/model/Game.java`](backend/src/main/java/backend/model/Game.java): Game entity with fields for requirements, platforms, genres, etc.
- [`src/main/java/backend/model/User.java`](backend/src/main/java/backend/model/User.java): User entity with registration and authentication logic.

---

## Tech Stack

- **Frontend:** React, Vite, Tailwind CSS, Axios, React Router, @react-three/fiber
- **Backend:** Java 17+, Spring Boot, Spring Security, JPA/Hibernate, JWT, Gemini API (for chatbot)
- **Database:** H2 (dev) or any SQL DB (prod)
- **Other:** ESLint, Prettier, custom CSS for neon/glassmorphic UI

---

### PayPal Payment Integration

The platform uses PayPal for secure payment processing. Users can checkout their cart and complete purchases using their PayPal account. The backend verifies payment status and grants access to purchased games upon successful transactions.

---

##License
MIT License. See LICENSE for details.

---

## Environment Setup

The backend reads several secrets from OS environment variables (via `${VAR}` placeholders and Spring Boot's environment-variable-to-property relaxed binding in `application.properties`) instead of hardcoding them. **The app will not start** without at minimum `JWT_SECRET`, `GEMINI_API_KEY` and `HUGGINGFACE_API_KEY` set.

A template listing every variable, with comments, lives at [`backend/.env.example`](backend/.env.example). Copy it and fill in real values:

```sh
cd backend
cp .env.example .env   # then edit .env with real secrets - never commit it
```

> **Note:** this project does not use a dotenv-loading library, so `backend/.env` is *not* read automatically by Spring Boot — it's a reference file. You still need to load those values into your actual shell/session or IDE run configuration before starting the app, using one of the options below.

**Option A — PowerShell (per session):**
```powershell
$env:JWT_SECRET = "a-random-32-plus-character-secret"
$env:JWT_EXPIRATION_MS = "86400000"
$env:GEMINI_API_KEY = "your-gemini-api-key"
$env:GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
$env:HUGGINGFACE_API_KEY = "your-huggingface-api-key"
$env:HUGGINGFACE_MODEL_LLM = "your-model-id"
$env:GOOGLE_CLIENT_ID = "your-google-oauth-client-id"
$env:GOOGLE_CLIENT_SECRET = "your-google-oauth-client-secret"
cd backend
.\mvnw spring-boot:run
```

**Option B — bash / Git Bash / macOS / Linux (per session):**
```sh
set -a
source .env
set +a
./mvnw spring-boot:run
```

**Option C — IDE run configuration:** in IntelliJ/Eclipse, open the Spring Boot run configuration for `backend` and add each variable under "Environment variables" instead of exporting them in a shell.

Required vs. optional:
- `JWT_SECRET`, `GEMINI_API_KEY`, `HUGGINGFACE_API_KEY` — **required**, no default, app fails to start without them.
- `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` — optional for the app to boot (placeholder defaults exist), but a real Google login will not complete without them. See [`docs/SECURITY-VULN-7-8.md`](docs/SECURITY-VULN-7-8.md) for how to obtain Google OAuth2 credentials.
- `APP_SECURITY_COOKIE_SECURE` — defaults to `false` for local HTTP testing; set to `true` for any HTTPS/deployed environment so the JWT cookie is only sent over TLS.

The frontend needs no environment variables — it calls the backend at a hardcoded `http://localhost:8080` for local development.

---

## How to Run

Frontend
cd frontend
npm install
npm run dev

---
### Backend

```sh
cd backend
./mvnw spring-boot:run

Runs on http://localhost:8080





---

## Testing & 10-minute TestNG Demo

We include a short TestNG demo used for a class presentation. See detailed instructions and speaking notes in `backend/TESTING.md`.

Quick run (Unix/macOS):

```sh
cd backend
chmod +x mvnw || true
./mvnw -B test
```

Quick run (Windows PowerShell):

```powershell
cd backend
.\mvnw -B test
```

After running, TestNG reports are in `backend/target/surefire-reports/` (open `index.html`).



