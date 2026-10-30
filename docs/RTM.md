# Requirements Traceability Matrix (RTM)

This document maps business and functional requirements to test case identifiers and exact automated test method names across the Java, Python, Postman, and Mobile test suites.

## 1. Authentication & Session Management

| Req ID | Requirement Description | Test Case ID | Test Level | Automated Test Name |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-AUTH-01** | User can authenticate with valid email and password to receive a JWT token | TC-AUTH-01 | UI (Java) | `LoginUiTest.testSuccessfulLoginWithValidCredentials` |
| | | TC-AUTH-02 | UI (Python) | `TestUiLogin.test_successful_login` |
| | | TC-AUTH-03 | API (Java) | `AuthApiTest.testLoginSuccessReturnsJwtToken` |
| | | TC-AUTH-04 | API (Python) | `TestAuthApi.test_login_scenarios[valid_admin_login]`, `TestAuthApi.test_login_scenarios[valid_tester_login]` |
| | | TC-AUTH-05 | API (Newman) | `Auth - Login Admin Success`, `Auth - Login Tester Success (Chaining)` |
| **REQ-AUTH-02** | System rejects invalid password with 401 Unauthorized status | TC-AUTH-06 | UI (Java) | `LoginUiTest.testLoginFailureWithInvalidPassword` |
| | | TC-AUTH-07 | UI (Python) | `TestUiLogin.test_invalid_password_shows_error` |
| | | TC-AUTH-08 | API (Java) | `AuthApiTest.testLoginInvalidCredentialsReturns401` |
| | | TC-AUTH-09 | API (Python) | `TestAuthApi.test_login_scenarios[invalid_password]` |
| | | TC-AUTH-10 | API (Newman) | `Auth - Login Invalid Password` |
| **REQ-AUTH-03** | System validates mandatory credentials on login submission | TC-AUTH-11 | API (Java) | `AuthApiTest.testLoginMissingPayloadReturns400` |
| | | TC-AUTH-12 | API (Python) | `TestAuthApi.test_login_scenarios[empty_credentials]` |
| | | TC-AUTH-13 | API (Newman) | `Auth - Login Empty Credentials Validation` |
| **REQ-AUTH-04** | User can terminate authenticated session (logout) | TC-AUTH-14 | UI (Java) | `LoginUiTest.testUserLogout` |
| | | TC-AUTH-15 | UI (Python) | `TestUiLogin.test_logout_clears_session` |

---

## 2. Product Catalog & Search

| Req ID | Requirement Description | Test Case ID | Test Level | Automated Test Name |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-PROD-01** | Display all seeded active products with image, title, price, and stock | TC-PROD-01 | UI (Java) | `ProductCatalogUiTest.testProductCatalogDisplay` |
| | | TC-PROD-02 | UI (Python) | `TestUiCatalog.test_catalog_renders_seeded_products` |
| | | TC-PROD-03 | API (Java) | `ProductApiTest.testGetAllProductsReturnsCatalog` |
| | | TC-PROD-04 | API (Python) | `TestProductsApi.test_get_all_products` |
| | | TC-PROD-05 | API (Newman) | `Products - Get All Products` |
| **REQ-PROD-02** | Retrieve single product details by identifier | TC-PROD-06 | API (Java) | `ProductApiTest.testGetProductByIdReturnsDetails` |
| | | TC-PROD-07 | API (Python) | `TestProductsApi.test_get_product_by_id_valid` |
| | | TC-PROD-08 | API (Newman) | `Products - Get By ID Valid` |
| **REQ-PROD-03** | Return 404 Not Found for non-existent product ID query | TC-PROD-09 | API (Java) | `ProductApiTest.testGetNonExistentProductReturns404` |
| | | TC-PROD-10 | API (Python) | `TestProductsApi.test_get_product_by_id_not_found` |
| | | TC-PROD-11 | API (Newman) | `Products - Get By ID Non-Existent` |
| **REQ-PROD-04** | Search catalog products by keyword substring | TC-PROD-12 | UI (Java) | `ProductCatalogUiTest.testSearchFilter` |
| | | TC-PROD-13 | UI (Python) | `TestUiCatalog.test_search_filters_products` |
| **REQ-PROD-05** | Filter catalog products by category | TC-PROD-14 | UI (Java) | `ProductCatalogUiTest.testCategoryFilter` |
| | | TC-PROD-15 | UI (Python) | `TestUiCatalog.test_category_dropdown_filters_products` |
| | | TC-PROD-16 | API (Python) | `TestProductsApi.test_filter_products_by_category` |

---

## 3. Shopping Cart Management

