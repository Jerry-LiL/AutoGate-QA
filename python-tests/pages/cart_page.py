from pages.base_page import BasePage


class CartPage(BasePage):
    CART_VIEW = "[data-testid='cart-view']"
    EMPTY_CART_MSG = "[data-testid='empty-cart-msg']"
    CART_TOTAL = "[data-testid='cart-total-amount']"
    CLEAR_CART_BTN = "[data-testid='clear-cart-btn']"
    PROCEED_BTN = "[data-testid='proceed-checkout-btn']"

    def wait_for_cart_loaded(self):
        self.wait_for_visible(self.CART_VIEW)

    def is_item_in_cart(self, product_id: int) -> bool:
        return self.is_visible(f"[data-testid='cart-row-{product_id}']")

    def wait_for_item_in_cart(self, product_id: int):
        self.wait_for_visible(f"[data-testid='cart-row-{product_id}']")

    def get_item_title(self, product_id: int) -> str:
        return self.get_text(f"[data-testid='cart-item-title-{product_id}']")

    def get_cart_total(self) -> float:
        text = self.get_text(self.CART_TOTAL)
        return float(text)

    def remove_item(self, product_id: int):
        self.click(f"[data-testid='cart-item-remove-{product_id}']")

    def clear_cart(self):
        self.click(self.CLEAR_CART_BTN)

    def proceed_to_checkout(self):
        self.click(self.PROCEED_BTN)
