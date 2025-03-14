# **Automated Testing of Grocery Store API**

## **Table of Contents**
1. [Project Description](#project-description)
2. [Technologies and Tools Used](#technologies-and-tools-used)
3. [Test Coverage](#test-coverage)
4. [Key Features](#key-features)
5. [Test Cases](#test-cases)
   - [Status Endpoint](#1-status-endpoint)
   - [Product Endpoints](#2-product-endpoints)
   - [Authentication](#3-authentication)
   - [Cart Management](#4-cart-management)
   - [Order Management](#5-order-management)
6. [Upcoming Work](#upcoming-work)

---

## **Project Description**
This project focuses on automating the testing of a Grocery Store API using **Java (RestAssured)**, **Postman**, and **Python (requests)**.  
The goal is to ensure that various endpoints of the API function correctly across different testing tools, providing a robust test suite that can be run regularly to catch any issues or regressions.  

The project covers:
- **Endpoint validation**: Ensuring correct status codes and responses.
- **Data validation**: Verifying the accuracy of returned data.
- **Authentication**: Testing API key registration and usage.
- **Cart and Order Management**: Testing creation, updating, and deletion of carts and orders.

---

## **Technologies and Tools Used**
1. **Java**:
   - **RestAssured**: For API testing and validation.
   - **TestNG**: For test execution and reporting.
   - **Maven**: For dependency management.
2. **Postman**:
   - **Collections**: For organizing and running API tests.
   - **Pre-request Scripts and Tests**: For dynamic data handling and assertions.
3. **Python**:
   - **requests**: For sending HTTP requests and handling responses.
   - **pytest**: For test execution and reporting.

---

## **Test Coverage**
The tests cover the following functionalities across all three technologies:
1. **Status Endpoint**: Verify the API is up and running.
2. **Product Endpoints**:
   - Fetch all products.
   - Fetch products by category.
   - Fetch a specific product by ID.
3. **Authentication**:
   - Register a new API key.
   - Handle conflicts and invalid data during registration.
4. **Cart Management**:
   - Create a new cart.
   - Add, update, and delete items in the cart.
5. **Order Management**:
   - Place a new order.
   - Update and delete orders.

---

## **Key Features**
- **Cross-Technology Testing**: Tests are implemented in Java (RestAssured), Postman, and Python (requests) to ensure consistency and reliability.
- **Reusable Components**: Helper methods and utilities are created to reduce code duplication.
- **Data-Driven Testing**: Dynamic data handling for API keys, cart IDs, and order IDs.
- **Validation**: Extensive validation of status codes, response bodies, and error messages.

---

## **Test Cases**
### **1. Status Endpoint**
- **Objective**: Verify the API status.
- **Technologies**:
  - Java: `given().when().get("/status").then().statusCode(200).body("status", equalTo("UP"));`
  - Postman: GET request to `/status` with assertions for status code and response body.
  - Python: `requests.get("/status")` with assertions for status code and JSON response.

### **2. Product Endpoints**
- **Objective**: Fetch and validate product data.
- **Technologies**:
  - Java: RestAssured queries with JSONPath for data extraction.
  - Postman: Collection runner with dynamic assertions.
  - Python: `requests.get()` with `pytest` assertions.

### **3. Authentication**
- **Objective**: Register and validate API keys.
- **Technologies**:
  - Java: POST requests with JSON payloads and error handling.
  - Postman: Pre-request scripts for dynamic data and tests for response validation.
  - Python: `requests.post()` with error handling and assertions.

### **4. Cart Management**
- **Objective**: Create, update, and delete carts and items.
- **Technologies**:
  - Java: CRUD operations using RestAssured.
  - Postman: Chained requests with variables for cart and item IDs.
  - Python: `requests` for CRUD operations with `pytest` validation.

### **5. Order Management**
- **Objective**: Place, update, and delete orders.
- **Technologies**:
  - Java: OAuth2 token handling and order operations.
  - Postman: Token management and order workflows.
  - Python: `requests` with token handling and order operations.

---

## **Upcoming Work**
- **Performance Testing**: Integrate tools like JMeter or k6 for load testing.
- **CI/CD Integration**: Automate test execution using Jenkins or GitHub Actions.
- **Reporting**: Generate detailed test reports for all three technologies.
- **Cross-Browser/Platform Testing**: Extend testing to different environments and configurations.  

---

This project demonstrates the versatility of API testing across multiple technologies, ensuring comprehensive coverage and reliability.
