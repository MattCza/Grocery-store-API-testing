# Grocery-store-API-testing in Postman, REST Assured, Requests    
Ta kolekcja Postmana zawiera zestaw testów API dla sklepu spożywczego, który umożliwia składanie zamówień na produkty spożywcze z odbiorem w sklepie. Testy obejmują różne endpointy API i sprawdzają ich funkcjonalność, wydajność oraz poprawność odpowiedzi. Poniżej znajduje się opis poszczególnych testów.   

Technologies Used

    Postman: For API testing and automation.

    JavaScript: For writing test scripts in Postman.

    REST API: For interacting with the grocery store API.


Grocery store testing with 3 different Techs:  

How to run: Postman + newman-reporter-htmlextra  
```
Install: 
https://www.npmjs.com/package/newman-reporter-htmlextra  

run:
newman run "Grocery store API testing.postman_collection.json" -r htmlextra
```

📑 Table of Contents

    🎯 Objective

    🛠 Tools & Technologies Used

    🔗 API Endpoints Tested

    ✅ Test Scenarios

        🟢 Status Code Verification

        🔍 Fetching Product Data

        🛒 Cart Management

        📦 Order Placement & Management

    🔍 JSON Schema Validation

    ⚙️ Test Execution Order

    📌 Example Requests

    🚀 Next Steps

🎯 Objective

The purpose of this project is to test the Grocery Store API using Postman to validate its functionality, response codes, and data integrity. The tests cover GET, POST, PUT, PATCH, and DELETE requests to ensure that the API behaves as expected for product retrieval, cart management, and order placement.

🚀 The API is hosted at https://simple-grocery-store-api.glitch.me.
🛠 Tools & Technologies Used

    Postman – for API testing and automation

    JavaScript – for writing test scripts in Postman

    REST API – for interacting with the grocery store API

    JSON Schema – for validating API responses

🔗 API Endpoints Tested

    GET /status – Check server status

    GET /products – Fetch all products

    GET /products/:productId – Fetch a product by ID

    GET /products?category=coffee – Fetch products by category

    POST /api-clients – Register a new API client

    POST /carts – Create a new cart

    POST /carts/:cartId/items – Add an item to the cart

    GET /carts/:cartId/items – Fetch items in the cart

    PATCH /carts/:cartId/items/:itemId – Update item quantity in the cart

    PUT /carts/:cartId/items/:itemId – Replace an item in the cart

    DELETE /carts/:cartId/items/:itemId – Remove an item from the cart

    POST /orders – Place an order

    GET /orders – Fetch all orders

    PATCH /orders/:orderId – Modify an order

    DELETE /orders/:orderId – Delete an order

✅ Test Scenarios
🟢 Status Code Verification

✔️ Ensures that the API returns 200 OK for valid requests.
✔️ Verifies that invalid requests return expected errors (e.g., 404 Not Found).
🔍 Fetching Product Data

✔️ Retrieves all products and verifies that they are in stock.
✔️ Fetches a specific product by ID and validates its details.
✔️ Filters products by category (e.g., "coffee") and ensures all returned products belong to the specified category.
🛒 Cart Management

✔️ Creates a new cart and saves the cart ID for further use.
✔️ Adds a product to the cart and verifies that the item is successfully added.
✔️ Updates the quantity of an item in the cart and validates the change.
✔️ Replaces an item in the cart with another product.
✔️ Removes an item from the cart and ensures it is no longer present.
📦 Order Placement & Management

✔️ Places an order and saves the order ID for further use.
✔️ Retrieves all orders and verifies that the list is not empty.
✔️ Modifies an existing order (e.g., updates customer name and comments).
✔️ Deletes an order and ensures it is removed from the system.

⚙️ Test Execution Order

The tests are designed to run sequentially, with each test depending on the output of the previous one. For example:

    GET /status – Verify the server is up.

    GET /products – Fetch products and save IDs for later use.

    POST /api-clients – Register a new client and save the access token.

    POST /carts – Create a new cart and save the cart ID.

    POST /carts/:cartId/items – Add a product to the cart.

    POST /orders – Place an order and save the order ID.

    DELETE /orders/:orderId – Clean up by deleting the order.

📌 Example Requests
POST /api-clients – Register a New Client

📩 Request Body:

🚀 Next Steps

✅ Expand test coverage with more complex assertions.
✅ Implement parameterized tests for dynamic data validation.
✅ Integrate tests with CI/CD pipelines to automate execution.
✅ Enhance JSON Schema validation for more structured testing.
✅ Add tests for edge cases (e.g., out-of-stock products, invalid inputs).
✅ Extend the collection to include more API endpoints (e.g., user management).






