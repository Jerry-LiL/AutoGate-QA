package qa.autogate.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeSuite;
import qa.autogate.config.QaConfig;

import java.util.Map;

public abstract class BaseApiTest {

    @BeforeSuite(alwaysRun = true)
    public void configureRestAssured() {
        RestAssured.baseURI = QaConfig.getBaseUrl();
    }

    protected String getAuthToken(String email, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", password))
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected String getDefaultUserToken() {
        return getAuthToken("tester@autogate.qa", "Tester@123");
    }
}
