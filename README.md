# AutoGate QA

Test automation for a small demo store: web UI, REST API, database checks and a few mobile tests. The store itself (Express + SQLite) lives in `demo-app/` so the suites have something real to run against.

## Stack

- Demo app: Node, Express, SQLite (`node:sqlite`)
- Java: Java 17, TestNG, Selenium 4, REST Assured, Allure
- Python: PyTest, Playwright, Requests
- Postman collection run with Newman
- Mobile: Appium Python client
- CI: GitHub Actions, plus a Jenkinsfile

## Running the Demo App

```bash
cd demo-app
npm install
npm run reset
npm start
```

The app runs on `http://localhost:3000`. `npm run reset` re-seeds the database.

Seeded users: `admin@autogate.qa` / `Admin@123`, `tester@autogate.qa` / `Tester@123`, `shopper@autogate.qa` / `Shopper@123`.

## Running the Tests

Start the demo app first.

Java (Selenium + REST Assured):

```bash
mvn -f java-tests/pom.xml test
```

Python (Playwright + Requests):

```bash
python3 -m venv python-tests/.venv
source python-tests/.venv/bin/activate
pip install -r python-tests/requirements.txt
playwright install chromium
cd python-tests && pytest
```

Newman:

```bash
npm install
npm run test:postman
```

Mobile tests need Appium on port 4723 and an emulator with the app installed (see `mobile-tests/README.md`). They skip if Appium isn't running:

```bash
pytest mobile-tests/
```

The test plan, traceability matrix and bug template are in `docs/`.

## Demo app API

- `GET /health` - Service health status
- `POST /api/auth/login` - Authenticate and return JWT token
- `GET /api/auth/me` - Authenticated user info
- `GET /api/products` - List all products (`?category=` filter supported)
- `GET /api/products/:id` - Fetch product by ID
- `GET /api/cart` - Get current cart items and totals
- `POST /api/cart` - Add product to cart
- `DELETE /api/cart/:productId` - Remove item from cart
- `DELETE /api/cart` - Clear entire cart
- `POST /api/orders` - Place order from cart
- `GET /api/orders/:id` - Get order by ID
- `POST /api/reset` - Reset database schema and seed data
