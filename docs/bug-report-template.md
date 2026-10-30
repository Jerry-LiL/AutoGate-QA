# Standard Bug Report Template

A standardized defect reporting guide for QA engineers and developers. High-quality bug reports accelerate triage and resolution by eliminating ambiguity.

---

## 1. Severity vs Priority Definitions

| Level | Severity (Technical Impact on System) | Priority (Business Urgency to Resolve) | Example |
| :--- | :--- | :--- | :--- |
| **Critical / P1** | System crash, data corruption, security vulnerability, complete blocker with no workaround. | Must fix immediately; blocks deployment or release. | Payment step crashes or orders fail to persist to database during checkout. |
| **Major / P2** | Core functionality severely impaired; acceptable workaround exists or affects specific subset of users. | Fix in current sprint or release milestone. | Search filter fails to display products matching special characters; customer cannot filter by category. |
| **Minor / P3** | Non-critical functionality failure; cosmetic or UI defect that does not block completion. | Normal priority; schedule for upcoming maintenance sprint. | Pincode error text alignment offset by 4 pixels on mobile viewports. |
| **Trivial / P4** | Minor cosmetic, typo, or minor enhancement suggestion. | Low priority; fix when time permits. | Typo in footer copyright notice or product description capitalization inconsistency. |

---

## 2. Bug Report Template (Blank)

```markdown
**Title / Summary**: [Component] Concise description of failure

**Issue ID**: BUG-XXXX
**Reported By**: [Engineer Name]
**Date**: [YYYY-MM-DD]
**Environment**:
  - App Version: [e.g. 1.0.0]
  - OS / Platform: [e.g. Ubuntu 24.04 Linux / Android 13]
  - Browser / Client: [e.g. Headless Chrome 156 / Pixel 6]
  - Backend Base URL: [e.g. http://localhost:3000]

**Severity**: Critical | Major | Minor | Trivial
**Priority**: P1 (Immediate) | P2 (High) | P3 (Normal) | P4 (Low)

**Pre-conditions**:
1. User account exists and is seeded in database.
2. System is running and healthy.

**Steps to Reproduce**:
1. Navigate to `/`
2. Perform action X
3. Click on element Y
4. Submit form Z

**Expected Result**:
[Clear statement of what should happen according to product requirements]

**Actual Result**:
[Observed incorrect behavior, including error messages or HTTP status codes]

**Reproducibility**: Always (10/10) | Intermittent (3/10)

**Attachments / Artifacts**:
- Logs: [Link to log file or console trace]
- Screenshots: [Screenshot path or diff]
- API Request / Response:
  ```json
  // Request / Response payload
  ```
```

---

## 3. Filled Sample Bug Report

**Title / Summary**: [Checkout] Pincode field allows progression when trailing whitespace is appended

**Issue ID**: BUG-1042  
**Reported By**: Lead SDET  
**Date**: 2026-10-30  
**Environment**:
- App Version: 1.0.0-snapshot
- OS / Platform: Linux (x86_64, Kernel 6.12)
- Browser / Client: Headless Chrome 156.0.8078.47 / Playwright 1.48
- Backend Base URL: `http://localhost:3000`
- Database: SQLite `autogate.db`

**Severity**: Major  
**Priority**: P2 (High)  

**Pre-conditions**:
1. Demo app initialized with `npm run reset`.
2. Authenticated as `shopper@autogate.qa` with 1 item in shopping cart.

**Steps to Reproduce**:
1. Navigate to `http://localhost:3000` and sign in.
2. Open cart and click **Proceed to Checkout**.
3. In Step 1 (Shipping Details), enter:
   - Full Name: `Alice Walker`
   - Street Address: `456 Automation Lane`
   - City: `Bengaluru`
   - Postal / Pincode: `560001 ` (6 digits followed by a space).
4. Click **Continue to Payment**.

**Expected Result**:
The shipping form validation regex should reject non-exact 6 numeric digits, display the error message `"Pincode must be exactly 6 numeric digits"`, and prevent progression to Step 2.

**Actual Result**:
The form parser trims whitespace before length validation, allowing the user to advance to Step 2 with untrimmed postal records, resulting in downstream shipping provider integration failure.

**Reproducibility**: Always (10/10)

**Attachments / Artifacts**:
- Automated Test Reference: `TestUiCheckout.test_shipping_pincode_boundary_validation[pincode containing spaces]`
- API Response:
  ```json
  {
    "error": "Validation Error",
    "field": "pincode",
    "message": "Pincode must be exactly 6 numeric digits"
  }
  ```
