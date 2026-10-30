package qa.autogate.tests.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;
import qa.autogate.pages.LoginPage;
import qa.autogate.pages.ProductsPage;
import qa.autogate.retry.RetryAnalyzer;
import qa.autogate.tests.BaseUiTest;

@Feature("Product Catalog UI")
public class ProductCatalogUiTest extends BaseUiTest {

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify product catalog renders all seeded products")
    @Description("Verify that 6 seeded products are rendered in the catalog grid")
    public void testProductCatalogDisplay() {
        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.openProductsPage();

        Assert.assertTrue(productsPage.getAllProductCards().size() >= 6, "At least 6 products should be visible");
        Assert.assertTrue(productsPage.isProductDisplayed(1), "Product 1 should be displayed");
        Assert.assertEquals(productsPage.getProductTitle(1), "UltraSync Smartwatch");
        Assert.assertEquals(productsPage.getProductPrice(1), "$199.99");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify search filter filters catalog by product name")
    @Description("Search for 'Headphones' and verify only matching card is shown")
    public void testSearchFilter() {
        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.openProductsPage();

        productsPage.search("Headphones");
        productsPage.waitForProductHidden(1);
        Assert.assertTrue(productsPage.isProductDisplayed(2), "Headphones should be displayed");
        Assert.assertFalse(productsPage.isProductDisplayed(1), "Smartwatch should be hidden");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify category filter dropdown")
    @Description("Filter by 'Displays' category and verify matching monitor card is shown")
    public void testCategoryFilter() {
        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.openProductsPage();

        productsPage.filterByCategory("Displays");
        productsPage.waitForProductHidden(1);
        Assert.assertTrue(productsPage.isProductDisplayed(6), "ViewMaster Monitor should be displayed");
        Assert.assertFalse(productsPage.isProductDisplayed(1), "Smartwatch should be filtered out");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify quantity boundary minimum (qty = 1)")
    @Description("Add product with quantity = 1 (valid boundary min) and verify cart update")
    public void testProductQuantityBoundaryMin() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("tester@autogate.qa", "Tester@123");
        loginPage.waitForLoginSuccess();

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.waitForProductsLoaded();
        productsPage.setProductQuantity(3, 1);
        productsPage.addToCart(3);
        productsPage.waitForCartToast();

        Assert.assertFalse(productsPage.isQuantityErrorDisplayed(3), "Quantity error should not appear for qty=1");
        Assert.assertTrue(productsPage.getCartBadgeCount() >= 1, "Cart badge count should be at least 1");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify quantity boundary maximum (qty = 10)")
    @Description("Set product quantity to 10 (valid boundary max) and verify no validation error")
    public void testProductQuantityBoundaryMax() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("tester@autogate.qa", "Tester@123");
        loginPage.waitForLoginSuccess();

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.waitForProductsLoaded();
        productsPage.setProductQuantity(4, 10);
        productsPage.addToCart(4);
        productsPage.waitForCartToast();

        Assert.assertFalse(productsPage.isQuantityErrorDisplayed(4), "Quantity error should not appear for qty=10");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify quantity boundary exceeded (qty = 11) triggers error")
    @Description("Set product quantity to 11 (invalid boundary max + 1) and verify validation error appears")
    public void testProductQuantityBoundaryExceededValidation() {
        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.openProductsPage();
        productsPage.setProductQuantity(1, 11);
        productsPage.addToCart(1);
        productsPage.waitForQuantityError(1);

        Assert.assertTrue(productsPage.isQuantityErrorDisplayed(1), "Validation error should be displayed for qty=11");
        Assert.assertEquals(productsPage.getQuantityErrorMessage(1), "Quantity must be between 1 and 10");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify add to cart updates header cart badge")
    @Description("User adds product and confirms header badge displays updated total item count")
    public void testAddToCartUpdatesHeaderBadge() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("shopper@autogate.qa", "Shopper@123");
        loginPage.waitForLoginSuccess();

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.waitForProductsLoaded();

        productsPage.setProductQuantity(5, 2);
        productsPage.addToCart(5);
        productsPage.waitForCartToast();

        Assert.assertTrue(productsPage.getCartBadgeCount() >= 2,
                "Cart badge count should display at least the added quantity");
    }
}
