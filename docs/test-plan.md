# Test Plan: AutoGate QA Regression & Automation Suite

## 1. Introduction & Objectives

The AutoGate QA suite is an end-to-end multi-layer regression automation framework designed to validate the reliability, functional correctness, schema conformity, and data integrity of the AutoGate mini shop system.

The primary objective is to guarantee zero regression across web UI, JSON REST API endpoints, database transactions, and mobile interfaces through automated continuous testing.

---

## 2. Scope

### In-Scope
- **Web UI Automation:** Complete customer workflows including authentication, catalog browsing, real-time filtering, cart mutations, and multi-step checkout (Shipping -> Payment -> Review -> Order Confirmation).
- **REST API Automation:** All endpoints under `/api/auth`, `/api/products`, `/api/cart`, `/api/orders`, and system `/health`.
- **Schema Validation:** Strict JSON Schema conformance validation (draft-07) across all API responses.
- **Database Persistence Assertions:** Direct SQL validations against the SQLite backing store verifying order headers, order items, stock values, and referential integrity.
- **Data-Driven Boundary Testing:** Boundary Value Analysis (BVA) and Equivalence Partitioning (EP) for item quantities and postal pincodes.
- **Continuous Integration:** Parallel headless test execution on pull request and push events via GitHub Actions and Jenkins.

### Out-of-Scope
- Hardware-dependent automated mobile execution in headless CI environments (tested locally via Appium against an Android emulator).
- Performance and stress load testing beyond functional concurrency.
- Production payment gateway integration (mocked via internal validation engine).

---

## 3. Test Approach & Architecture

The testing pyramid is applied strictly across multiple isolated test harnesses:

1. **API Layer (Fast Regression):**
   - **Java:** REST Assured with `io.restassured:json-schema-validator`.
   - **Python:** Requests library combined with `jsonschema`.
   - **Newman:** Command-line Postman execution with environment variable chaining and assertion scripting.
2. **UI Layer (End-to-End Workflow Validation):**
   - **Java:** Selenium WebDriver 4 with Page Object Model (POM), ThreadLocal driver management for parallel safety, and explicit `WebDriverWait` waits exclusively (zero `Thread.sleep`).
   - **Python:** Playwright Sync API with Page Object Model, native web-first assertions, and parallel execution via `pytest-xdist`.
3. **Database Layer (Verification of Truth):**
   - Direct JDBC (Java) and SQLite3 (Python) queries asserting database row state matching API and UI outputs.
4. **Mobile Layer (Android Appium):**
   - Appium Python Client with Page Object Model targeting `com.vineet.simplifytrack`.

---

## 4. Equivalence Partitioning & Boundary Value Analysis Matrix

### 4.1 Product Quantity Input (Allowed Range: 1 to 10 inclusive)

| Partition Class | Input Value | Condition / Boundary Type | Expected Behavior | Automated Test Reference |
| :--- | :--- | :--- | :--- | :--- |
| **Invalid Low** | `< 1` (e.g. `0`, `-1`) | Min - 1 / Negative boundary | Rejected; error "Quantity must be between 1 and 10" | `TestUiCatalog.test_quantity_boundaries[TC_QTY_01]`, `[TC_QTY_06]` |
| **Valid Boundary Min** | `1` | Exact lower boundary ($B_{min}$) | Accepted; item added to cart with count 1 | `testProductQuantityBoundaryMin`, `[TC_QTY_02]` |
| **Valid Interior** | `2` to `9` (e.g. `2`, `5`) | Nominal interior partition | Accepted; line item subtotal updated | `[TC_QTY_03]` |
| **Valid Boundary Max** | `10` | Exact upper boundary ($B_{max}$) | Accepted; item added to cart with count 10 | `testProductQuantityBoundaryMax`, `[TC_QTY_04]` |
| **Invalid High** | `> 10` (e.g. `11`, `15`) | Max + 1 / Exceeded boundary | Rejected; error "Quantity must be between 1 and 10" | `testProductQuantityBoundaryExceededValidation`, `[TC_QTY_05]`, `Cart - Boundary Quantity Exceeded (15)` |

### 4.2 Shipping Postal Pincode (Constraint: Strictly 6 numeric digits)

| Partition Class | Input Value | Character Length | Validation Status | Expected Error / Result | Automated Test Reference |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Invalid Short** | `"56001"` | 5 digits ($L_{min} - 1$) | Rejected | "Pincode must be exactly 6 numeric digits" | `testShippingValidationRejectsInvalidPincodeBoundary`, `pincode_boundary_cases[5-digit]` |
| **Valid Boundary** | `"560001"` | 6 digits (exact) | Accepted | Progresses to Step 2 (Payment) | `testCompleteEndToEndCheckoutFlow`, `pincode_boundary_cases[6-digit]` |
| **Invalid Long** | `"5600001"` | 7 digits ($L_{max} + 1$) | Rejected | "Pincode must be exactly 6 numeric digits" | `pincode_boundary_cases[7-digit]` |
| **Invalid Non-Numeric** | `"56A001"` | 6 characters | Rejected | "Pincode must be exactly 6 numeric digits" | `pincode_boundary_cases[alphanumeric]` |
| **Invalid Whitespace** | `"56 001"` | 6 characters | Rejected | "Pincode must be exactly 6 numeric digits" | `pincode_boundary_cases[spaces]` |

---

## 5. Entry & Exit Criteria

### Entry Criteria
1. System Under Test (SUT) starts successfully on designated port (`3000`) and reports `{"status":"UP"}` on `/health`.
2. Seed script executes cleanly (`npm run reset`) and populates baseline users and catalog products.
3. Node.js (20+), Java JDK (17/21), Python (3.12+), and Chrome browser are installed on the execution agent.

### Exit Criteria
1. 100% of automated tests pass across Java TestNG (31 tests), Python PyTest (41 tests), and Newman (15 requests, 33 assertions).
2. Zero arbitrary sleep statements (`Thread.sleep`, `time.sleep`) present in production test code.
3. Allure, PyTest HTML, and Newman execution reports generated and archived.
4. Clean teardown with no orphaned database locks or unclosed browser sessions.

---

## 6. Risk Assessment & Mitigation Strategy

| Risk Factor | Impact | Likelihood | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| **Test Data Contamination in Parallel Runs** | High | High | Segregated seeded user accounts per test class (`admin`, `tester`, `shopper`); unique cart resets before mutating operations. |
| **Headless Browser Flakiness / Stale Elements** | Medium | Medium | Exclusive use of explicit `WebDriverWait` / Playwright web-first assertions; standard `IRetryAnalyzer` (max 1 retry) for transient network timeouts. |
| **Schema Drift between Frontend & Backend** | High | Low | Automated JSON Schema validation asserting contracts on all API responses in both Java and Python suites. |
| **Appium Emulator Resource Exhaustion in CI** | High | High | Explicit decoupling: mobile tests are maintained locally with honest skip handling (`pytest.skip`) when Appium server is unreachable. |
