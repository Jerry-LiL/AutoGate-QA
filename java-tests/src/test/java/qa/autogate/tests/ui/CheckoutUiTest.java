package qa.autogate.tests.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;
import qa.autogate.pages.CartPage;
import qa.autogate.pages.CheckoutPage;
import qa.autogate.pages.LoginPage;
import qa.autogate.pages.ProductsPage;
import qa.autogate.retry.RetryAnalyzer;
import qa.autogate.tests.BaseUiTest;

@Feature("Checkout & Order Placement UI")
public class CheckoutUiTest extends BaseUiTest {

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify complete end-to-end checkout flow across all wizard steps")
    @Description("Add product, open cart, proceed to checkout, enter shipping, card payment, review and confirm order")
    public void testCompleteEndToEndCheckoutFlow() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("shopper@autogate.qa", "Shopper@123");
        loginPage.waitForLoginSuccess();

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.waitForProductsLoaded();
        productsPage.setProductQuantity(1, 1);
        productsPage.addToCart(1);
        productsPage.waitForCartToast();
        productsPage.goToCart();

        CartPage cartPage = new CartPage(getDriver());
        cartPage.waitForCartLoaded();
        Assert.assertTrue(cartPage.isItemInCart(1), "Product 1 should appear in cart");
        cartPage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.waitForStep1();
        checkoutPage.fillShipping("Alice Tester", "456 Automation Lane", "Bengaluru", "560001");
        checkoutPage.clickStep1Next();

        checkoutPage.waitForStep2();
        checkoutPage.fillCardPayment("4111222233334444", "12/28", "888");
        checkoutPage.clickStep2Next();

        checkoutPage.waitForStep3();
        Assert.assertTrue(checkoutPage.getReviewShippingDetails().contains("Alice Tester"));
        Assert.assertTrue(checkoutPage.getReviewPaymentDetails().contains("CARD"));
        checkoutPage.placeOrder();

        checkoutPage.waitForConfirmation();
        String orderNum = checkoutPage.getOrderConfirmationNumber();
        Assert.assertNotNull(orderNum, "Order number should be generated");
        Assert.assertTrue(orderNum.startsWith("ORD-"), "Order number format should start with ORD-");
        Assert.assertEquals(checkoutPage.getOrderConfirmationStatus(), "CONFIRMED");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify shipping validation triggers error when full name is missing")
    @Description("Attempt step 1 progression with empty name field and verify validation error")
    public void testShippingValidationRequiresName() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("shopper@autogate.qa", "Shopper@123");
        loginPage.waitForLoginSuccess();

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.waitForProductsLoaded();
        productsPage.setProductQuantity(2, 1);
        productsPage.addToCart(2);
        productsPage.waitForCartToast();
        productsPage.goToCart();

        CartPage cartPage = new CartPage(getDriver());
        cartPage.waitForCartLoaded();
        Assert.assertTrue(cartPage.isItemInCart(2), "Product 2 should appear in cart");
        cartPage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.waitForStep1();
        checkoutPage.fillShipping("", "123 Valid Street", "Bengaluru", "560001");
        checkoutPage.clickStep1Next();
        checkoutPage.waitForShippingNameError();

        Assert.assertTrue(checkoutPage.isShippingNameErrorVisible(), "Error message for shipping name must be visible");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify shipping validation rejects invalid pincode boundary (5 digits)")
    @Description("Enter 5-digit pincode (boundary length - 1) and verify rejection error")
    public void testShippingValidationRejectsInvalidPincodeBoundary() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("shopper@autogate.qa", "Shopper@123");
        loginPage.waitForLoginSuccess();

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.waitForProductsLoaded();
        productsPage.setProductQuantity(3, 1);
        productsPage.addToCart(3);
        productsPage.waitForCartToast();
        productsPage.goToCart();

        CartPage cartPage = new CartPage(getDriver());
        cartPage.waitForCartLoaded();
        Assert.assertTrue(cartPage.isItemInCart(3), "Product 3 should appear in cart");
        cartPage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.waitForStep1();
        checkoutPage.fillShipping("Valid User", "123 Valid Street", "Bengaluru", "56001"); // 5 digits
        checkoutPage.clickStep1Next();
        checkoutPage.waitForShippingPincodeError();

        Assert.assertTrue(checkoutPage.isShippingPincodeErrorVisible(), "Pincode error should be visible for 5 digits");
        Assert.assertEquals(checkoutPage.getShippingPincodeError(), "Pincode must be exactly 6 numeric digits");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify back navigation between checkout steps retains workflow state")
    @Description("Navigate from shipping to payment, then click Back to return to shipping")
    public void testPaymentStepBackNavigation() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("shopper@autogate.qa", "Shopper@123");
        loginPage.waitForLoginSuccess();

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.waitForProductsLoaded();
        productsPage.setProductQuantity(4, 1);
        productsPage.addToCart(4);
        productsPage.waitForCartToast();
        productsPage.goToCart();

        CartPage cartPage = new CartPage(getDriver());
        cartPage.waitForCartLoaded();
        Assert.assertTrue(cartPage.isItemInCart(4), "Product 4 should appear in cart");
        cartPage.proceedToCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.waitForStep1();
        checkoutPage.fillShipping("Back Tester", "789 Workflow Avenue", "Mumbai", "400001");
        checkoutPage.clickStep1Next();

        checkoutPage.waitForStep2();
        checkoutPage.clickStep2Back();
        checkoutPage.waitForStep1();
    }
}
