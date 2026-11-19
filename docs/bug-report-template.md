# Bug Report Template

## Severity Levels

- **Critical**: System crash, data loss, or blocking issue with no workaround.
- **Major**: Core feature broken, but a workaround exists.
- **Minor**: Non-critical functional or visual defect.
- **Trivial**: Cosmetic issue or typo.

## Template

```markdown
**Title**: [Component] Summary of the issue

**Issue ID**: BUG-XXXX
**Environment**:
  - App Version:
  - OS / Platform:
  - Browser / Client:
  - Base URL:

**Severity**: Critical | Major | Minor | Trivial
**Priority**: P1 | P2 | P3 | P4

**Steps to Reproduce**:
1.
2.
3.

**Expected Result**:

**Actual Result**:

**Reproducibility**: Always | Intermittent

**Attachments**:
- Logs:
- Screenshots:
- API Request / Response:
```

## Sample Report

**Title**: [Checkout] Pincode accepts trailing whitespace and fails downstream

**Issue ID**: BUG-1042
**Environment**: Chrome, Ubuntu 24.04, `http://localhost:3000`
**Severity**: Major
**Priority**: P2

**Steps to Reproduce**:
1. Sign in as `shopper@autogate.qa` and add an item to cart.
2. Go to Checkout.
3. In Shipping Details, enter pincode `560001 ` (space at end) and submit.

**Expected Result**:
Form rejects the pincode with "Pincode must be exactly 6 numeric digits".

**Actual Result**:
Whitespace is accepted and the form advances to Step 2.

**Reproducibility**: Always
