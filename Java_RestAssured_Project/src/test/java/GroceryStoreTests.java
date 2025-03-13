import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GroceryStoreTests extends BaseTest{

    private int productId;
    private int replaceProductId;
    private final String FULL_NAME = "Timothy Lang1";
    private final String EMAIL = "TimothyLang1@gmaasd.px";
    private String accessToken;
    private String cartId;
    private String itemCartId;
    private String orderId;


    private Response sendPostRequest(String endpoint, JSONObject requestBody) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody.toJSONString())
                .when()
                .post(endpoint)
                .then()
                .extract().response();
    }

    private Response sendGetRequest(String endpoint, Map<String, Object> queryParams) {
        return given()
                .queryParams(queryParams)
                .when()
                .get(endpoint)
                .then()
                .extract().response();
    }

    private String extractJsonValue(Response response, String jsonPath) {
        return response.jsonPath().getString(jsonPath);
    }


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


    @Test(priority = 2)
    public void testGetProductById() {
        given()
                .pathParam("productId", productId)
                .when()
                .get("/products/{productId}")
                .then()
                .assertThat()
                .body("id", equalTo(productId));
    }


    @Test(priority = 2)
    public void testGetAllProductsByCategoryCoffee() {
        given()
                .queryParam("category", "coffee")
                .when()
                .get("/products")
                .then()
                .assertThat()
                .body("category", hasItem("coffee"))
                .log().body();
    }

    @Test(priority = 2)
    public void testGetAllProductsWithCategoryFreshProduceLimitedTo3() {
        given()
                .queryParams("category", "fresh-produce",
                        "results", "3",
                        "available", "true")
                .when()
                .get("/products")
                .then()
                .assertThat()
                .body("size()", equalTo(3))
                .log().body();
    }


    @Test(priority = 2)
    public void testRegisterNewApiKey() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("clientName", FULL_NAME);
        requestBody.put("clientEmail", EMAIL);

        Response response = sendPostRequest("/api-clients", requestBody);
        Assert.assertEquals(response.getStatusCode(), 201);
        accessToken = extractJsonValue(response, "accessToken");
    }


    @Test(priority = 3)
    public void testRegisterApiKeyConflict() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("clientName", FULL_NAME);
        requestBody.put("clientEmail", EMAIL);

        Response response = sendPostRequest("/api-clients", requestBody);
        Assert.assertEquals(response.getStatusCode(), 409);
    }

    @Test(priority = 3)
    public void testRegisterApiKeyWithInvalidEmail() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("clientName", "Mark");
        requestBody.put("clientEmail", "MarkQa");

        Response response = sendPostRequest("/api-clients", requestBody);
        Assert.assertEquals(response.getStatusCode(), 400);
    }

    @Test(priority = 3)
    public void testCreateNewCart() {
        Response response = when()
                .post("/carts")
                .then()
                .statusCode(201)
                .extract().response();

        cartId = extractJsonValue(response, "cartId");
        Assert.assertNotNull(cartId, "Cart ID should not be null");
    }


    @Test(priority = 4)
    public void testGetCartStatusCode200() {
        given()
                .pathParam("cartId", cartId)
                .when()
                .get("/carts/{cartId}")
                .then()
                .statusCode(200);
    }

    @Test(priority = 4)
    public void testAddItemToCart() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("productId", productId);

        Response response = sendPostRequest("/carts/" + cartId + "/items", requestBody);
        Assert.assertEquals(response.getStatusCode(), 201);

        itemCartId = extractJsonValue(response, "itemId");
        Assert.assertNotNull(itemCartId, "Item ID should not be null");
    }

    @Test(priority = 5)
    public void testAddItemToCartConflict() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("productId", productId);

        Response response = sendPostRequest("/carts/" + cartId + "/items", requestBody);
        String errorMessage = extractJsonValue(response, "error");
        Assert.assertEquals(errorMessage, "This product has already been added to cart.",
                "Error message does not match expected value");
    }


    @Test(priority = 6)
    public void testGetCartItems() {
        given()
                .pathParam("cartId", cartId)
                .when()
                .get("/carts/{cartId}/items")
                .then()
                .body("", not(empty()))
                .body("productId", hasItem(productId));
    }

    @Test(priority = 6)
    public void testUpdateProductQuantityInCart() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("quantity", 2);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody.toJSONString())
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemCartId)
                .when()
                .patch("/carts/{cartId}/items/{itemId}")
                .then()
                .statusCode(204);
    }


    @Test(priority = 7)
    public void testGetCartItemsAfterQuantityUpdate() {
        given()
                .pathParam("cartId", cartId)
                .when()
                .get("/carts/{cartId}/items")
                .then()
                .body("quantity", hasItem(2));
    }
//////////////////////////////////////////////////////////////
    @Test(priority = 8)
    public void testReplaceProductInCart() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("productId", replaceProductId);
        requestBody.put("quantity", 1);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody.toJSONString())
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemCartId)
                .when()
                .put("/carts/{cartId}/items/{itemId}")
                .then().statusCode(204);
    }


    @Test(priority = 9)
    public void testDeleteProductInCart() {
        given()
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemCartId)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then().statusCode(204);
    }

    @Test(priority = 10)
    public void testDeleteProductInCartNotFound() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("cartId", cartId)
                .pathParam("itemId", itemCartId)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then().statusCode(404);
    }

    @Test(priority = 11)
    public void testAddItemToCartAgain() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("productId", productId);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody.toJSONString())
                .pathParam("cartId", cartId)
                .when()
                .post("/carts/{cartId}/items")
                .then()
                .statusCode(201)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        itemCartId = jsonPath.getString("itemId");
    }

    @Test(priority = 12)
    public void testGetCartItemsAfterAdding() {
        given()
                .pathParam("cartId", cartId)
                .when()
                .get("/carts/{cartId}/items")
                .then()
                .log().body()
                .body("", not(empty()))
                .body("productId", hasItem(productId));
    }


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

    @Test(priority = 13)
    public void testGetAllOrders() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }

    @Test(priority = 14)
    public void testUpdateOrder() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("customerName", "Joe Doe");
        requestBody.put("comment", "Pick-up at 4pm");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(requestBody.toJSONString())
                .auth().oauth2(accessToken)
                .pathParam("orderId", orderId)
                .when()
                .patch("/orders/{orderId}")
                .then()
                .statusCode(204);
    }


    @Test(priority = 15)
    public void testGetAllOrdersAfterUpdate() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }

    @Test(priority = 16)
    public void testDeleteOrder() {
        given()
                .auth().oauth2(accessToken)
                .pathParam("orderId", orderId)
                .when()
                .delete("/orders/{orderId}")
                .then()
                .statusCode(204);
    }

    @Test(priority = 17)
    public void testDeleteOrderNotFound() {
        given()
                .auth().oauth2(accessToken)
                .pathParam("orderId", orderId)
                .when()
                .delete("/orders/{orderId}")
                .then()
                .statusCode(404);
    }

    @Test(priority = 18)
    public void testGetAllOrdersFinal() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }
}