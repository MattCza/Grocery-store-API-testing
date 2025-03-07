import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
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

    @BeforeClass
    public void setupEnvironment() {
        baseURI = "https://simple-grocery-store-api.glitch.me";
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



}