Java -RestAssured  
📌 Grocery Store API Testing with Rest Assured
📑 Table of Contents

    🎯 Objective

    🛠 Tools & Technologies Used

    🔗 API Endpoints Tested

    ✅ Test Scenarios

        🟢 Status Code Verification

        🔍 Fetching Product Data

        ➕ Registering a New API Client

        🛒 Cart Management

        📦 Order Management

        🧹 Cleanup and Negative Test Cases

    ⚙️ Test Execution Order with @Priority

    📌 Example Requests

    🚀 Next Steps

🎯 Objective

The purpose of this project is to test the Grocery Store API using Rest Assured to validate its functionality, response codes, and data integrity. The tests cover GET, POST, PATCH, PUT, and DELETE requests to ensure the API behaves as expected in various scenarios, including cart management, order placement, and user registration.
🛠 Tools & Technologies Used

    Rest Assured – for API testing

    TestNG – for test execution and prioritization

    JSON Simple – for creating request bodies

    Hamcrest Matchers – for response validation

    OAuth2 – for authentication in order management

🔗 API Endpoints Tested

    GET /status – Check API status

    GET /products – Fetch all products

    GET /products/{id} – Fetch a product by ID

    POST /api-clients – Register a new API client

    POST /carts – Create a new cart

    POST /carts/{cartId}/items – Add items to the cart

    PATCH /carts/{cartId}/items/{itemId} – Update item quantity in the cart

    PUT /carts/{cartId}/items/{itemId} – Replace an item in the cart

    DELETE /carts/{cartId}/items/{itemId} – Remove an item from the cart

    POST /orders – Place an order

    PATCH /orders/{orderId} – Update an order

    DELETE /orders/{orderId} – Delete an order

✅ Test Scenarios
🟢 Status Code Verification

✔️ Verify that the API returns 200 OK for valid requests.
✔️ Ensure that invalid requests return expected errors (e.g., 404 Not Found, 400 Bad Request).
🔍 Fetching Product Data

✔️ Retrieve all products and validate response time (less than 5000ms).
✔️ Fetch a specific product by ID and validate its details.
✔️ Filter products by category (e.g., "coffee", "fresh-produce") and limit results.
➕ Registering a New API Client

✔️ Register a new API client and generate an access token.
✔️ Validate error responses for duplicate registration and invalid email formats.
🛒 Cart Management

✔️ Create a new cart and verify its creation.
✔️ Add items to the cart and validate the response.
✔️ Update item quantity and replace items in the cart.
✔️ Remove items from the cart and verify deletion.
📦 Order Management

✔️ Place an order using the cart and validate the response.
✔️ Update order details (e.g., customer name, comments).
✔️ Delete an order and verify its removal.
🧹 Cleanup and Negative Test Cases

✔️ Clean up the test environment by deleting carts and orders.
✔️ Test negative scenarios (e.g., adding duplicate items, deleting non-existent orders).
⚙️ Test Execution Order with @Priority

To ensure proper test execution, the @Test(priority = X) annotation is used:

    Priority 1: Verify API status and fetch product data.

    Priority 2: Register a new API client and validate responses.

    Priority 3-7: Manage cart operations (create, add items, update, delete).

    Priority 8-13: Place and manage orders.

    Priority 14-18: Clean up and perform negative test cases.

📌 Example Requests
POST /api-clients – Register a New API Client

📩 Request Body:
json
Copy

{
  "clientName": "Timothy Lang",
  "clientEmail": "TimothyLang@gmaasd.px"
}

POST /carts/{cartId}/items – Add an Item to the Cart

📩 Request Body:
json
Copy

{
  "productId": 4643
}

PATCH /orders/{orderId} – Update an Order

📩 Request Body:
json
Copy

{
  "customerName": "Joe Doe",
  "comment": "Pick-up at 4pm"
}

🚀 Next Steps

✅ Expand test coverage with more complex assertions.
✅ Implement parameterized tests for dynamic data validation.
✅ Integrate tests with CI/CD pipelines for automated execution.
✅ Add support for additional API endpoints (e.g., product reviews, promotions).
✅ Enhance error handling and logging for better debugging.
✅ Include performance testing to validate API scalability.
⚙️ Running the Tests

    Clone the repository.

    Install dependencies (Rest Assured, TestNG, etc.).

    Run the test suite using TestNG.

    Review test results and logs for any failures or issues.

This structure provides a clear and concise overview of your project, making it easy for others to understand its purpose, functionality, and future improvements. 🚀


Python - Requests  
