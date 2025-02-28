import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class GroceryStoreTests {


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
    public void getAllProductsResponseTimeLessThan5000mls() {
        given()
                .when()
                .get("/products")
                .then()
                .time(Matchers.lessThan(5000L));
    }


}
