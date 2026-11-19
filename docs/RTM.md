# Requirements Traceability Matrix

Maps functional requirements to automated tests across Java, Python, Newman, and Mobile suites.

| Req ID | Requirement | Tests |
| :--- | :--- | :--- |
| REQ-AUTH-01 | Login with valid credentials | `LoginUiTest`, `TestUiLogin`, `AuthApiTest`, `TestAuthApi`, Newman |
| REQ-AUTH-02 | Reject invalid password (401) | `LoginUiTest`, `TestUiLogin`, `AuthApiTest`, `TestAuthApi`, Newman |
| REQ-AUTH-03 | Reject empty login payload (400) | `AuthApiTest`, `TestAuthApi`, Newman |
| REQ-AUTH-04 | User logout terminates session | `LoginUiTest`, `TestUiLogin` |
| REQ-PROD-01 | Display product catalog | `ProductCatalogUiTest`, `TestUiCatalog`, `ProductApiTest`, `TestProductsApi`, Newman |
| REQ-PROD-02 | Fetch product details by ID | `ProductApiTest`, `TestProductsApi`, Newman |
| REQ-PROD-03 | Return 404 for nonexistent product | `ProductApiTest`, `TestProductsApi`, Newman |
| REQ-PROD-04 | Search catalog by keyword | `ProductCatalogUiTest`, `TestUiCatalog` |
| REQ-PROD-05 | Filter catalog by category | `ProductCatalogUiTest`, `TestUiCatalog`, `TestProductsApi` |
| REQ-CART-01 | Add product to cart and update count | `ProductCatalogUiTest`, `CartApiTest`, `TestCartApi`, Newman |
| REQ-CART-02 | Quantity boundaries (1-10 allowed) | `ProductCatalogUiTest`, `TestUiCatalog`, `TestCartApi`, Newman |
| REQ-CART-03 | Fetch cart contents and totals | `CartApiTest`, `TestCartApi`, Newman |
| REQ-CART-04 | Remove items and clear cart | `CartApiTest`, `TestCartApi`, Newman |
| REQ-CHK-01 | Complete checkout flow | `CheckoutUiTest`, `TestUiCheckout`, `OrderApiTest`, `TestOrdersApi`, Newman |
| REQ-CHK-02 | Require shipping fields | `CheckoutUiTest`, `TestUiCheckout`, `OrderApiTest` |
| REQ-CHK-03 | Enforce 6-digit pincode format | `CheckoutUiTest`, `TestUiCheckout`, Newman |
| REQ-CHK-04 | Preserve step state on back navigation | `CheckoutUiTest`, `TestUiCheckout` |
| REQ-CHK-05 | Fetch placed order by ID | `OrderApiTest`, `TestOrdersApi`, Newman |
| REQ-DB-01 | Order record persists with ORD- prefix | `OrderDatabaseTest`, `TestDatabasePersistence` |
| REQ-DB-02 | Order items persist with snapshot prices | `OrderDatabaseTest`, `TestDatabasePersistence` |
| REQ-DB-03 | Seed users and product stock integrity | `OrderDatabaseTest`, `TestDatabasePersistence` |
| REQ-MOB-01 | Track screen renders header | `TestMobileTrackApp.test_track_screen_header_rendered` |
| REQ-MOB-02 | Navigate to Settings screen | `TestMobileTrackApp.test_navigate_to_settings_screen` |
| REQ-MOB-03 | Reset server URL to default | `TestMobileTrackApp.test_settings_reset_server_url` |
| REQ-MOB-04 | Configure custom server URL | `TestMobileTrackApp.test_settings_custom_server_url` |