| Req ID | Requirement Description | Test Case ID | Test Level | Automated Test Name |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-CART-01** | Add product item to cart and increment header badge count | TC-CART-01 | UI (Java) | `ProductCatalogUiTest.testAddToCartUpdatesCartBadge` |
| | | TC-CART-02 | API (Java) | `CartApiTest.testAddProductToCart` |
| | | TC-CART-03 | API (Python) | `TestCartApi.test_add_item_to_cart` |
| | | TC-CART-04 | API (Newman) | `Cart - Add Product` |
| **REQ-CART-02** | Validate item quantity boundary constraints (allowed integer 1 to 10) | TC-CART-05 | UI (Java) | `ProductCatalogUiTest.testProductQuantityBoundaryMin`, `ProductCatalogUiTest.testProductQuantityBoundaryMax`, `ProductCatalogUiTest.testProductQuantityBoundaryExceededValidation` |
| | | TC-CART-06 | UI (Python) | `TestUiCatalog.test_quantity_boundaries[TC_QTY_01]` through `[TC_QTY_06]` |
| | | TC-CART-07 | API (Python) | `TestCartApi.test_add_cart_item_validation_boundary` |
| | | TC-CART-08 | API (Newman) | `Cart - Boundary Quantity Exceeded (15)` |
| **REQ-CART-03** | Fetch active cart contents with subtotal and grand total calculation | TC-CART-09 | API (Java) | `CartApiTest.testGetCurrentUserCart` |
| | | TC-CART-10 | API (Python) | `TestCartApi.test_get_cart_items` |
| | | TC-CART-11 | API (Newman) | `Cart - Get Current Cart` |
| **REQ-CART-04** | Remove line item or clear entire cart | TC-CART-12 | API (Java) | `CartApiTest.testRemoveItemFromCart`, `CartApiTest.testClearCart` |
| | | TC-CART-13 | API (Python) | `TestCartApi.test_remove_cart_item` |
| | | TC-CART-14 | API (Newman) | `Cart - Clear Cart` |

---

## 4. Multi-Step Checkout & Order Placement

| Req ID | Requirement Description | Test Case ID | Test Level | Automated Test Name |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-CHK-01** | Multi-step checkout wizard flow (Shipping -> Payment -> Review -> Confirmation) | TC-CHK-01 | UI (Java) | `CheckoutUiTest.testCompleteEndToEndCheckoutFlow` |
| | | TC-CHK-02 | UI (Python) | `TestUiCheckout.test_complete_checkout_flow` |
| | | TC-CHK-03 | API (Java) | `OrderApiTest.testCreateOrderSuccess` |
| | | TC-CHK-04 | API (Python) | `TestOrdersApi.test_create_order_direct_payload` |
| | | TC-CHK-05 | API (Newman) | `Orders - Create Order (Chaining)` |
| **REQ-CHK-02** | Mandatory shipping field validation (Full Name, Address, City) | TC-CHK-06 | UI (Java) | `CheckoutUiTest.testShippingValidationRequiresName` |
| | | TC-CHK-07 | UI (Python) | `TestUiCheckout.test_shipping_requires_name` |
| | | TC-CHK-08 | API (Java) | `OrderApiTest.testCreateOrderValidationInvalidShipping` |
| **REQ-CHK-03** | Pincode boundary validation (strictly 6 numeric digits required) | TC-CHK-09 | UI (Java) | `CheckoutUiTest.testShippingValidationRejectsInvalidPincodeBoundary` |
| | | TC-CHK-10 | UI (Python) | `TestUiCheckout.test_shipping_pincode_boundary_validation[...]` |
| | | TC-CHK-11 | API (Newman) | `Orders - Pincode Boundary Validation (5 digits)` |
| **REQ-CHK-04** | Wizard navigation allows backtracking to previous steps without loss of input | TC-CHK-12 | UI (Java) | `CheckoutUiTest.testPaymentStepBackNavigation` |
| | | TC-CHK-13 | UI (Python) | `TestUiCheckout.test_back_navigation_preserves_state` |
| **REQ-CHK-05** | Order query by ID retrieves persisted order record | TC-CHK-14 | API (Java) | `OrderApiTest.testGetOrderById` |
| | | TC-CHK-15 | API (Python) | `TestOrdersApi.test_get_order_by_id` |
| | | TC-CHK-16 | API (Newman) | `Orders - Get Created Order By ID` |

---

## 5. Database Persistence & Data Integrity

| Req ID | Requirement Description | Test Case ID | Test Level | Automated Test Name |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-DB-01** | Orders table records confirmed orders with unique ORD- prefix and shipping metadata | TC-DB-01 | DB (Java JDBC) | `OrderDatabaseTest.testDirectOrderDatabasePersistence` |
| | | TC-DB-02 | DB (Python SQL) | `TestDatabasePersistence.test_order_persistence_in_database` |
| **REQ-DB-02** | Order items table correctly records line item snapshot prices and quantities | TC-DB-03 | DB (Java JDBC) | `OrderDatabaseTest.testDirectOrderDatabasePersistence` |
| | | TC-DB-04 | DB (Python SQL) | `TestDatabasePersistence.test_order_persistence_in_database` |
| **REQ-DB-03** | Seeded user credentials and products table maintain referential integrity | TC-DB-05 | DB (Java JDBC) | `OrderDatabaseTest.testCatalogProductStockIntegrity` |
| | | TC-DB-06 | DB (Python SQL) | `TestDatabasePersistence.test_seeded_users_in_database`, `TestDatabasePersistence.test_seeded_products_in_database` |

---

## 6. Mobile Application Integration

| Req ID | Requirement Description | Test Case ID | Test Level | Automated Test Name |
| :--- | :--- | :--- | :--- | :--- |
| **REQ-MOB-01** | Track app renders transaction header and settings access action | TC-MOB-01 | Mobile (Appium) | `TestMobileTrackApp.test_track_screen_header_rendered` |
| **REQ-MOB-02** | User can navigate into Settings screen from primary view | TC-MOB-02 | Mobile (Appium) | `TestMobileTrackApp.test_navigate_to_settings_screen` |
| **REQ-MOB-03** | Reset server URL to default action functions properly | TC-MOB-03 | Mobile (Appium) | `TestMobileTrackApp.test_settings_reset_server_url` |
| **REQ-MOB-04** | User can configure custom backend server URL and trigger resync | TC-MOB-04 | Mobile (Appium) | `TestMobileTrackApp.test_settings_custom_server_url` |
