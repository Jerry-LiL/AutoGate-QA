import pytest
import requests


@pytest.mark.db
@pytest.mark.regression
class TestDatabasePersistence:

    def test_seeded_users_in_database(self, db_conn):
        cursor = db_conn.cursor()
        cursor.execute("SELECT email, role, name FROM users ORDER BY id ASC")
        rows = cursor.fetchall()
        emails = [r["email"] for r in rows]

        assert "admin@autogate.qa" in emails
        assert "tester@autogate.qa" in emails
        assert "shopper@autogate.qa" in emails

    def test_seeded_products_in_database(self, db_conn):
        cursor = db_conn.cursor()
        cursor.execute("SELECT id, name, price, stock FROM products WHERE id = 1")
        row = cursor.fetchone()

        assert row is not None
        assert row["name"] == "UltraSync Smartwatch"
        assert row["price"] == 199.99
        assert row["stock"] > 0

    def test_order_persistence_in_database(self, base_url, auth_token_factory, db_conn):
        token = auth_token_factory("shopper@autogate.qa", "Shopper@123")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

        order_data = {
            "shippingAddress": {
                "fullName": "DB Persistence Verifier",
                "addressLine": "999 SQLite Highway",
                "city": "Bengaluru",
                "pincode": "560001"
            },
            "paymentMethod": "UPI",
            "items": [
                {"productId": 1, "quantity": 1},
                {"productId": 5, "quantity": 2}
            ]
        }

        resp = requests.post(f"{base_url}/api/orders", json=order_data, headers=headers, timeout=5)
        assert resp.status_code == 201
        created = resp.json()
        order_number = created["orderNumber"]

        # Assert directly in SQLite database
        cursor = db_conn.cursor()
        cursor.execute("SELECT * FROM orders WHERE order_number = ?", (order_number,))
        order_row = cursor.fetchone()

        assert order_row is not None
        assert order_row["status"] == "CONFIRMED"
        assert order_row["payment_method"] == "UPI"
        assert order_row["shipping_city"] == "Bengaluru"

        # Assert items in order_items table
        cursor.execute("SELECT * FROM order_items WHERE order_id = ?", (order_row["id"],))
        item_rows = cursor.fetchall()
        assert len(item_rows) == 2
        product_ids = [r["product_id"] for r in item_rows]
        assert 1 in product_ids
        assert 5 in product_ids
