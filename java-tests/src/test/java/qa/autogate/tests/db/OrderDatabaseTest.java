package qa.autogate.tests.db;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import qa.autogate.db.DatabaseManager;
import qa.autogate.tests.BaseApiTest;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Feature("Database Persistence (SQLite JDBC)")
public class OrderDatabaseTest extends BaseApiTest {

    @Test(description = "Verify created order is persisted with exact fields in SQLite database")
    @Description("Places an order via API and executes JDBC query on 'orders' table to verify persistence")
    public void testPersistedOrderRecordMatchesInDatabase() {
        String token = getDefaultUserToken();

        Map<String, Object> orderPayload = Map.of(
                "shippingAddress", Map.of(
                        "fullName", "JDBC Validator",
                        "addressLine", "777 Database Way",
                        "city", "Chennai",
                        "pincode", "600001"
                ),
                "paymentMethod", "CARD",
                "items", List.of(
                        Map.of("productId", 5, "quantity", 2) // 49.99 * 2 = 99.98
                )
        );

        String orderNumber = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(orderPayload)
                .post("/api/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("orderNumber");

        Assert.assertNotNull(orderNumber, "Order number must be present in response");

        // Query database via JDBC
        Map<String, Object> dbOrder = DatabaseManager.getOrderByNumber(orderNumber);
        Assert.assertNotNull(dbOrder, "Order record must exist in SQLite 'orders' table");
        Assert.assertEquals(dbOrder.get("customer_name"), "JDBC Validator");
        Assert.assertEquals(dbOrder.get("shipping_pincode"), "600001");
        Assert.assertEquals(dbOrder.get("payment_method"), "CARD");
        Assert.assertEquals(dbOrder.get("status"), "CONFIRMED");
        Assert.assertEquals(((Number) dbOrder.get("total_amount")).doubleValue(), 99.98, 0.01);
    }

    @Test(description = "Verify order items integrity and foreign keys in SQLite database")
    @Description("Asserts that child records in 'order_items' table match product details and quantities")
    public void testPersistedOrderItemsIntegrity() {
        String token = getDefaultUserToken();

        Map<String, Object> orderPayload = Map.of(
                "shippingAddress", Map.of(
                        "fullName", "Integrity Checker",
                        "addressLine", "999 SQL Road",
                        "city", "Hyderabad",
                        "pincode", "500032"
                ),
                "paymentMethod", "UPI",
                "items", List.of(
                        Map.of("productId", 1, "quantity", 1),
                        Map.of("productId", 3, "quantity", 2)
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

        List<Map<String, Object>> items = DatabaseManager.getOrderItems(orderId);
        Assert.assertEquals(items.size(), 2, "There should be 2 order_items persisted for order ID " + orderId);

        Map<String, Object> firstItem = items.get(0);
        Assert.assertEquals(((Number) firstItem.get("product_id")).intValue(), 1);
        Assert.assertEquals(((Number) firstItem.get("quantity")).intValue(), 1);

        Map<String, Object> secondItem = items.get(1);
        Assert.assertEquals(((Number) secondItem.get("product_id")).intValue(), 3);
        Assert.assertEquals(((Number) secondItem.get("quantity")).intValue(), 2);
    }
}
