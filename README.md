# Pesacore (PesaCore Backend)

A robust, transactional Fintech backend built with **Kotlin** and **Spring Boot**. This system handles atomic financial transactions (Deposits, Withdrawals, P2P Transfers) with double-entry accounting principles, audit trails for failed transactions, and strict "Guard Clause" validation logic.

## Key Features

- **Atomic Transactions:** Uses `@Transactional` to ensure ACID compliance. Money never vanishes; it is either safely transferred or the entire operation rolls back.
- **Audit Trail:** Implements a "No-Rollback" policy for specific exceptions (`InsufficientFunds`, `InvalidAmount`), ensuring that **failed attempts are logged** in the database for security and support auditing.
- **Double-Entry Ledger:** Every transfer creates two transaction records (Sender Debit + Receiver Credit) to ensure accounting consistency.
- **Privacy-First API:** Transfer responses return full details to the sender but mask sensitive receiver data (e.g., balance) using dedicated DTOs (`RecipientResponse`).
- **Robust Error Handling:** A centralized `GlobalExceptionHandler` converts both Validation errors (`@Valid`) and Business logic errors into a consistent, clean JSON format.

## Tech Stack

- **Language:** Kotlin (JVM)
- **Framework:** Spring Boot 4 (Web, JPA, Validation)
- **Database:** PostgreSQL
- **Build Tool:** Gradle(Kotlin DSL)
- **Architecture:** Layered Architecture (Controller → Service → Repository) with extensive DTO mapping.

## API Endpoints

### Accounts

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | /api/v1/accounts | Create a new account |
| GET | /api/v1/accounts/{id} | Get account details |

### Transactions

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | /.../deposit | Deposit funds |
| POST | /.../withdraw | Withdraw funds |
| POST | /.../transfer | P2P Transfer |

## Known Limitations & Roadmap

While the core banking ledger is ACID-compliant and supports atomic transfers, the following features were excluded 
from the MVP scope to focus on architectural constraints:
 - Transaction Fees: Currently, all transfers are zero-fee. A Tariff Engine (configurable fees based on tiers) is 
planned for v2.
 - Currency Conversion: Multi-currency transfers assume a 1:1 exchange rate currently.
 - Idempotency Keys: To prevent double-spending on network retries.
 - Authentication: The system currently assumes an authenticated context; JWT/OAuth2 integration is the next immediate 
   step.

## Local Setup

```bash
git clone https://github.com/devcavin/pesacore.git
cd pesacore

./gradlew bootRun # Linux/MacOS
gradlew bootRun # Windows(Powershell/CMD)

# run compose
docker-compose up --build
docker-compose up

podman-compose up --build
podman-compose up

# Build Docker image
docker build -t pesacore:latest . # docker build --network=host -t pesacore . # for networking issues

# podman
podman build -t pesacore:latest .

# Run locally
docker run -p 8080:8080 pesacore:latest
podman run -p 8080:8080 pesacore:latest # for podman

curl http://localhost:8080
```
