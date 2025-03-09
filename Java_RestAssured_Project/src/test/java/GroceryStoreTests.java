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


    @BeforeClass
    public void setupEnvironment() {
        baseURI = "https://simple-grocery-store-api.glitch.me";
    }


    public String extractStringJsonResponse(ValidatableResponse response, String value) {
        return response.extract().jsonPath().getString(value);
    }


    @Test
    public void getStatusEndPointCode200() {
        given()
                .when()
                .get("/status")
                .then()
                .statusCode(200).log().status();
    }


    @Test
    public void getStatusEndPointResponseUP() {
        given()
                .when()
                .get("/status")
                .then()
                .body("status", equalTo("UP"))
                .log().body();
    }


    @Test
    public void getAllProductsStatusCode200() {
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200);
    }


    @Test
    public void getFirstAndFourthElement() {
        Response response = given()
                .when()
                .get("/products");

        JsonPath jsonPath = response.jsonPath();

        List<Map<String, Object>> products = jsonPath.getList("findAll { it.inStock == true }");

        Map<String, Object> product1 = products.get(0);
        Map<String, Object> product4 = products.get(3);

        productID = (int) product1.get("id");
        replaceProductID = (int) product4.get("id");

        Assert.assertEquals(productID, 4643);
        Assert.assertEquals(replaceProductID, 1225);
    }


    @Test
    public void getFirstProductFoundInStock() {
        given()
                .when()
                .get("/products")
                .then()
                .assertThat()
                .body("id", hasItem(productID));
    }


    @Test
    public void getAllProductsResponseTimeLessThan5000mls() {
        given()
                .when()
                .get("/products")
                .then()
                .time(Matchers.lessThan(5000L));
    }


    @Test
    public void getProductWithID4643byPath() {
        given()
                .pathParam("productID", productID)
                .when()
                .get("/products/{productID}")
                .then()
                .assertThat()
                .body("id", equalTo(productID));
    }


    @Test
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


    @Test
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


    @Test
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


    @Test
    public void postNegativeTCprovidedInvalidParams() {
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


    @Test
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


    @Test
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

        int productid = 4643;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("productId", productid);

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


    @Test
    public void getCartStatusCode200() {
        given()
                .pathParam("cartId", cartID)
                .when()
                .get("/carts/{cartId}")
                .then()
                .statusCode(200);
    }


    @Test
    public void postAddItemToCartStatusCode200() {
        int productid = 4643;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("productId", productid);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonObject.toJSONString())
                .pathParam("cartId", cartID)
                .when()
                .post("/carts/{cartId}/items")
                .then()
                .statusCode(201);
    }


    @Test
    public void postNegativeTCItemAlreadyAddedToCart() {
        int productid = 4643;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("productId", productid);

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












}
