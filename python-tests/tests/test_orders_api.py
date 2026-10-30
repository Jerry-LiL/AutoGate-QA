import pytest
import requests
from jsonschema import validate


@pytest.mark.api
@pytest.mark.regression
class TestOrdersApi:

    def test_create_order_direct_payload(self, base_url, auth_token_factory, json_schemas):
        token = auth_token_factory("tester@autogate.qa", "Tester@123")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

        order_data = {
            "shippingAddress": {
                "fullName": "Diana Prince",
                "addressLine": "789 Themyscira Way",
                "city": "Metropolis",
                "pincode": "500001"
            },
            "paymentMethod": "CARD",
            "items": [
                {"productId": 1, "quantity": 1},
                {"productId": 2, "quantity": 1}
            ]
        }
        resp = requests.post(f"{base_url}/api/orders", json=order_data, headers=headers, timeout=5)
        assert resp.status_code == 201
        data = resp.json()
        assert data["orderNumber"].startswith("ORD-")
        assert data["status"] == "CONFIRMED"
        assert len(data["items"]) == 2
        validate(instance=data, schema=json_schemas["order-schema"])

    def test_get_order_by_id(self, base_url, auth_token_factory):
        token = auth_token_factory("tester@autogate.qa", "Tester@123")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

        # Create an order
        order_data = {
            "shippingAddress": {
                "fullName": "Arthur Curry",
                "addressLine": "Ocean Drive 101",
                "city": "Atlantis",
                "pincode": "600001"
            },
            "paymentMethod": "COD",
            "items": [{"productId": 3, "quantity": 1}]
        }
        create_resp = requests.post(f"{base_url}/api/orders", json=order_data, headers=headers, timeout=5)
        assert create_resp.status_code == 201
        created = create_resp.json()
        order_id = created["id"]

        # Fetch order
        get_resp = requests.get(f"{base_url}/api/orders/{order_id}", headers=headers, timeout=5)
        assert get_resp.status_code == 200
        fetched = get_resp.json()
        assert fetched["id"] == order_id
        assert fetched["orderNumber"] == created["orderNumber"]

    def test_create_order_validation_empty_items(self, base_url, auth_token_factory, json_schemas):
        token = auth_token_factory("tester@autogate.qa", "Tester@123")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

        invalid_data = {
            "shippingAddress": {
                "fullName": "Invalid Tester",
                "addressLine": "123 Error Lane",
                "city": "Nowhere",
                "pincode": "560001"
            },
            "paymentMethod": "CARD",
            "items": []
        }
        resp = requests.post(f"{base_url}/api/orders", json=invalid_data, headers=headers, timeout=5)
        assert resp.status_code == 400
        validate(instance=resp.json(), schema=json_schemas["error-schema"])

    def test_create_order_unauthorized(self, base_url):
        resp = requests.post(f"{base_url}/api/orders", json={}, timeout=5)
        assert resp.status_code == 401
