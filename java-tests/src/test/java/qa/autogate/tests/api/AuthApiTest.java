package qa.autogate.tests.api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import qa.autogate.tests.BaseApiTest;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Authentication API")
public class AuthApiTest extends BaseApiTest {

    @Test(description = "Verify successful login returns 200 and matches JSON schema")
    @Description("POST /api/auth/login with valid credentials, validate JWT token presence and schema compliance")
    public void testLoginSuccessAndSchema() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", "tester@autogate.qa",
                        "password", "Tester@123"
                ))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("user.email", equalTo("tester@autogate.qa"))
                .body("user.role", equalTo("customer"))
                .body(matchesJsonSchemaInClasspath("schemas/login-response-schema.json"));
    }

    @Test(description = "Verify login with invalid password returns 401 Unauthorized")
    @Description("POST /api/auth/login with wrong password returns 401 and error schema")
    public void testLoginUnauthorizedInvalidPassword() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", "tester@autogate.qa",
                        "password", "WrongSecret!"
                ))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("error", equalTo("Unauthorized"))
                .body("message", equalTo("Invalid email or password"))
                .body(matchesJsonSchemaInClasspath("schemas/error-schema.json"));
    }

    @Test(description = "Verify login with missing email returns 400 Bad Request")
    @Description("POST /api/auth/login without email returns 400 validation error")
    public void testLoginBadRequestMissingFields() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("password", "Tester@123"))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .body(matchesJsonSchemaInClasspath("schemas/error-schema.json"));
    }
}
