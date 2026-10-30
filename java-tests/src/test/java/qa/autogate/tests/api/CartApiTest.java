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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@Feature("Cart API")
public class CartApiTest extends BaseApiTest {

    @Test(description = "Verify GET /api/cart without Authorization header returns 401 Unauthorized")
    @Description("Access cart without JWT token and verify 401 response and error message")
    public void testCartRequiresAuthentication() {
        given()
                .when()
                .get("/api/cart")
                .then()
                .statusCode(401)
                .body("error", equalTo("Unauthorized"))
                .body(matchesJsonSchemaInClasspath("schemas/error-schema.json"));
    }

    @Test(description = "Verify POST /api/cart adds product and updates totals")
    @Description("Add product 2 with quantity 2 and verify cart response payload")
    public void testAddItemToCartAndVerifyTotals() {
        String token = getDefaultUserToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("productId", 2, "quantity", 2))
                .when()
                .post("/api/cart")
                .then()
                .statusCode(200)
                .body("message", equalTo("Item added to cart"))
                .body("itemCount", greaterThanOrEqualTo(2));
    }

    @Test(description = "Verify POST /api/cart with quantity > 10 returns 400 Bad Request")
    @Description("Add product with quantity 15 (boundary > 10) and assert 400 error")
    public void testCartQuantityBoundaryViolation() {
        String token = getDefaultUserToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("productId", 1, "quantity", 15))
                .when()
                .post("/api/cart")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation Error"))
                .body("message", equalTo("Quantity must be an integer between 1 and 10"));
    }

    @Test(description = "Verify DELETE /api/cart/:productId removes item")
    @Description("Add product then delete it and verify item is removed")
    public void testRemoveItemFromCart() {
        String token = getDefaultUserToken();

        // First ensure product 3 is in cart
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("productId", 3, "quantity", 1))
                .post("/api/cart");

        // Delete product 3
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/cart/3")
                .then()
                .statusCode(200)
                .body("message", equalTo("Item removed from cart"));
    }
}
