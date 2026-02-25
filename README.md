# 🚀 TradeVerse

TradeVerse is a microservices-based crypto trading platform built using Spring Boot.  This is the Trading part of the project.
It implements stateless JWT authentication, real-time market price integration, financial precision using BigDecimal, and AI-powered portfolio insights using Ollama.

---

##  Architecture Overview

TradeVerse consists of two microservices:

###  1. Auth Service
- User registration & login
- BCrypt password hashing
- JWT generation & validation
- Stateless Spring Security configuration

###  2. Trading Service
- Wallet management
- Buy/Sell crypto operations
- Weighted average cost calculation
- Real-time BTC price from CoinGecko
- Profit/Loss computation
- AI-powered portfolio insights using Ollama

---

##  Key Features

-  Microservices architecture
-  Stateless JWT authentication
-  Spring Security filter chain
-  Financial precision using BigDecimal
-  Weighted average buy price logic
-  Transactional operations with rollback
-  Real-time market price integration (CoinGecko API)
-  AI portfolio assistant (Ollama + LLaMA3)
-  Global exception handling
-  Clean DTO-based API design

---

##  Authentication Flow

1. User logs in via Auth Service.
2. Auth Service generates a signed JWT.
3. Trading Service validates JWT locally using shared secret.
4. SecurityContext is populated per request (stateless).

No session storage is used.

---

##  Financial Logic

- Buy operations update weighted average cost.
- Sell operations validate ownership and adjust holdings.
- Profit/Loss is calculated using:

currentValue = quantity × currentPrice  
investedAmount = quantity × averageBuyPrice  
profitLoss = currentValue − investedAmount  

BigDecimal is used to prevent floating-point precision errors.

---

##  AI Integration

Users can ask portfolio-related questions via:

POST /api/ai/ask

The system:
- Reads user portfolio
- Builds contextual prompt
- Sends to Ollama (LLaMA3)
- Returns AI-generated financial insight

---

##  Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT (JJWT)
- PostgreSQL
- RestTemplate
- CoinGecko API
- Ollama (LLaMA3)
- Maven

---

##  How To Run

### 1. Start PostgreSQL
Create database:
tradeverse_db

### 2. Run Auth Service
Port: 8081

### 3. Run Trading Service
Port: 8082

### 4. Start Ollama
ollama run llama3

---

##  Example API Calls

### Login
POST /api/auth/login

### Buy BTC
POST /api/trade/buy

### View Portfolio
GET /api/trade/portfolio

### Ask AI
POST /api/ai/ask

---

##  Future Improvements

- Separate database per microservice
- Docker containerization
- API Gateway
- Refresh tokens
- Role-based access control

---

##  Author

Built as a backend-focused microservices project demonstrating secure authentication, financial modeling, and AI integration.


