# AutoGate QA: End-to-End Regression Automation Suite

[![CI](https://github.com/Jerry-LiL/AutoGate-QA/actions/workflows/ci.yml/badge.svg)](https://github.com/Jerry-LiL/AutoGate-QA/actions/workflows/ci.yml)

AutoGate QA is a full-stack test automation portfolio project demonstrating multi-layer quality assurance across web user interfaces, RESTful APIs, SQLite databases, and Android mobile applications.

The project incorporates strict Page Object Models (POM), explicit synchronization with zero arbitrary sleep intervals, contract and JSON schema verification, parallel thread-safe test execution, data-driven boundary analysis, and continuous integration workflows.

---

## Architecture Overview

```
+-------------------------------------------------------------------------+
|                                CI / CD                                  |
|     GitHub Actions (.github/workflows/ci.yml)  /  Jenkins (Jenkinsfile) |
+-------------------------------------------------------------------------+
                                    |
          +-------------------------+-------------------------+
          |                         |                         |
          v                         v                         v
+-------------------+     +-------------------+     +-------------------+
|    Java Suite     |     |   Python Suite    |     |   Newman Suite    |
| Selenium 4 (POM)  |     | Playwright (POM)  |     | Postman Runner    |
| TestNG (Parallel) |     | PyTest (-n 4)     |     | Token Chaining    |
| REST Assured      |     | Requests          |     | Schema Validation |
| Allure Reporter   |     | PyTest-HTML       |     | HTML Extra Report |
+-------------------+     +-------------------+     +-------------------+
          |                         |                         |
          +-------------------------+-------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                      System Under Test (demo-app)                       |
|   Node 24 + Express + JWT Authentication + SQLite (node:sqlite)        |
|   Static Web UI with data-testid attributes & Multi-Step Wizard Checkout|
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                        Direct SQL Assertions                            |
|             Java (sqlite-jdbc)  |  Python (sqlite3)                     |
+-------------------------------------------------------------------------+
```

---

## Directory Structure

```
AutoGate-QA/
├── demo-app/                 # System Under Test (Express + SQLite shop)
│   ├── data/                 # SQLite database storage (autogate.db)
│   ├── public/               # HTML/CSS/JS frontend with data-testid tags
│   ├── scripts/              # Database schema seed and reset scripts
│   ├── db.js                 # SQLite database connection and operations
│   ├── package.json          # Express application manifest
│   └── server.js             # REST API routes and static asset serving
├── java-tests/               # Java automation framework
│   ├── src/main/java/        # Page Object classes, BasePage, Driver manager
│   ├── src/test/java/        # TestNG test classes (UI, API, DB)
│   ├── src/test/resources/   # testng.xml, qa.properties, JSON schemas
│   └── pom.xml               # Maven configuration (Java 17 compiler release)
├── python-tests/             # Python automation framework
│   ├── pages/                # Playwright Page Objects with web-first waits
│   ├── tests/                # PyTest test modules (UI, API, DB)
│   │   ├── data/             # CSV and JSON boundary/scenario fixtures
│   │   └── schemas/          # JSON Schema contract definition files
│   ├── config.py             # Environment configuration loader
│   ├── conftest.py           # Shared fixtures, Playwright browser lifecycle
│   ├── pytest.ini            # Marker definitions and runner flags
│   └── requirements.txt      # Pinned dependency definitions
├── mobile-tests/             # Appium Android mobile automation suite
│   ├── pages/                # Mobile screen objects (UiAutomator / Accessibility)
│   ├── tests/                # Test cases targeting simplify-money-track APK
│   ├── conftest.py           # Appium driver fixture with offline skip logic
│   ├── pytest.ini            # Mobile test runner configuration
│   ├── requirements.txt      # Appium client dependencies
│   └── README.md             # Local emulator setup and execution instructions
├── postman/                  # Postman collection and environment
│   ├── AutoGate.postman_collection.json  # 15 requests with assertions and schema tests
│   └── local.postman_environment.json   # Local variables and token persistence
├── docs/                     # Test engineering documentation
│   ├── RTM.md                # Requirements Traceability Matrix
│   ├── test-plan.md          # Strategy, boundary analysis, entry/exit criteria
│   └── bug-report-template.md# Defect reporting guidelines with filled sample
├── .github/workflows/        # GitHub Actions CI workflow definitions
│   └── ci.yml                # Parallel matrix execution for Java, Python, Newman
├── Jenkinsfile               # Declarative multi-stage pipeline configuration
├── package.json              # Root package with Newman scripts and devDependencies
└── .gitignore                # Ignore rules for artifacts, logs, venvs, and databases
```

---

## Results (Measured Locally)

The metrics below represent execution results measured locally on this Linux development environment (Ubuntu, 8 vCPUs, 30 GB RAM).

| Test Suite | Framework / Runner | Total Tests / Requests | Passed | Failed | Skipped | Parallel Time | Serial Time |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Java Suite** | TestNG + Selenium 4 + REST Assured | 31 tests | 31 | 0 | 0 | 14.03 s (`thread-count=4`) | 6m 06 s (`-Dparallel=none`) |
| **Python Suite** | PyTest + Playwright + Requests | 41 tests | 41 | 0 | 0 | 3.12 s (`-n 4`) | 6.76 s (`-n 0`) |
| **Newman Suite** | Newman CLI | 15 requests (33 assertions)| 15 (33) | 0 | 0 | 286 ms | 286 ms |
| **Mobile Suite** | Appium + PyTest | 4 tests | 0 | 0 | 4 (offline skip)| N/A | 40 ms (skip) |

---

## Prerequisites

- **Java JDK**: Version 17 or 21 (`java -version`, `javac -version`)
- **Maven**: Version 3.8+ (`mvn -version`)
- **Node.js**: Version 20 or 24 (`node -v`, `npm -v`)
- **Python**: Version 3.12 or 3.13 (`python3 -v`)
- **Google Chrome**: Stable release installed at `/usr/bin/google-chrome`
- **Playwright Chromium**: Installed via Playwright CLI

---

## Starting the System Under Test (demo-app)

The demo application is a Node.js Express server with an embedded SQLite database.

1. Navigate to the `demo-app` directory and install dependencies:
   ```bash
   cd demo-app
   npm install
   ```

2. Seed or reset the SQLite database with baseline products and accounts:
   ```bash
   npm run reset
   ```

3. Start the application on port 3000:
   ```bash
   npm start
   ```

4. Verify health status in another terminal:
   ```bash
   curl http://localhost:3000/health
   # Returns: {"status":"UP","service":"AutoGate Mini Shop","timestamp":"..."}
   ```

---

## Running the Automated Test Suites

### 1. Java Regression Suite (Selenium 4, REST Assured, TestNG)

The Java suite contains 31 automated tests covering UI workflows, API endpoints, and direct SQLite JDBC persistence assertions. Parallel execution runs across 4 threads organized by test classes.

```bash
# Execute full suite in parallel (default testng.xml)
mvn -f java-tests/pom.xml clean test

# Execute sequentially (single-threaded)
mvn -f java-tests/pom.xml test -Dparallel=none -DthreadCount=1

# Execute a single test class
mvn -f java-tests/pom.xml test -Dtest=CheckoutUiTest

# Execute API tests only
mvn -f java-tests/pom.xml test -Dtest="*ApiTest"
```

### 2. Python Regression Suite (Playwright, Requests, PyTest)

The Python suite contains 41 tests, including data-driven tests parameterized from JSON and CSV fixtures.

```bash
# Set up virtual environment
python3 -m venv python-tests/.venv
source python-tests/.venv/bin/activate
pip install -r python-tests/requirements.txt
playwright install chromium

# Run full suite in parallel across 4 workers with HTML report
cd python-tests
pytest -n 4 --html=reports/report.html --self-contained-html

# Run suite sequentially
pytest -n 0

# Run specific categories using markers
pytest -m ui
pytest -m api
pytest -m db
pytest -m smoke
```

### 3. Postman / Newman REST API Suite

The collection contains 15 automated HTTP requests covering authentication, catalog, cart operations, boundary quantities, order placement, and schema validation.

```bash
# Install Newman dependencies
npm install

# Run Newman with CLI summary and HTML Extra reporter
npm run test:postman

# Or invoke directly via npx
npx newman run postman/AutoGate.postman_collection.json \
  -e postman/local.postman_environment.json \
  -r cli,htmlextra \
  --reporter-htmlextra-export reports/postman/report.html
```

### 4. Mobile Test Suite (Appium)

The mobile suite contains Page Object models and 4 test cases targeting the `com.vineet.simplifytrack` Android application.

```bash
# Verify test collection without an active device
python-tests/.venv/bin/pytest --collect-only mobile-tests/

# Execute suite (gracefully skips when Appium is offline)
python-tests/.venv/bin/pytest mobile-tests/
```

To run against an emulator, start Appium on port 4723 (`appium`), launch an Android virtual device, install the APK, and execute `pytest mobile-tests/`. See [mobile-tests/README.md](mobile-tests/README.md) for detailed instructions.

---

## Viewing Test Reports

- **Allure (Java)**:
  ```bash
  mvn -f java-tests/pom.xml allure:serve
  ```
  Or generate static files:
  ```bash
  mvn -f java-tests/pom.xml allure:report
  ```
  Output directory: `java-tests/target/site/allure-maven-plugin/index.html`.

- **PyTest HTML Report (Python)**:
  Open `python-tests/reports/report.html` in any browser.

- **Newman HTML Extra Report (Postman)**:
  Open `reports/postman/report.html` in any browser.

---

## Continuous Integration

- **GitHub Actions**: Configured in `.github/workflows/ci.yml`. Triggers on push and pull request against `main`. Runs three parallel matrix jobs (`java-ui-api`, `python-ui-api`, `newman`), starts the background service, checks health via a retry loop, executes tests headless, and archives reports on every run.
- **Jenkins**: Configured in `Jenkinsfile`. Implements declarative pipeline syntax with parallel stages, Allure test reporting, JUnit XML integration, and HTML publishing.
