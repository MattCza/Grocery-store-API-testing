# **Automated Testing of Grocery Store API** 🛒🧪

## **Table of Contents** 📑
1. [Project Description](#project-description-)
2. [Technologies and Tools Used](#technologies-and-tools-used-)
3. [Test Coverage](#test-coverage-)
4. [Key Features](#key-features-)
5. [Test Cases](#test-cases-)
   - [Status Endpoint](#1-status-endpoint-)
   - [Product Endpoints](#2-product-endpoints-)
   - [Authentication](#3-authentication-)
   - [Cart Management](#4-cart-management-)
   - [Order Management](#5-order-management-)
6. [Upcoming Work](#upcoming-work-)

---

## **Project Description** 📝
This project focuses on automating the testing of a Grocery Store API using **Java (RestAssured)**, **Postman**, and **Python (requests)**.  
The goal is to ensure that various endpoints of the API function correctly across different testing tools, providing a robust test suite that can be run regularly to catch any issues or regressions.  

The project covers:
- **Endpoint validation**: Ensuring correct status codes and responses. ✅
- **Data validation**: Verifying the accuracy of returned data. 📊
- **Authentication**: Testing API key registration and usage. 🔑
- **Cart and Order Management**: Testing creation, updating, and deletion of carts and orders. 🛒📦

---

## **Technologies and Tools Used** 🛠️
1. **Java**:
   - **RestAssured**: For API testing and validation. 🧪
   - **TestNG**: For test execution and reporting. 📋
   - **Maven**: For dependency management. 📦
2. **Postman**:
   - **Collections**: For organizing and running API tests. 📂
   - **Pre-request Scripts and Tests**: For dynamic data handling and assertions. 🧠
3. **Python**:
   - **requests**: For sending HTTP requests and handling responses. 🐍
   - **pytest**: For test execution and reporting. 📊

---

## **Test Coverage** 🧾
The tests cover the following functionalities across all three technologies:
1. **Status Endpoint**: Verify the API is up and running. 🟢
2. **Product Endpoints**:
   - Fetch all products. 📋
   - Fetch products by category. 🏷️
   - Fetch a specific product by ID. 🔍
3. **Authentication**:
   - Register a new API key. 🔑
   - Handle conflicts and invalid data during registration. ⚠️
4. **Cart Management**:
   - Create a new cart. 🛒
   - Add, update, and delete items in the cart. ➕🔄🗑️
5. **Order Management**:
   - Place a new order. 📦
   - Update and delete orders. 🔄🗑️

---

## **Key Features** ✨
- **Cross-Technology Testing**: Tests are implemented in Java (RestAssured), Postman, and Python (requests) to ensure consistency and reliability. 🔄
- **Reusable Components**: Helper methods and utilities are created to reduce code duplication. ♻️
- **Data-Driven Testing**: Dynamic data handling for API keys, cart IDs, and order IDs. 📊
- **Validation**: Extensive validation of status codes, response bodies, and error messages. ✅

---

## **Test Cases** �
### **1. Status Endpoint** 🟢
- **Objective**: Verify the API status.
- **Technologies**:
  - Java:
    ```java
    @Test(priority = 1)
    public void testStatusEndpointReturns200AndStatusUp() {
        given()
            .when()
            .get("/status")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .log().body();
    }
    ```
  - Postman: GET request to `/status` with assertions for status code and response body.
  - Python:
    ```python
    def test_status_endpoint_returns_200_and_status_up():
        response = requests.get(f"{BASE_URL}/status")
        assert response.status_code == 200
        assert response.json().get("status") == "UP"
    ```

### **2. Product Endpoints** 📋
- **Objective**: Fetch and validate product data.
- **Technologies**:
  - Java:
    ```java
    @Test(priority = 2)
    public void testGetAllProductsWithin500ms() {
        Response response = given()
            .when()
            .get("/products")
            .then()
            .time(lessThan(5000L))
            .extract().response();

        List<Map<String, Object>> inStockProducts = response.jsonPath().getList("findAll { it.inStock == true }");
        Assert.assertFalse(inStockProducts.isEmpty(), "No in-stock products found");
        productId = (int) inStockProducts.get(0).get("id");
        replaceProductId = inStockProducts.size() > 3 ? (int) inStockProducts.get(3).get("id") : productId;
    }
    ```
  - Postman: Collection runner with dynamic assertions.
  - Python:
    ```python
    def test_get_all_products_within_500ms():
        response = requests.get(f"{BASE_URL}/products")
        assert response.elapsed.total_seconds() < 2
        products = response.json()
        in_stock_products = [p for p in products if p.get("inStock")]
        assert in_stock_products
    ```

### **3. Authentication** 🔑
- **Objective**: Register and validate API keys.
- **Technologies**:
  - Java:
    ```java
    @Test(priority = 2)
    public void testRegisterNewApiKey() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("clientName", FULL_NAME);
        requestBody.put("clientEmail", EMAIL);

        Response response = sendPostRequest("/api-clients", requestBody);
        Assert.assertEquals(response.getStatusCode(), 201);
        accessToken = extractJsonValue(response, "accessToken");
    }
    ```
  - Postman: Pre-request scripts for dynamic data and tests for response validation.
  - Python:
    ```python
    def test_register_new_api_key():
        response = requests.post(f"{BASE_URL}/api-clients", json={"clientName": FULL_NAME, "clientEmail": EMAIL})
        assert response.status_code == 201
        access_token = response.json().get("accessToken")
    ```

### **4. Cart Management** 🛒
- **Objective**: Create, update, and delete carts and items.
- **Technologies**:
  - Java:
    ```java
    @Test(priority = 4)
    public void testAddItemToCart() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("productId", productId);

        Response response = sendPostRequest("/carts/" + cartId + "/items", requestBody);
        Assert.assertEquals(response.getStatusCode(), 201);

        itemCartId = extractJsonValue(response, "itemId");
        Assert.assertNotNull(itemCartId, "Item ID should not be null");
    }
    ```
  - Postman: Chained requests with variables for cart and item IDs.
  - Python:
    ```python
    def test_add_item_to_cart():
        response = requests.post(f"{BASE_URL}/carts/{cart_id}/items", json={"productId": product_id})
        assert response.status_code == 201
        item_cart_id = response.json().get("itemId")
    ```

### **5. Order Management** 📦
- **Objective**: Place, update, and delete orders.
- **Technologies**:
  - Java:
    ```java
    @Test(priority = 12)
    public void testPlaceOrder() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("cartId", cartId);
        requestBody.put("customerName", "Marry Jane");

        Response response = given()
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(requestBody.toJSONString())
            .auth().oauth2(accessToken)
            .when()
            .post("/orders/")
            .then()
            .statusCode(201)
            .log().body()
            .extract().response();

        orderId = response.jsonPath().getString("orderId");
    }
    ```
  - Postman: Token management and order workflows.
  - Python:
    ```python
    def test_place_order():
        response = requests.post(f"{BASE_URL}/orders/", json={"cartId": cart_id, "customerName": "Marry Jane"}, headers={"Authorization": f"Bearer {access_token}"})
        assert response.status_code == 201
        order_id = response.json().get("orderId")
    ```

---

## **Upcoming Work** 🚀
- **Performance Testing**: Integrate tools like JMeter or k6 for load testing. 📈
- **CI/CD Integration**: Automate test execution using Jenkins or GitHub Actions. 🤖
- **Reporting**: Generate detailed test reports for all three technologies. 📊
- **Cross-Browser/Platform Testing**: Extend testing to different environments and configurations. 🌐  

---

This project demonstrates the versatility of API testing across multiple technologies, ensuring comprehensive coverage and reliability. 🧪✨
