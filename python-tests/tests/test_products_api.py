import pytest
import requests
from jsonschema import validate


@pytest.mark.api
@pytest.mark.regression
class TestProductsApi:

    def test_get_all_products(self, base_url, json_schemas):
        resp = requests.get(f"{base_url}/api/products", timeout=5)
        assert resp.status_code == 200
        products = resp.json()
        assert isinstance(products, list)
        assert len(products) >= 6
        validate(instance=products, schema=json_schemas["products-schema"])

    def test_get_product_by_id_valid(self, base_url, json_schemas):
        resp = requests.get(f"{base_url}/api/products/1", timeout=5)
        assert resp.status_code == 200
        data = resp.json()
        assert data["id"] == 1
        assert "UltraSync" in data["name"]
        validate(instance=data, schema=json_schemas["product-schema"])

    def test_get_product_by_id_not_found(self, base_url, json_schemas):
        resp = requests.get(f"{base_url}/api/products/99999", timeout=5)
        assert resp.status_code == 404
        data = resp.json()
        assert data["error"] == "Not Found"
        validate(instance=data, schema=json_schemas["error-schema"])

    def test_filter_products_by_category(self, base_url):
        resp = requests.get(f"{base_url}/api/products?category=Audio", timeout=5)
        assert resp.status_code == 200
        items = resp.json()
        assert len(items) > 0
        for item in items:
            assert item["category"] == "Audio"
