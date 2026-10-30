from appium.webdriver.common.appiumby import AppiumBy
from pages.base_screen import BaseScreen


class TrackScreen(BaseScreen):
    # Selectors discovered from APK manifest & React Native components
    SETTINGS_BUTTON = (AppiumBy.ACCESSIBILITY_ID, "Open settings")
    HEADER_TITLE = (AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().text("Track")')
    ADD_TRANSACTION_BTN = (AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().textContains("Add")')
    TRANSACTION_LIST = (AppiumBy.CLASS_NAME, "android.widget.ScrollView")

    def wait_for_screen_loaded(self):
        return self.wait_for_visible(self.HEADER_TITLE)

    def is_header_visible(self) -> bool:
        return self.is_visible(self.HEADER_TITLE)

    def is_settings_button_visible(self) -> bool:
        return self.is_visible(self.SETTINGS_BUTTON)

    def open_settings(self):
        self.click(self.SETTINGS_BUTTON)
