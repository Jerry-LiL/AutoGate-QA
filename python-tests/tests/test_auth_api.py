import json
from pathlib import Path
import pytest
import requests
from jsonschema import validate

DATA_FILE = Path(__file__).parent / "data" / "login_scenarios.json"
with open(DATA_FILE, "r", encoding="utf-8") as f:
    LOGIN_SCENARIOS = json.load(f)


@pytest.mark.api
@pytest.mark.regression
class TestAuthApi:

    @pytest.mark.parametrize("scenario", LOGIN_SCENARIOS, ids=[s["scenario"] for s in LOGIN_SCENARIOS])
    def test_login_scenarios(self, base_url, json_schemas, scenario):
        payload = {
            "email": scenario["email"],
            "password": scenario["password"]
        }
        resp = requests.post(f"{base_url}/api/auth/login", json=payload, timeout=5)
        assert resp.status_code == scenario["expected_status"]

        body = resp.json()
        if scenario["should_succeed"]:
            assert "token" in body
            assert body["user"]["role"] == scenario["expected_role"]
            validate(instance=body, schema=json_schemas["login-response-schema"])
        else:
            assert "error" in body or "message" in body

    @pytest.mark.smoke
    def test_health_endpoint(self, base_url):
        resp = requests.get(f"{base_url}/health", timeout=5)
        assert resp.status_code == 200
        data = resp.json()
        assert data["status"] == "UP"
        assert data["service"] == "AutoGate Mini Shop"
