package qa.autogate.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private final By loginView = By.cssSelector("[data-testid='login-view']");
    private final By emailInput = By.cssSelector("[data-testid='login-email']");
    private final By passwordInput = By.cssSelector("[data-testid='login-password']");
    private final By submitButton = By.cssSelector("[data-testid='login-submit']");
    private final By errorMessage = By.cssSelector("[data-testid='login-error']");
    private final By userGreeting = By.cssSelector("[data-testid='user-greeting']");
    private final By logoutButton = By.cssSelector("[data-testid='nav-logout']");
    private final By navLogin = By.cssSelector("[data-testid='nav-login']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLogin() {
        open("/");
        click(navLogin);
        waitForVisibility(loginView);
    }

    public void login(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(submitButton);
    }

    public void waitForLoginSuccess() {
        waitForVisibility(userGreeting);
        waitForVisibility(logoutButton);
        waitForVisibility(By.cssSelector("[data-testid='products-view'].active"));
    }

    public void waitForLoginError() {
        waitForVisibility(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getUserGreeting() {
        return getText(userGreeting);
    }

    public boolean isUserLoggedIn() {
        return isDisplayed(userGreeting) && isDisplayed(logoutButton);
    }

    public void logout() {
        click(logoutButton);
        waitForVisibility(loginView);
        waitForVisibility(navLogin);
    }
}
