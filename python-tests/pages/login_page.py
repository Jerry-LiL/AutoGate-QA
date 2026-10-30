from pages.base_page import BasePage


class LoginPage(BasePage):
    NAV_LOGIN = "[data-testid='nav-login']"
    NAV_LOGOUT = "[data-testid='nav-logout']"
    LOGIN_VIEW = "[data-testid='login-view']"
    EMAIL_INPUT = "[data-testid='login-email']"
    PASSWORD_INPUT = "[data-testid='login-password']"
    SUBMIT_BTN = "[data-testid='login-submit']"
    ERROR_BOX = "[data-testid='login-error']"
    USER_GREETING = "[data-testid='user-greeting']"

    def navigate_to_login(self):
        self.open("/")
        self.click(self.NAV_LOGIN)
        self.wait_for_visible(self.LOGIN_VIEW)

    def login(self, email: str, password: str):
        self.type(self.EMAIL_INPUT, email)
        self.type(self.PASSWORD_INPUT, password)
        self.click(self.SUBMIT_BTN)

    def wait_for_login_success(self):
        self.wait_for_visible(self.USER_GREETING)
        self.wait_for_visible(self.NAV_LOGOUT)

    def get_greeting_text(self) -> str:
        return self.get_text(self.USER_GREETING)

    def get_error_text(self) -> str:
        self.wait_for_visible(self.ERROR_BOX)
        return self.get_text(self.ERROR_BOX)
