import pytest
from pages.login_page import LoginPage


@pytest.mark.ui
@pytest.mark.smoke
@pytest.mark.regression
class TestUiLogin:

    def test_successful_login(self, page):
        login_page = LoginPage(page)
        login_page.navigate_to_login()
        login_page.login("admin@autogate.qa", "Admin@123")
        login_page.wait_for_login_success()

        assert "Admin" in login_page.get_greeting_text()

    def test_invalid_password_shows_error(self, page):
        login_page = LoginPage(page)
        login_page.navigate_to_login()
        login_page.login("admin@autogate.qa", "IncorrectPass999")

        err = login_page.get_error_text()
        assert err == "Invalid email or password"

    def test_logout_clears_session(self, page):
        login_page = LoginPage(page)
        login_page.navigate_to_login()
        login_page.login("admin@autogate.qa", "Admin@123")
        login_page.wait_for_login_success()

        login_page.click(LoginPage.NAV_LOGOUT)
        login_page.wait_for_visible(LoginPage.LOGIN_VIEW)
        assert login_page.is_visible(LoginPage.NAV_LOGIN)
