package qa.autogate.tests.api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;
import qa.autogate.tests.BaseApiTest;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Feature("Products API")
public class ProductApiTest extends BaseApiTest {

    @Test(description = "Verify GET /api/products returns product array conforming to schema")
    @Description("Verify products endpoint returns HTTP 200 with all products matching json schema")
    public void testGetAllProductsAndSchema() {
        given()
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(6))
                .body(matchesJsonSchemaInClasspath("schemas/products-schema.json"));
    }

    @Test(description = "Verify GET /api/products/:id returns specific product details")
    @Description("Fetch product ID 1 and verify title, category and schema")
    public void testGetProductByIdSuccess() {
        given()
                .when()
                .get("/api/products/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("UltraSync Smartwatch"))
                .body("category", equalTo("Wearables"))
                .body(matchesJsonSchemaInClasspath("schemas/product-schema.json"));
    }

    @Test(description = "Verify GET /api/products/:id returns 404 for non-existent product")
    @Description("Request non-existent product ID 9999 and assert 404 Not Found")
    public void testGetProductNotFound() {
        given()
                .when()
                .get("/api/products/9999")
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"))
                .body(matchesJsonSchemaInClasspath("schemas/error-schema.json"));
    }
}
