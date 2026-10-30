import os
from pathlib import Path
from dotenv import load_dotenv

load_dotenv()

BASE_DIR = Path(__file__).resolve().parent
REPO_ROOT = BASE_DIR.parent

BASE_URL = os.getenv("AUTOGATE_BASE_URL", "http://localhost:3000")
DB_PATH = os.getenv(
    "AUTOGATE_DB_PATH",
    str(REPO_ROOT / "demo-app" / "data" / "autogate.db")
)
HEADLESS = os.getenv("HEADLESS", "true").lower() in ("true", "1", "yes")
TIMEOUT_MS = int(os.getenv("TIMEOUT_MS", "10000"))
DEFAULT_TIMEOUT_SEC = TIMEOUT_MS / 1000.0
