from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class BaseScreen:
    def __init__(self, driver, timeout_sec: int = 10):
        self.driver = driver
        self.wait = WebDriverWait(driver, timeout_sec)

    def find(self, locator):
        return self.wait.until(EC.presence_of_element_located(locator))

    def wait_for_visible(self, locator):
        return self.wait.until(EC.visibility_of_element_located(locator))

    def click(self, locator):
        element = self.wait.until(EC.element_to_be_clickable(locator))
        element.click()

    def type(self, locator, text: str):
        element = self.wait_for_visible(locator)
        element.clear()
        element.send_keys(text)

    def get_text(self, locator) -> str:
        return self.wait_for_visible(locator).text.strip()

    def is_visible(self, locator) -> bool:
        try:
            return self.wait_for_visible(locator).is_displayed()
        except Exception:
            return False
