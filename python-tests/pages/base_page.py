from playwright.sync_api import Page, expect
from config import BASE_URL


class BasePage:
    def __init__(self, page: Page):
        self.page = page

    def open(self, path: str = "/"):
        self.page.goto(f"{BASE_URL}{path}")

    def wait_for_visible(self, selector: str):
        locator = self.page.locator(selector)
        locator.wait_for(state="visible")
        return locator

    def wait_for_hidden(self, selector: str):
        self.page.locator(selector).wait_for(state="hidden")

    def click(self, selector: str):
        self.page.locator(selector).click()

    def type(self, selector: str, text: str):
        locator = self.page.locator(selector)
        locator.fill(text)

    def get_text(self, selector: str) -> str:
        return self.page.locator(selector).inner_text().strip()

    def is_visible(self, selector: str) -> bool:
        return self.page.locator(selector).is_visible()
