#  Payment Processing System

## Overview
This project is a microservices-based architecture for managing user accounts and transactions.

---

## Services:
* Account Service - Manages user-related data.
* Payment Service - Handles transactions and payments.
* Risk Service - Handles checks.
* API Gateway - Gateway for routing requests.
* Discovery Service - Service registry (Eureka).

--- 

## Setup Instructions
1. Clone the repository:
```
https://github.com/nurananacafova/Microservice-Payment-Processing-System.git
```
2. Go to root folder and run docker-compose.yaml file:
```
cd Payment-Processing-System
docker-compose up
```

3. Start the **Discovery Service** (`http://localhost:8761`).

4. Start the:

   - **Account Service**(`http://localhost:8081`);

   - **Payment Service** (`http://localhost:8082`);

   - **Risk Service**(`http://localhost:8083`);

5. Start the **API Gateway** (`http://localhost:8080`).

---

#### discovery service: http://localhost:8761/
#### account-service: http://localhost:8081/swagger-ui/index.html#/
#### payment-service: http://localhost:8082/swagger-ui/index.html#/
#### risk-service: http://localhost:8083/swagger-ui/index.html#/
#### postgre: http://localhost:5050/browser/
#### mongodb: http://localhost:8085/

---

## Request Examples:
1. Create Account:

POST http://localhost:8081/accounts/
```
{
  "userId": 1,
  "accountNumber": "Account1",
  "balances": {
    "USD": 1000,
    "EUR": 1000,
    "AZN": 1000
  }
}
```
2. Send Payment Request:

POST http://localhost:8082/payments
```
{
  "fromAccount": "fromAccount",
  "toAccount": "toAccount",
  "currency": "currency",
  "amount": amount,
  "description": "description",
  "transactionId": "transactionId"
}
```

---

## Built With

- Spring Boot
- Java 17+
- Gradle
- PostgresSQL
- MongoDb
- Liquibase
- Docker



