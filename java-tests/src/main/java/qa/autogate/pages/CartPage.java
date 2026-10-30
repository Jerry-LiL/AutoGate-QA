package qa.autogate.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {
    private final By cartView = By.cssSelector("[data-testid='cart-view']");
    private final By emptyCartMsg = By.cssSelector("[data-testid='empty-cart-msg']");
    private final By cartItemsList = By.cssSelector("[data-testid='cart-items-list']");
    private final By cartTotalAmount = By.cssSelector("[data-testid='cart-total-amount']");
    private final By clearCartBtn = By.cssSelector("[data-testid='clear-cart-btn']");
    private final By proceedCheckoutBtn = By.cssSelector("[data-testid='proceed-checkout-btn']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void waitForCartLoaded() {
        waitForVisibility(cartView);
    }

    public void waitForItemInCart(int productId) {
        waitForVisibility(By.cssSelector("[data-testid='cart-row-" + productId + "']"));
    }

    public boolean isItemInCart(int productId) {
        try {
            waitForItemInCart(productId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getItemTitle(int productId) {
        return getText(By.cssSelector("[data-testid='cart-item-title-" + productId + "']"));
    }

    public String getItemPrice(int productId) {
        return getText(By.cssSelector("[data-testid='cart-item-price-" + productId + "']"));
    }

    public int getItemQuantity(int productId) {
        String val = getText(By.cssSelector("[data-testid='cart-item-qty-" + productId + "']"));
        return Integer.parseInt(val);
    }

    public double getCartTotal() {
        String val = getText(cartTotalAmount);
        return Double.parseDouble(val);
    }

    public void removeItem(int productId) {
        click(By.cssSelector("[data-testid='cart-item-remove-" + productId + "']"));
    }

    public void clearCart() {
        click(clearCartBtn);
    }

    public boolean isEmptyCartMessageDisplayed() {
        return isDisplayed(emptyCartMsg);
    }

    public void proceedToCheckout() {
        click(proceedCheckoutBtn);
    }
}
