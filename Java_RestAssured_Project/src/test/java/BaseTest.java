import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.baseURI;

public class BaseTest {

    protected static final String BASE_URI = "https://simple-grocery-store-api.glitch.me";

    @BeforeClass
    public void setupEnvironment() {
        baseURI = BASE_URI;
    }

}
