package qa.autogate.tests.api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import qa.autogate.tests.BaseApiTest;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Feature("Orders API")
public class OrderApiTest extends BaseApiTest {

    @Test(description = "Verify POST /api/orders creates order, returns 201 and matches JSON schema")
    @Description("Create order with shipping and card payment, assert 201 status and order schema")
    public void testCreateOrderWithCardPaymentAndSchema() {
        String token = getDefaultUserToken();

        Map<String, Object> orderPayload = Map.of(
                "shippingAddress", Map.of(
                        "fullName", "API Tester",
                        "addressLine", "404 Route Boulevard",
                        "city", "Hyderabad",
                        "pincode", "500081"
                ),
                "paymentMethod", "CARD",
                "items", List.of(
                        Map.of("productId", 1, "quantity", 1),
                        Map.of("productId", 4, "quantity", 2)
                )
        );

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(orderPayload)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(201)
                .body("orderNumber", notNullValue())
                .body("status", equalTo("CONFIRMED"))
                .body("customerName", equalTo("API Tester"))
                .body(matchesJsonSchemaInClasspath("schemas/order-schema.json"));
    }

    @Test(description = "Verify POST /api/orders with invalid pincode boundary returns 422 Unprocessable Entity")
    @Description("Submit order with 7-digit pincode (boundary length + 1) and assert 422 error")
    public void testCreateOrderValidationInvalidPincode() {
        String token = getDefaultUserToken();

        Map<String, Object> orderPayload = Map.of(
                "shippingAddress", Map.of(
                        "fullName", "API Tester",
                        "addressLine", "404 Route Boulevard",
                        "city", "Hyderabad",
                        "pincode", "5000081" // 7 digits
                ),
                "paymentMethod", "UPI",
                "items", List.of(
                        Map.of("productId", 1, "quantity", 1)
                )
        );

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(orderPayload)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(422)
                .body("error", equalTo("Validation Error"))
                .body("field", equalTo("pincode"))
                .body("message", equalTo("Pincode must be exactly 6 numeric digits"))
                .body(matchesJsonSchemaInClasspath("schemas/error-schema.json"));
    }

    @Test(description = "Verify POST /api/orders with empty items list returns 400 Bad Request")
    @Description("Submit order with empty items array and assert 400 error")
    public void testCreateOrderValidationEmptyItems() {
        String token = getDefaultUserToken();

        Map<String, Object> orderPayload = Map.of(
                "shippingAddress", Map.of(
                        "fullName", "API Tester",
                        "addressLine", "404 Route Boulevard",
                        "city", "Hyderabad",
                        "pincode", "500081"
                ),
                "paymentMethod", "COD",
                "items", List.of()
        );

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(orderPayload)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation Error"));
    }

    @Test(description = "Verify GET /api/orders/:id retrieves order by ID")
    @Description("Create order then fetch by ID and assert data consistency")
    public void testGetOrderById() {
        String token = getDefaultUserToken();

        Map<String, Object> orderPayload = Map.of(
                "shippingAddress", Map.of(
                        "fullName", "Alice Fetch",
                        "addressLine", "100 Query Street",
                        "city", "Pune",
                        "pincode", "411001"
                ),
                "paymentMethod", "UPI",
                "items", List.of(
                        Map.of("productId", 3, "quantity", 1)
                )
        );

        int orderId = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(orderPayload)
                .post("/api/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/orders/" + orderId)
                .then()
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("customerName", equalTo("Alice Fetch"))
                .body(matchesJsonSchemaInClasspath("schemas/order-schema.json"));
    }
}
