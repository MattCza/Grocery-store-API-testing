import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GroceryStoreTests {

    int productID;
    int replaceProductID;
    String accessToken;
    String cartID;
    String itemCartID;
    String itemInCartID;


    @BeforeClass
    public void setupEnvironment() {
        baseURI = "https://simple-grocery-store-api.glitch.me";
    }


    public String extractStringJsonResponse(ValidatableResponse response, String value) {
        return response.extract().jsonPath().getString(value);
    }


    @Test(priority = 1)
    public void getStatusEndPointCode200andResponseBodyUP() {
        given()
                .when()
                .get("/status")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"))
                .log().body();
    }


    @Test(priority = 2)
    public void getAllProducts() {
        Response response = given()
                .when()
                .get("/products")
                .then().extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<Map<String, Object>> products = jsonPath.getList("findAll { it.inStock == true }");

        Map<String, Object> product1 = products.get(0);
        Map<String, Object> product4 = products.get(3);

        productID = (int) product1.get("id");
        replaceProductID = (int) product4.get("id");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Status code is not 200");
        Assert.assertEquals(productID, 4643);
        Assert.assertEquals(replaceProductID, 1225);
    }


    @Test(priority = 2)
    public void getFirstProductFoundInStock() {
        given()
                .when()
                .get("/products")
                .then()
                .assertThat()
                .body("id", hasItem(productID));
    }


    @Test(priority = 2)
    public void getAllProductsResponseTimeLessThan5000mls() {
        given()
                .when()
                .get("/products")
                .then()
                .time(Matchers.lessThan(5000L));
    }


    @Test(priority = 2)
    public void getProductWithID4643byPath() {
        given()
                .pathParam("productID", productID)
                .when()
                .get("/products/{productID}")
                .then()
                .assertThat()
                .body("id", equalTo(productID));
    }


    @Test(priority = 2)
    public void getAllProductWithCategoryCoffeeByQuery() {
        String categoryCoffee = "coffee";
        given()
                .queryParam("category", categoryCoffee)
                .when()
                .get("/products")
                .then()
                .assertThat()
                .body("category", hasItem("coffee")).log().body();
    }


    @Test(priority = 2)
    public void getAllProductsWithCategoryFreshProduceAvailableLimitedTo3Objects() {
        String category = "fresh-produce";
        String limit = "3";
        String available = "true";

        given()
                .queryParams("category", category,
                        "results", limit,
                        "available", available)
                .when()
                .get("/products")
                .then()
                .assertThat()
                .body("size()", equalTo(3))
                .log().body();
    }


    @Test(priority = 3)
    public void postRegisterNewAPIkey() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("clientName", "Mark Qakk");
        jsonObject.put("clientEmail", "MarkQak@asdasdk.zz");

        ValidatableResponse response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .when()
                .post("/api-clients")
                .then()
                .statusCode(201).log().body();

        accessToken = extractStringJsonResponse(response, "accessToken");
        System.out.println(accessToken);
    }


    @Test(priority = 3)
    public void postNegativeTCProvidedInvalidParams() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("clientName", "Mark");
        jsonObject.put("clientEmail", "MarkQa");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .when()
                .post("/api-clients")
                .then()
                .statusCode(400).log().body();
    }


    @Test(priority = 4)
    public void postNegativeTCConflict() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("clientName", "Mark Qakk");
        jsonObject.put("clientEmail", "MarkQak@asdasdk.zz");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .when()
                .post("/api-clients")
                .then()
                .statusCode(409).log().body();
    }


    @Test(priority = 5)
    public void postCreateNewCart() {
        ValidatableResponse response = when()
                .post("/carts")
                .then()
                .statusCode(201);

        String createdJsonResponse = extractStringJsonResponse(response, "created");

        if (createdJsonResponse.equals("true")) {
            cartID = extractStringJsonResponse(response, "cartId");
            System.out.println(cartID);
        }

        given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}")
                .then()
                .statusCode(200);
    }


    @Test(priority = 6)
    public void getCartStatusCode200() {
        given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}")
                .then()
                .statusCode(200);
    }


    @Test(priority = 6)
    public void postAddItemToCartStatusCode200() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("productId", productID);

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .pathParam("cartId", cartID)
                .when()
                .post("/carts/{cartId}/items")
                .then()
                .statusCode(201).extract().response();

        JsonPath jsonPath = response.jsonPath();
        itemCartID = jsonPath.getString("itemId");


        System.out.println(response.getBody().asString());
        System.out.println("itemId: " + itemCartID);
    }


    @Test(priority = 7)
    public void postNegativeTCItemAlreadyAddedToCart() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("productId", productID);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .pathParam("cartId", cartID)
                .when()
                .post("/carts/{cartId}/items")
                .then()
                .body("error", equalTo("This product has already been added to cart."));
    }

    @Test(priority = 7)
    public void getCartItemsResponseBodyIsNotEmpty() {
        Response response = given()
                .pathParam("cartId", cartID)
                .when()
                .post("/carts/{cartId}/items")
                .then().extract().response();



        Assert.assertFalse(response.getBody().asString().isEmpty(), "Response body is empty");
    }

    @Test(priority = 7)
    public void getCartItemsResponseContainsProductID() {

        Response response = given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}/items")
                .then().extract().response();

        JsonPath jsonPath = response.jsonPath();
        boolean containsProductId = jsonPath.getList("productId").contains(productID);

        Assert.assertTrue(containsProductId, "Response does not contain productId = " + productID);
    }

    @Test(priority = 8)
    public void patchUpdateQuantityOfProduct() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quantity", 2);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .pathParam("cartId", cartID)
                .pathParam("itemId", itemCartID)
                .when()
                .patch("/carts/{cartId}/items/{itemId}")
                .then().statusCode(204);

//        JsonPath jsonPath = response.jsonPath();
//        boolean containsProductId = jsonPath.getList("productId").contains(productID);
//
//        Assert.assertTrue(containsProductId, "Response does not contain productId = " + productID);
    }

    @Test(priority = 9)
    public void getCartItemsQuantityUpdated() {

        Response response = given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}/items")
                .then().extract().response();

        JsonPath jsonPath = response.jsonPath();
        boolean containsProductId = jsonPath.getList("quantity").contains(2);

        Assert.assertTrue(containsProductId, "Quantity not equals 2 ");
    }

    @Test(priority = 10)
    public void putReplaceProductInCart() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productId", replaceProductID);
        jsonObject.put("quantity", 1);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .pathParam("cartId", cartID)
                .pathParam("itemId", itemCartID)
                .when()
                .put("/carts/{cartId}/items/{itemId}")
                .then().statusCode(204);
    }

    @Test(priority = 11)
    public void deleteProductInCart() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("cartId", cartID)
                .pathParam("itemId", itemInCartID)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then().statusCode(204);
    }

    @Test(priority = 12)
    public void deleteNegativeTCProductInCartNotFound() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("cartId", cartID)
                .pathParam("itemId", itemInCartID)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then().statusCode(404);
    }

    @Test(priority = 12)
    public void getCartItemsQuantityUpdated2() {

        given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}/items")
                .then().log().body();

//        JsonPath jsonPath = response.jsonPath();
//        boolean containsProductId = jsonPath.getList("quantity").contains(2);
//
//        Assert.assertTrue(containsProductId, "Quantity not equals 2 ");
    }




}

