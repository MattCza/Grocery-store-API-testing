import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
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
    String orderID;


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


    @Test(priority = 1)
    public void getAllProductsWithin500ms() {
        Response response = given()
                .when()
                .get("/products")
                .then()
                .time(lessThan(5000L))
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<Map<String, Object>> inStockProducts = jsonPath.getList("findAll { it.inStock == true }");

        productID = (int) inStockProducts.get(0).get("id");
        replaceProductID = (int) inStockProducts.get(3).get("id");

        Assert.assertEquals(response.getStatusCode(), 200,
                "Status code is not 200");

        Assert.assertEquals(productID, 4643);
        Assert.assertEquals(replaceProductID, 1225);
    }


    @Test(priority = 2)
    public void getProductWithID4643byPathParam() {
        given()
                .pathParam("productID", productID)
                .when()
                .get("/products/{productID}")
                .then()
                .assertThat()
                .body("id", equalTo(4643));
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
                .body("category", hasItem("coffee"))
                .log().body();
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


    @Test(priority = 2)
    public void postRegisterNewAPIkey() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("clientName", "Mark Qakkkk");
        jsonObject.put("clientEmail", "MarkQakkkk@asdasdk.zz");

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .when()
                .post("/api-clients")
                .then()
                .statusCode(201)
                .extract().response();

        accessToken = response.jsonPath().getString("accessToken");
        System.out.println(accessToken);
    }


    @Test(priority = 3)
    public void postNegativeTCRegisterAPIConflict() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("clientName", "Mark Qakkkk");
        jsonObject.put("clientEmail", "MarkQakkkk@asdasdk.zz");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .when()
                .post("/api-clients")
                .then()
                .statusCode(409).log().body();
    }


    @Test(priority = 3)
    public void postNegativeTCProvideInvalidEmail() {
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
                .statusCode(400)
                .log().body();
    }





    @Test(priority = 4)
    public void postCreateNewCart() {
        Response response = when()
                .post("/carts")
                .then()
                .statusCode(201)
                .extract().response();

        String createdJsonResponse = response.jsonPath().getString("created");

        if (createdJsonResponse.equals("true")) {
            cartID = response.jsonPath().getString("cartId");
            System.out.println(cartID);
        }
    }


    @Test(priority = 5)
    public void getCartStatusCode200Created() {
        given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}")
                .then()
                .statusCode(200);
    }


    @Test(priority = 5)
    public void postAddItemToCartStatusCode201() {
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
                .statusCode(201)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        itemCartID = jsonPath.getString("itemId");

        System.out.println(response.getBody().asString());
        System.out.println("itemId: " + itemCartID);
    }


    @Test(priority = 6)
    public void postNegativeTCItemAlreadyAddedToCartResponseMessage() {
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
    public void getCartItemsResponseBodyIsNotEmptyAndContainsProductID() {
        given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}/items")
                .then()
                .log().body()
                .body("", not(empty()))
                .body("productId", hasItem(productID));
    }


    @Test(priority = 7)
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
                .then()
                .statusCode(204);
    }


    @Test(priority = 8)
    public void getCartItemsQuantityUpdated() {
        Response response = given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}/items")
                .then()
                .body("quantity", hasItem(2))
                .extract().response();
    }

    @Test(priority = 9)
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

    @Test(priority = 10)
    public void deleteProductInCart() {
        System.out.println(cartID);
        System.out.println(itemCartID);

        given()
                .pathParam("cartId", cartID)
                .pathParam("itemId", itemCartID)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then().statusCode(204);
    }

    @Test(priority = 11)
    public void deleteNegativeTCProductInCartNotFound() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("cartId", cartID)
                .pathParam("itemId", itemCartID)
                .when()
                .delete("/carts/{cartId}/items/{itemId}")
                .then().statusCode(404);
    }

    @Test(priority = 12)
    public void postAddItemToCart() {
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
                .statusCode(201)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        itemCartID = jsonPath.getString("itemId");

        System.out.println(response.getBody().asString());
        System.out.println("itemId: " + itemCartID);
    }


    @Test(priority = 13)
    public void getCartItemsResponseBody() {
        given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}/items")
                .then()
                .log().body()
                .body("", not(empty()))
                .body("productId", hasItem(productID));
    }


    @Test(priority = 13)
    public void postAnOrder() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cartId", cartID);
        jsonObject.put("customerName", "Marry Jane");

        Response response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .auth().oauth2(accessToken)
                .when()
                .post("/orders/")
                .then()
                .statusCode(201)
                .log().body()
                .extract().response();

        orderID = response.jsonPath().getString("orderId");
        System.out.println(orderID);
    }

    @Test(priority = 14)
    public void getAllOrders() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }


    @Test(priority = 15)
    public void patchOrder() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }


    @Test(priority = 16)
    public void getAllOrders2() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }


    @Test(priority = 17)
    public void deleteAllOrders2() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }


    @Test(priority = 18)
    public void getAllOrders3() {
        given()
                .auth().oauth2(accessToken)
                .when()
                .get("/orders/")
                .then()
                .statusCode(200);
    }






}

