# Test Plan

Test plan for AutoGate-QA covering web UI, REST API, SQLite database persistence, and mobile tests.

## Scope

### In-Scope
- Web UI: authentication, catalog search/filter, cart mutations, checkout wizard (shipping, payment, confirmation).
- REST API: endpoints under `/api/auth`, `/api/products`, `/api/cart`, `/api/orders`, and `/health`.
- Data persistence: SQLite validation for orders, items, and inventory.
- Boundary testing: quantity limits (1-10) and shipping pincode format (6 numeric digits).

### Out-of-Scope
- Performance and load testing.
- Production payment integrations.
- Appium mobile tests in headless CI (run locally with emulator).

## Test Approach

- **UI Tests**: Selenium 4 (Java) and Playwright (Python) using Page Object Model and explicit waits.
- **API Tests**: REST Assured (Java), Requests (Python), and Newman (Postman) with JSON schema validation.
- **Database Tests**: Direct SQLite queries in Java (JDBC) and Python (sqlite3) verifying data state.
- **Mobile Tests**: Appium Python client targeting Android emulator.

## Boundary Analysis

### Quantity (1-10 allowed)
- `< 1`: Rejected with validation error.
- `1` (min) to `10` (max): Accepted and added to cart.
- `> 10`: Rejected with validation error.

### Shipping Pincode (6 digits required)
- `560001` (6 numeric digits): Valid, advances to payment.
- 5 digits, 7 digits, alphanumeric, or whitespace: Rejected with validation error.

## Criteria

### Entry
- SUT running on port 3000 with healthy `/health` status.
- Database reset to clean state (`npm run reset` in `demo-app`).

### Exit
- All automated test suites pass with no unhandled exceptions.
- No hardcoded sleeps in test code.
