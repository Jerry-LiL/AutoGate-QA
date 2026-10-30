package qa.autogate.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductsPage extends BasePage {
    private final By productsView = By.cssSelector("[data-testid='products-view']");
    private final By searchInput = By.cssSelector("[data-testid='search-input']");
    private final By categoryFilter = By.cssSelector("[data-testid='category-filter']");
    private final By productGrid = By.cssSelector("[data-testid='product-grid']");
    private final By cartBadge = By.cssSelector("[data-testid='cart-badge']");
    private final By navCart = By.cssSelector("[data-testid='nav-cart']");
    private final By toast = By.cssSelector("[data-testid='cart-toast']");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public void openProductsPage() {
        open("/");
        waitForVisibility(productsView);
        waitForVisibility(By.cssSelector("[data-testid='product-card-1']"));
    }

    public void waitForProductsLoaded() {
        waitForVisibility(productsView);
        waitForVisibility(By.cssSelector("[data-testid='product-card-1']"));
    }

    public void search(String keyword) {
        type(searchInput, keyword);
    }

    public void filterByCategory(String category) {
        selectByValue(categoryFilter, category);
    }

    public boolean isProductDisplayed(int productId) {
        return isDisplayed(By.cssSelector("[data-testid='product-card-" + productId + "']"));
    }

    public void waitForProductHidden(int productId) {
        waitForInvisibility(By.cssSelector("[data-testid='product-card-" + productId + "']"));
    }

    public String getProductTitle(int productId) {
        return getText(By.cssSelector("[data-testid='product-title-" + productId + "']"));
    }

    public String getProductPrice(int productId) {
        return getText(By.cssSelector("[data-testid='product-price-" + productId + "']"));
    }

    public void setProductQuantity(int productId, int quantity) {
        By locator = By.cssSelector("[data-testid='product-qty-" + productId + "']");
        WebElement input = waitForVisibility(locator);
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true })); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            input, String.valueOf(quantity));
    }

    public void addToCart(int productId) {
        click(By.cssSelector("[data-testid='add-to-cart-" + productId + "']"));
    }

    public void waitForCartToast() {
        waitForVisibility(toast);
    }

    public int getCartBadgeCount() {
        String text = getText(cartBadge);
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    public void waitForCartBadgeCount(int expectedCount) {
        wait.until(driver -> getCartBadgeCount() == expectedCount);
    }

    public void waitForQuantityError(int productId) {
        waitForVisibility(By.cssSelector("[data-testid='qty-error-" + productId + "'].visible"));
    }

    public boolean isQuantityErrorDisplayed(int productId) {
        return isDisplayed(By.cssSelector("[data-testid='qty-error-" + productId + "'].visible"));
    }

    public String getQuantityErrorMessage(int productId) {
        return getText(By.cssSelector("[data-testid='qty-error-" + productId + "']"));
    }

    public void goToCart() {
        click(navCart);
    }

    public List<WebElement> getAllProductCards() {
        return findElements(By.cssSelector("[data-testid^='product-card-']"));
    }
}
