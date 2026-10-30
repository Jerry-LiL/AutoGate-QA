import json
from pathlib import Path
import pytest
from pages.login_page import LoginPage
from pages.products_page import ProductsPage
from pages.cart_page import CartPage
from pages.checkout_page import CheckoutPage

PINCODE_DATA = Path(__file__).parent / "data" / "pincode_boundary_cases.json"
with open(PINCODE_DATA, "r", encoding="utf-8") as f:
    PINCODE_CASES = json.load(f)


@pytest.mark.ui
@pytest.mark.regression
class TestUiCheckout:

    def test_complete_checkout_flow(self, page):
        # 1. Login
        login_page = LoginPage(page)
        login_page.navigate_to_login()
        login_page.login("shopper@autogate.qa", "Shopper@123")
        login_page.wait_for_login_success()

        # 2. Add product to cart
        products_page = ProductsPage(page)
        products_page.open_products()
        products_page.set_quantity(1, 1)
        products_page.add_to_cart(1)
        products_page.wait_for_cart_toast()
        products_page.go_to_cart()

        # 3. Cart View
        cart_page = CartPage(page)
        cart_page.wait_for_cart_loaded()
        cart_page.wait_for_item_in_cart(1)
        cart_page.proceed_to_checkout()

        # 4. Multi-step checkout - Step 1: Shipping
        checkout_page = CheckoutPage(page)
        checkout_page.wait_for_step1()
        checkout_page.fill_shipping("Diana Prince", "456 Gateway Avenue", "Bengaluru", "560001")
        checkout_page.submit_step1()

        # Step 2: Payment
        checkout_page.wait_for_step2()
        checkout_page.fill_card_payment("4111222233334444", "12/28", "999")
        checkout_page.submit_step2()

        # Step 3: Review
        checkout_page.wait_for_step3()
        assert "Diana Prince" in checkout_page.get_review_shipping()
        assert "CARD" in checkout_page.get_review_payment()
        checkout_page.place_order()

        # Step 4: Confirmation
        checkout_page.wait_for_confirmation()
        order_num = checkout_page.get_confirmation_number()
        assert order_num.startswith("ORD-")
        assert checkout_page.get_confirmation_status() == "CONFIRMED"

    def test_shipping_requires_name(self, page):
        login_page = LoginPage(page)
        login_page.navigate_to_login()
        login_page.login("shopper@autogate.qa", "Shopper@123")
        login_page.wait_for_login_success()

        products_page = ProductsPage(page)
        products_page.open_products()
        products_page.set_quantity(2, 1)
        products_page.add_to_cart(2)
        products_page.wait_for_cart_toast()
        products_page.go_to_cart()

        cart_page = CartPage(page)
        cart_page.wait_for_cart_loaded()
        cart_page.proceed_to_checkout()

        checkout_page = CheckoutPage(page)
        checkout_page.wait_for_step1()
        checkout_page.fill_shipping("", "123 Valid Street", "Bengaluru", "560001")
        checkout_page.submit_step1()

        err = checkout_page.get_shipping_name_error()
        assert "Full name is required" in err

    @pytest.mark.parametrize("case", PINCODE_CASES, ids=[c["description"] for c in PINCODE_CASES])
    def test_shipping_pincode_boundary_validation(self, page, case):
        login_page = LoginPage(page)
        login_page.navigate_to_login()
        login_page.login("shopper@autogate.qa", "Shopper@123")
        login_page.wait_for_login_success()

        products_page = ProductsPage(page)
        products_page.open_products()
        products_page.set_quantity(3, 1)
        products_page.add_to_cart(3)
        products_page.wait_for_cart_toast()
        products_page.go_to_cart()

        cart_page = CartPage(page)
        cart_page.wait_for_cart_loaded()
        cart_page.proceed_to_checkout()

        checkout_page = CheckoutPage(page)
        checkout_page.wait_for_step1()
        checkout_page.fill_shipping("Valid Customer", "100 Automation Rd", "Hyderabad", case["pincode"])
        checkout_page.submit_step1()

        if case["is_valid"]:
            checkout_page.wait_for_step2()
        else:
            err = checkout_page.get_shipping_pincode_error()
            assert err == case["expected_message"]

    def test_back_navigation_preserves_state(self, page):
        login_page = LoginPage(page)
        login_page.navigate_to_login()
        login_page.login("shopper@autogate.qa", "Shopper@123")
        login_page.wait_for_login_success()

        products_page = ProductsPage(page)
        products_page.open_products()
        products_page.set_quantity(4, 1)
        products_page.add_to_cart(4)
        products_page.wait_for_cart_toast()
        products_page.go_to_cart()

        cart_page = CartPage(page)
        cart_page.wait_for_cart_loaded()
        cart_page.proceed_to_checkout()

        checkout_page = CheckoutPage(page)
        checkout_page.wait_for_step1()
        checkout_page.fill_shipping("Back Navigator", "321 History Road", "Pune", "411001")
        checkout_page.submit_step1()

        checkout_page.wait_for_step2()
        checkout_page.click_step2_back()
        checkout_page.wait_for_step1()
