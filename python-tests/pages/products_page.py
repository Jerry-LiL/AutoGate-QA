from pages.base_page import BasePage


class ProductsPage(BasePage):
    PRODUCTS_VIEW = "[data-testid='products-view']"
    SEARCH_INPUT = "[data-testid='search-input']"
    CATEGORY_FILTER = "[data-testid='category-filter']"
    NAV_CART = "[data-testid='nav-cart']"
    CART_BADGE = "[data-testid='cart-badge']"
    CART_TOAST = "[data-testid='cart-toast']"

    def open_products(self):
        self.open("/")
        self.wait_for_visible(self.PRODUCTS_VIEW)
        self.wait_for_visible("[data-testid='product-card-1']")

    def search_product(self, term: str):
        self.type(self.SEARCH_INPUT, term)

    def filter_by_category(self, category: str):
        self.page.locator(self.CATEGORY_FILTER).select_option(value=category)

    def is_product_visible(self, product_id: int) -> bool:
        return self.is_visible(f"[data-testid='product-card-{product_id}']")

    def wait_for_product_hidden(self, product_id: int):
        self.wait_for_hidden(f"[data-testid='product-card-{product_id}']")

    def get_product_title(self, product_id: int) -> str:
        return self.get_text(f"[data-testid='product-title-{product_id}']")

    def get_product_price(self, product_id: int) -> str:
        return self.get_text(f"[data-testid='product-price-{product_id}']")

    def set_quantity(self, product_id: int, qty: int):
        self.type(f"[data-testid='product-qty-{product_id}']", str(qty))

    def add_to_cart(self, product_id: int):
        self.click(f"[data-testid='add-to-cart-{product_id}']")

    def wait_for_cart_toast(self):
        self.wait_for_visible(self.CART_TOAST)

    def wait_for_badge_count_at_least(self, min_count: int):
        self.page.wait_for_function(
            f"parseInt(document.querySelector('[data-testid=\"cart-badge\"]').textContent || '0', 10) >= {min_count}"
        )

    def get_cart_badge_count(self) -> int:
        text = self.get_text(self.CART_BADGE)
        return int(text) if text.isdigit() else 0

    def get_quantity_error(self, product_id: int) -> str:
        sel = f"[data-testid='qty-error-{product_id}'].visible"
        self.wait_for_visible(sel)
        return self.get_text(sel)

    def go_to_cart(self):
        self.click(self.NAV_CART)
