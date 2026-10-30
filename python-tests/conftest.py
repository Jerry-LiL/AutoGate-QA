import json
import sqlite3
import pytest
import requests
from pathlib import Path
from playwright.sync_api import sync_playwright

from config import BASE_URL, DB_PATH, HEADLESS, DEFAULT_TIMEOUT_SEC

SCHEMAS_DIR = Path(__file__).parent / "tests" / "schemas"


@pytest.fixture(scope="session")
def base_url():
    return BASE_URL


@pytest.fixture(scope="session")
def db_path():
    return DB_PATH


@pytest.fixture
def db_conn(db_path):
    conn = sqlite3.connect(db_path)
    conn.row_factory = sqlite3.Row
    yield conn
    conn.close()


@pytest.fixture(scope="session")
def json_schemas():
    schemas = {}
    for schema_file in SCHEMAS_DIR.glob("*.json"):
        with open(schema_file, "r", encoding="utf-8") as f:
            schemas[schema_file.stem] = json.load(f)
    return schemas


@pytest.fixture(scope="session")
def playwright_instance():
    with sync_playwright() as p:
        yield p


@pytest.fixture(scope="session")
def browser(playwright_instance):
    browser = playwright_instance.chromium.launch(headless=HEADLESS)
    yield browser
    browser.close()


@pytest.fixture
def page(browser, request):
    context = browser.new_context()
    page = context.new_page()
    page.set_default_timeout(DEFAULT_TIMEOUT_SEC * 1000)

    yield page

    # Screenshot on failure if requested
    if hasattr(request.node, "rep_call") and request.node.rep_call.failed:
        screenshots_dir = Path("reports") / "screenshots"
        screenshots_dir.mkdir(parents=True, exist_ok=True)
        screenshot_path = screenshots_dir / f"{request.node.name}.png"
        page.screenshot(path=str(screenshot_path))

    context.close()


@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    outcome = yield
    rep = outcome.get_result()
    setattr(item, "rep_" + rep.when, rep)


@pytest.fixture
def auth_token_factory(base_url):
    def _get_token(email="tester@autogate.qa", password="Tester@123"):
        resp = requests.post(
            f"{base_url}/api/auth/login",
            json={"email": email, "password": password},
            timeout=5
        )
        assert resp.status_code == 200, f"Login failed for {email}: {resp.text}"
        return resp.json()["token"]
    return _get_token


@pytest.fixture
def auth_headers(auth_token_factory):
    token = auth_token_factory()
    return {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
