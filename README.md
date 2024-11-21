# URL Shortener & Redirect Service

This project consists of two main components: a URL shortener service and a redirect service implemented in Java. The system allows creating shortened URLs and handling URL redirections efficiently.

## Project Structure

The project is divided into two main modules:

### 1. Shortener Module
- Handles URL shortening functionality
- Located in `/shortener` directory
- Contains controllers and services for URL shortening operations

### 2. Redirect Module  
- Manages URL redirection functionality
- Located in `/redirect` directory
- Contains controllers and services for handling redirects

## Prerequisites

- Java JDK 11 or higher
- Maven
- DynamoDB Local or AWS DynamoDB access

## How to Run

### Running the Shortener Service

1. Navigate to the shortener directory:
```bash
cd shortener
```
2. Build the project:
```bash
mvn clean install
```
3. Run the Application:
```bash
mvn spring-boot:run
```
The shortener service will start on default port 8080.

### Running the Redirect Service

1. Navigate to the redirect directory:
```bash
cd redirecter
```
2. Build the project:
```bash
mvn clean install
```
3. Run the Application:
```bash
mvn spring-boot:run
```
The shortener service will start on default port 8081.

# Running Tests

### Shortener Module Tests

1. Navigate to the shortener directory:
```bash
cd shortener
```
2. Run tests:
```bash
mvn test
```

This will execute:

* UrlRedirectControllerTest
* DynamoDbServiceTest
* RedirectApplicationTests

### Redirect Module Tests

1. Navigate to the redirect directory:
```bash
cd redirecter
```
2. Run tests:
```bash
mvn test
```

This will execute:

* UrlRedirectControllerTest
* DynamoDbServiceTest
* RedirectApplicationTests

# Configuration
Both modules use DynamoDB for data persistence. Make sure to configure the following:

1. AWS credentials (if using AWS DynamoDB)
2. Database connection settings in application.properties/application.yml
3. Port configurations if needed

# Project Dependencies
* Spring Boot/Cloud
* AWS SDK for DynamoDB
* JUnit for testing
* Maven for build management
  
# Contributing

1. Fork the repository
2. Create your feature branch (git checkout -b feature/amazing-feature)
3. Commit your changes (git commit -m 'Add some amazing feature')
4. Push to the branch (git push origin feature/amazing-feature)
5. Open a Pull Request
