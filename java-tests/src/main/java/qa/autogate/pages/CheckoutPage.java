package qa.autogate.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {
    // Step Indicators
    private final By step1Indicator = By.cssSelector("[data-testid='step-indicator-1']");
    private final By step2Indicator = By.cssSelector("[data-testid='step-indicator-2']");
    private final By step3Indicator = By.cssSelector("[data-testid='step-indicator-3']");
    private final By step4Indicator = By.cssSelector("[data-testid='step-indicator-4']");

    // Step 1: Shipping
    private final By step1Container = By.cssSelector("[data-testid='checkout-step-1']");
    private final By shippingName = By.cssSelector("[data-testid='shipping-name']");
    private final By shippingAddress = By.cssSelector("[data-testid='shipping-address']");
    private final By shippingCity = By.cssSelector("[data-testid='shipping-city']");
    private final By shippingPincode = By.cssSelector("[data-testid='shipping-pincode']");
    private final By errorShippingName = By.cssSelector("[data-testid='error-shipping-name']");
    private final By errorShippingAddress = By.cssSelector("[data-testid='error-shipping-address']");
    private final By errorShippingCity = By.cssSelector("[data-testid='error-shipping-city']");
    private final By errorShippingPincode = By.cssSelector("[data-testid='error-shipping-pincode']");
    private final By step1NextBtn = By.cssSelector("[data-testid='step1-next-btn']");

    // Step 2: Payment
    private final By step2Container = By.cssSelector("[data-testid='checkout-step-2']");
    private final By paymentMethodSelect = By.cssSelector("[data-testid='payment-method-select']");
    private final By cardNumber = By.cssSelector("[data-testid='card-number']");
    private final By cardExpiry = By.cssSelector("[data-testid='card-expiry']");
    private final By cardCvv = By.cssSelector("[data-testid='card-cvv']");
    private final By errorCardNumber = By.cssSelector("[data-testid='error-card-number']");
    private final By errorCardExpiry = By.cssSelector("[data-testid='error-card-expiry']");
    private final By errorCardCvv = By.cssSelector("[data-testid='error-card-cvv']");
    private final By step2BackBtn = By.cssSelector("[data-testid='step2-back-btn']");
    private final By step2NextBtn = By.cssSelector("[data-testid='step2-next-btn']");

    // Step 3: Review
    private final By step3Container = By.cssSelector("[data-testid='checkout-step-3']");
    private final By reviewShippingDetails = By.cssSelector("[data-testid='review-shipping-details']");
    private final By reviewPaymentDetails = By.cssSelector("[data-testid='review-payment-details']");
    private final By reviewItemsList = By.cssSelector("[data-testid='review-items-list']");
    private final By reviewTotalAmount = By.cssSelector("[data-testid='review-total-amount']");
    private final By step3BackBtn = By.cssSelector("[data-testid='step3-back-btn']");
    private final By placeOrderBtn = By.cssSelector("[data-testid='place-order-btn']");
    private final By checkoutErrorMsg = By.cssSelector("[data-testid='checkout-error-msg']");

    // Step 4: Confirmation
    private final By step4Container = By.cssSelector("[data-testid='checkout-step-4']");
    private final By orderSuccessBanner = By.cssSelector("[data-testid='order-success-banner']");
    private final By orderConfirmationNumber = By.cssSelector("[data-testid='order-confirmation-number']");
    private final By orderConfirmationStatus = By.cssSelector("[data-testid='order-confirmation-status']");
    private final By orderConfirmationTotal = By.cssSelector("[data-testid='order-confirmation-total']");
    private final By orderConfirmationShipping = By.cssSelector("[data-testid='order-confirmation-shipping']");
    private final By continueShoppingBtn = By.cssSelector("[data-testid='continue-shopping-btn']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void waitForStep1() {
        waitForVisibility(step1Container);
    }

    public void fillShipping(String name, String address, String city, String pincode) {
        type(shippingName, name);
        type(shippingAddress, address);
        type(shippingCity, city);
        type(shippingPincode, pincode);
    }

    public void clickStep1Next() {
        click(step1NextBtn);
    }

    public boolean isShippingNameErrorVisible() {
        return isDisplayed(By.cssSelector("[data-testid='error-shipping-name'].visible"));
    }

    public boolean isShippingPincodeErrorVisible() {
        return isDisplayed(By.cssSelector("[data-testid='error-shipping-pincode'].visible"));
    }

    public String getShippingPincodeError() {
        return getText(errorShippingPincode);
    }

    public void waitForStep2() {
        waitForVisibility(step2Container);
    }

    public void fillCardPayment(String number, String expiry, String cvv) {
        selectByValue(paymentMethodSelect, "CARD");
        type(cardNumber, number);
        type(cardExpiry, expiry);
        type(cardCvv, cvv);
    }

    public void selectPaymentMethod(String method) {
        selectByValue(paymentMethodSelect, method);
    }

    public void clickStep2Next() {
        click(step2NextBtn);
    }

    public void clickStep2Back() {
        click(step2BackBtn);
    }

    public boolean isCardNumberErrorVisible() {
        return isDisplayed(By.cssSelector("[data-testid='error-card-number'].visible"));
    }

    public void waitForStep3() {
        waitForVisibility(step3Container);
    }

    public String getReviewShippingDetails() {
        return getText(reviewShippingDetails);
    }

    public String getReviewPaymentDetails() {
        return getText(reviewPaymentDetails);
    }

    public double getReviewTotal() {
        return Double.parseDouble(getText(reviewTotalAmount));
    }

    public void clickStep3Back() {
        click(step3BackBtn);
    }

    public void placeOrder() {
        click(placeOrderBtn);
    }

    public void waitForConfirmation() {
        waitForVisibility(step4Container);
        waitForVisibility(orderSuccessBanner);
    }

    public String getOrderConfirmationNumber() {
        return getText(orderConfirmationNumber);
    }

    public String getOrderConfirmationStatus() {
        return getText(orderConfirmationStatus);
    }

    public double getOrderConfirmationTotal() {
        return Double.parseDouble(getText(orderConfirmationTotal));
    }

    public void clickContinueShopping() {
        click(continueShoppingBtn);
    }
}
