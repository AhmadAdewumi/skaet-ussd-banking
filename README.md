# Skaet Assessment USSD Banking Application

## Overview

This project is a USSD banking application built with Java and Spring Boot. It is designed to be scalable, stateless, and maintainable. The system simulates a core banking backend accessible through two channels:

---

## Features and Implementation Status

### Core Requirements
- [x] Create Account (Name and PIN)
- [x] Deposit
- [x] Withdraw
- [x] Check Balance
- [x] Scalability via Redis and Docker

### Extra Credit
- [x] SMS Notification with Twilio (async)
- [x] Payments REST API
- [x] Multicurrency Support via SOAP client adapter  
  Converts to USD, EUR, GBP, CAD

---

## Architecture and Design Principles

### 1. Stateless Session Management

USSD sessions are short-lived. Instead of storing session state in-memory, Redis is used to store state such as:
- WELCOME
- ENTER_PIN
- ENTER_AMOUNT


Using Redis with 2 minutes TTL to manage user sessions

---

### 2. Strategy Pattern

To avoid a massive if/else chain, the USSD flow uses the Strategy Pattern.

- `MenuHandler` is the interface
- Each screen is its own class (`DepositHandler`, `WelcomeHandler`,  etc)
- `UssdRoutingService` selects the right handler based on the current USSD state

This makes the system easy to extend.

---

### 3. Event-Driven Architecture

SMS notifications run asynchronously using Spring events.

Flow:
- Transaction happens
- `WalletService` publishes a `TransactionEvent`
- `SmsService` listens and sends SMS in a background thread

This prevents blocking the user's session.

---

### 4. Hexagonal Architecture (Ports and Adapters)

Business logic lives in `WalletService`.

Adapters:
- USSD Controller handles form data
- Payment API Controller handles JSON

Both call the same service layer to avoid duplication.

---

### 5. SOAP Integration (Adapter Pattern)

To support multicurrency, a SOAP client was added using the Adapter Pattern.

- `ExchangeRateService` is the interface
- `SoapExchangeRateAdapter` handles JAXB (Java Architecture for XML Binding) XML, SOAP envelopes, and parsing
- Redis caches exchange rates for 1 hour

---

## Technology Stack

- Java 21, Spring Boot 3.5.4
- MySQL 8
- Spring Data JPA
- Redis
- Spring Web Services (SOAP)
- Twilio SDK
- Docker and Docker Compose

---

## Setup and Installation

### Prerequisites

- Docker & Docker Compose
- Java 21+
- Maven

### Environment Variables

Create a `.env` file or export variables:

- SPRING_DATASOURCE_URL=
- SPRING_DATASOURCE_USERNAME=
- SPRING_DATASOURCE_PASSWORD=
- SPRING_DATA_REDIS_HOST=
- SPRING_DATA_REDIS_PORT=
- TWILIO_ACCOUNT_SID=
- TWILIO_AUTH_TOKEN=
- TWILIO_PHONE_NUMBER=
- APP_USSD_SESSION_TIMEOUT=


---

## Running with Docker

```sh
docker-compose up --build -d
```
## Application URL
The service runs locally at:

http://localhost:8080

---

## Testing Guide

### 1. USSD Interface (Postman)

**Endpoint**

POST http://localhost:8080/api/ussd  
Content-Type: application/x-www-form-urlencoded

**Form Parameters**

| Key         | Value Example           |
|-------------|-------------------------|
| sessionId   | any random string       |
| serviceCode | *123#                   |
| phoneNumber | +2348123456789          |
| text        | user input (e.g 1*5000) |

#### Deposit Example (NGN 5000)

text=

text=1

text=1*5000

text=1 * 5000 * 1


---

### 2. Payments REST API

#### Deposit
POST http://localhost:8080/api/v1/payments/deposit

```json
{
  "phoneNumber": "+2348000000000",
  "amount": 10000.00
}
```

#### Withdraw

POST http://localhost:8080/api/v1/payments/withdraw

```json
{
"phoneNumber": "+2348000000000",
"amount": 500.00,
"pin": "1234"
}
```

### 3. Multicurrency Check (SOAP Integration (Simulated))

Selecting option 5 triggers the SOAP Adapter.

**Example Output**

END Multicurrency Balance:
NGN 5000.0000
= EUR 5.95
(Rate: 0.00119)

---
## Conclusion

This implementation demonstrates clean architecture. By combining:

- Stateless design

- Redis caching

- Asynchronous event-driven workflows

- Proper separation of concerns

---
