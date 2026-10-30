import pytest
import requests


@pytest.mark.api
@pytest.mark.regression
class TestCartApi:

    def test_add_item_to_cart(self, base_url, auth_token_factory):
        token = auth_token_factory("admin@autogate.qa", "Admin@123")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

        # Clear cart first
        requests.delete(f"{base_url}/api/cart", headers=headers, timeout=5)

        # Add product 1 with quantity 2
        payload = {"productId": 1, "quantity": 2}
        resp = requests.post(f"{base_url}/api/cart", json=payload, headers=headers, timeout=5)
        assert resp.status_code == 200
        data = resp.json()
        assert len(data["items"]) >= 1
        assert data["totalAmount"] > 0

    def test_get_cart_items(self, base_url, auth_token_factory):
        token = auth_token_factory("admin@autogate.qa", "Admin@123")
        headers = {"Authorization": f"Bearer {token}"}

        resp = requests.get(f"{base_url}/api/cart", headers=headers, timeout=5)
        assert resp.status_code == 200
        data = resp.json()
        assert "items" in data
        assert "totalAmount" in data

    def test_remove_cart_item(self, base_url, auth_token_factory):
        token = auth_token_factory("admin@autogate.qa", "Admin@123")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

        # Ensure item 1 is present
        requests.post(f"{base_url}/api/cart", json={"productId": 1, "quantity": 1}, headers=headers, timeout=5)

        # Remove item 1
        del_resp = requests.delete(f"{base_url}/api/cart/1", headers=headers, timeout=5)
        assert del_resp.status_code == 200
        cart = del_resp.json()
        item_ids = [it["productId"] for it in cart.get("items", [])]
        assert 1 not in item_ids

    def test_add_cart_item_validation_boundary(self, base_url, auth_token_factory):
        token = auth_token_factory("admin@autogate.qa", "Admin@123")
        headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

        # Boundary qty = 0 (invalid)
        resp_zero = requests.post(f"{base_url}/api/cart", json={"productId": 2, "quantity": 0}, headers=headers, timeout=5)
        assert resp_zero.status_code == 400

        # Boundary qty = 11 (exceeds max 10)
        resp_over = requests.post(f"{base_url}/api/cart", json={"productId": 2, "quantity": 11}, headers=headers, timeout=5)
        assert resp_over.status_code == 400
