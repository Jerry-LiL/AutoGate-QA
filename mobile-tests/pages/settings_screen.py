from appium.webdriver.common.appiumby import AppiumBy
from pages.base_screen import BaseScreen


class SettingsScreen(BaseScreen):
    # Selectors discovered from simplify-money-track source
    SAVE_RESYNC_BTN = (AppiumBy.ACCESSIBILITY_ID, "Save server URL and resync")
    RESET_URL_BTN = (AppiumBy.ACCESSIBILITY_ID, "Reset server URL to default")
    SERVER_URL_INPUT = (AppiumBy.CLASS_NAME, "android.widget.EditText")
    SETTINGS_TITLE = (AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().textContains("Settings")')

    def wait_for_screen_loaded(self):
        return self.wait_for_visible(self.RESET_URL_BTN)

    def is_reset_button_visible(self) -> bool:
        return self.is_visible(self.RESET_URL_BTN)

    def is_save_button_visible(self) -> bool:
        return self.is_visible(self.SAVE_RESYNC_BTN)

    def set_server_url(self, url: str):
        self.type(self.SERVER_URL_INPUT, url)

    def get_server_url(self) -> str:
        return self.get_text(self.SERVER_URL_INPUT)

    def click_save_and_resync(self):
        self.click(self.SAVE_RESYNC_BTN)

    def click_reset_default(self):
        self.click(self.RESET_URL_BTN)
