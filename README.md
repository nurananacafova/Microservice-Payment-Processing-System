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
git clone https://github.com/nurananacafova/?.git
```
2. Go to root folder and run docker-compose.yaml file:
```
cd Payment-Processing-System
docker-compose up
```

[//]: # (2. Start the **Discovery Service** &#40;`http://localhost:8761`&#41;.)

[//]: # (3. Start the:)

[//]: # (   - **Account Service**&#40;`http://localhost:8081`&#41;;)

[//]: # (   - **Payment Service** &#40;`http://localhost:8082`&#41;;)

[//]: # (   - **Risk Service**&#40;`http://localhost:8083`&#41;;)

[//]: # (4. Start the **API Gateway** &#40;`http://localhost:8222`&#41;.)

---

## Built With

- Spring Boot
- Java 17+
- Gradle
- PostgresSQL
- MongoDb
- Liquibase
- Docker



* discovery service: http://localhost:8761/
* account-service: http://localhost:8081/swagger-ui/index.html#/
* payment-service: http://localhost:8082/swagger-ui/index.html#/
* risk-service: http://localhost:8083/swagger-ui/index.html#/
* api-gateway: http://localhost:8080/webjars/swagger-ui/index.html
* postgre: http://localhost:5050/browser/
* mongodb: http://localhost:8085/