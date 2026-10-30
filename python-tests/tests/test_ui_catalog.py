import csv
from pathlib import Path
import pytest
from pages.products_page import ProductsPage
from pages.login_page import LoginPage

CSV_PATH = Path(__file__).parent / "data" / "quantity_boundary_cases.csv"


def load_qty_cases():
    cases = []
    with open(CSV_PATH, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            cases.append(row)
    return cases


QTY_CASES = load_qty_cases()


@pytest.mark.ui
@pytest.mark.regression
class TestUiCatalog:

    def test_catalog_renders_seeded_products(self, page):
        products_page = ProductsPage(page)
        products_page.open_products()

        assert products_page.is_product_visible(1)
        assert products_page.is_product_visible(2)
        assert products_page.get_product_title(1) == "UltraSync Smartwatch"
        assert products_page.get_product_price(1) == "$199.99"

    def test_search_filters_products(self, page):
        products_page = ProductsPage(page)
        products_page.open_products()

        products_page.search_product("Headphones")
        products_page.wait_for_product_hidden(1)
        assert products_page.is_product_visible(2)
        assert not products_page.is_product_visible(1)

    def test_category_dropdown_filters_products(self, page):
        products_page = ProductsPage(page)
        products_page.open_products()

        products_page.filter_by_category("Audio")
        products_page.wait_for_product_hidden(1)
        assert products_page.is_product_visible(2)
        assert not products_page.is_product_visible(1)

    @pytest.mark.parametrize("case", QTY_CASES, ids=[c["case_id"] for c in QTY_CASES])
    def test_quantity_boundaries(self, page, case):
        qty = int(case["quantity"])
        is_valid = case["is_valid"].lower() == "true"

        if is_valid:
            # Must be logged in to add to cart
            login_page = LoginPage(page)
            login_page.navigate_to_login()
            login_page.login("shopper@autogate.qa", "Shopper@123")
            login_page.wait_for_login_success()

        products_page = ProductsPage(page)
        products_page.open_products()
        products_page.set_quantity(1, qty)
        products_page.add_to_cart(1)

        if is_valid:
            products_page.wait_for_cart_toast()
            products_page.wait_for_badge_count_at_least(1)
            assert products_page.get_cart_badge_count() >= 1
        else:
            err = products_page.get_quantity_error(1)
            assert err == case["expected_error"]
