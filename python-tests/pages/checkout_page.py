from pages.base_page import BasePage


class CheckoutPage(BasePage):
    STEP1_CONTAINER = "[data-testid='checkout-step-1']"
    SHIPPING_NAME = "[data-testid='shipping-name']"
    SHIPPING_ADDRESS = "[data-testid='shipping-address']"
    SHIPPING_CITY = "[data-testid='shipping-city']"
    SHIPPING_PINCODE = "[data-testid='shipping-pincode']"
    STEP1_NEXT = "[data-testid='step1-next-btn']"

    ERROR_NAME = "[data-testid='error-shipping-name']"
    ERROR_PINCODE = "[data-testid='error-shipping-pincode']"

    STEP2_CONTAINER = "[data-testid='checkout-step-2']"
    PAYMENT_METHOD = "[data-testid='payment-method-select']"
    CARD_NUMBER = "[data-testid='card-number']"
    CARD_EXPIRY = "[data-testid='card-expiry']"
    CARD_CVV = "[data-testid='card-cvv']"
    STEP2_BACK = "[data-testid='step2-back-btn']"
    STEP2_NEXT = "[data-testid='step2-next-btn']"

    STEP3_CONTAINER = "[data-testid='checkout-step-3']"
    REVIEW_SHIPPING = "[data-testid='review-shipping-details']"
    REVIEW_PAYMENT = "[data-testid='review-payment-details']"
    PLACE_ORDER_BTN = "[data-testid='place-order-btn']"

    STEP4_CONTAINER = "[data-testid='checkout-step-4']"
    ORDER_NUMBER = "[data-testid='order-confirmation-number']"
    ORDER_STATUS = "[data-testid='order-confirmation-status']"

    def wait_for_step1(self):
        self.wait_for_visible(self.STEP1_CONTAINER)

    def fill_shipping(self, name: str, address: str, city: str, pincode: str):
        self.type(self.SHIPPING_NAME, name)
        self.type(self.SHIPPING_ADDRESS, address)
        self.type(self.SHIPPING_CITY, city)
        self.type(self.SHIPPING_PINCODE, pincode)

    def submit_step1(self):
        self.click(self.STEP1_NEXT)

    def get_shipping_name_error(self) -> str:
        sel = f"{self.ERROR_NAME}.visible"
        self.wait_for_visible(sel)
        return self.get_text(sel)

    def get_shipping_pincode_error(self) -> str:
        sel = f"{self.ERROR_PINCODE}.visible"
        self.wait_for_visible(sel)
        return self.get_text(sel)

    def wait_for_step2(self):
        self.wait_for_visible(self.STEP2_CONTAINER)

    def fill_card_payment(self, number: str, expiry: str, cvv: str):
        self.page.locator(self.PAYMENT_METHOD).select_option(value="CARD")
        self.type(self.CARD_NUMBER, number)
        self.type(self.CARD_EXPIRY, expiry)
        self.type(self.CARD_CVV, cvv)

    def submit_step2(self):
        self.click(self.STEP2_NEXT)

    def click_step2_back(self):
        self.click(self.STEP2_BACK)

    def wait_for_step3(self):
        self.wait_for_visible(self.STEP3_CONTAINER)

    def get_review_shipping(self) -> str:
        return self.get_text(self.REVIEW_SHIPPING)

    def get_review_payment(self) -> str:
        return self.get_text(self.REVIEW_PAYMENT)

    def place_order(self):
        self.click(self.PLACE_ORDER_BTN)

    def wait_for_confirmation(self):
        self.wait_for_visible(self.STEP4_CONTAINER)

    def get_confirmation_number(self) -> str:
        return self.get_text(self.ORDER_NUMBER)

    def get_confirmation_status(self) -> str:
        return self.get_text(self.ORDER_STATUS)
