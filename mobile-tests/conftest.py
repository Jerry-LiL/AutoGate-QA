import os
import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options

APPIUM_SERVER_URL = os.getenv("APPIUM_SERVER_URL", "http://localhost:4723")
APP_PACKAGE = os.getenv("APP_PACKAGE", "com.vineet.simplifytrack")
APP_ACTIVITY = os.getenv("APP_ACTIVITY", "com.vineet.simplifytrack.MainActivity")


@pytest.fixture(scope="function")
def driver():
    options = UiAutomator2Options()
    options.platform_name = "Android"
    options.automation_name = "UiAutomator2"
    options.app_package = APP_PACKAGE
    options.app_activity = APP_ACTIVITY
    options.no_reset = True
    options.new_command_timeout = 60

    try:
        driver = webdriver.Remote(command_executor=APPIUM_SERVER_URL, options=options)
    except Exception as exc:
        pytest.skip(
            f"Appium server unreachable at {APPIUM_SERVER_URL} ({type(exc).__name__}). "
            "Start Appium 2 (`appium`) and an Android emulator to run mobile regression tests."
        )

    yield driver

    try:
        driver.quit()
    except Exception:
        pass
