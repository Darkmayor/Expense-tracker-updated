# AI-Powered Expense Tracker: Comprehensive Project Documentation

## 1. Working Details of the Project
The AI-Powered Expense Tracker is a production-grade microservices system designed to automate financial tracking. The core value proposition is the ability to send natural language messages (e.g., "Spent $50 on groceries at Walmart") and have the system automatically extract the data, categorize it, and save it to a user's profile.

### The End-to-End Flow:
1.  **Frontend/Client**: Sends a request to the **Kong API Gateway**.
2.  **Kong Gateway**: 
    *   Authenticates the request using a **Custom Lua Plugin**.
    *   Injects the validated `X-User-ID` into the request header.
    *   Routes the request to the appropriate microservice.
3.  **AI Extraction (DS Service)**: Processes raw text using **Mistral AI**, extracts structured JSON, and publishes the data to a **Kafka** topic.
4.  **Backend Integration**: The **Expense Service** consumes the Kafka event and persists the expense to the database under the correct user ID.
5.  **User Management**: Profiles are managed by the **User Service**, which uses **Redis** for high-speed caching of frequently accessed profile data.
6.  **Optimized Retrieval**: The **Expense Service** also leverages **Redis** to cache user expense lists, significantly reducing database load for frequent lookups.

---

## 2. Working of Each Service

### 🛡️ Kong API Gateway
*   **Role**: Reverse proxy and security gatekeeper.
*   **Key Features**: 
    *   **Custom Auth Plugin**: A Lua-based script that challenges every request. it calls the Auth Service's `/ping` endpoint to verify JWTs.
    *   **Rate Limiting**: Protects the backend from brute-force or DDoS attacks (set to 60 requests/minute).
    *   **Path Stripping**: Manages URI prefixes so that internal service routing remains simple.

### 🔑 Authentication Service (Java / Spring Boot)
*   **Role**: Identity provider and token issuer.
*   **Responsibilities**:
    *   Handles User Signup and Login.
    *   Issues **JWT Access Tokens** and **Refresh Tokens**.
    *   Provides high-speed validation via the `/ping` endpoint.
    *   Publishes `UserInfoEvent` to Kafka whenever a new user joins.

### 👤 User Service (Java / Spring Boot)
*   **Role**: Profile and preferences manager.
*   **Responsibilities**:
    *   Consumes signup events from Kafka to initialize profile records.
    *   Manages profile updates (Name, Phone, Bio).
    *   Uses **Redis Caching** to serve profile data in milliseconds.

### 💰 Expense Service (Java / Spring Boot)
*   **Role**: Transaction ledger and repository.
*   **Responsibilities**:
    *   Standard CRUD operations for expenses.
    *   Acts as a **Kafka Consumer**, listening for expenses extracted by the AI service.
    *   Ensures data integrity between user IDs and transactions.
    *   **Redis Caching**: Caches expense lists by `userId` to speed up historical data retrieval.

### 🤖 DS (Data Science) Service (Python / Flask)
*   **Role**: The intellectual engine of the project.
*   **Responsibilities**:
    *   Integrates with **Mistral AI (via LangChain)**.
    *   Uses **Pydantic** models to force the AI to return structured data (Amount, Merchant, Currency).
    *   Implements a **Retry Loop** to ensure it reconnects to Kafka if the broker is slow to start.

---
