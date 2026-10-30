package qa.autogate.tests.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;
import qa.autogate.pages.LoginPage;
import qa.autogate.retry.RetryAnalyzer;
import qa.autogate.tests.BaseUiTest;

@Feature("Authentication UI")
public class LoginUiTest extends BaseUiTest {

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify successful login with valid credentials")
    @Description("User logs in with valid admin credentials and verifies user greeting and logout button visibility")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("admin@autogate.qa", "Admin@123");
        loginPage.waitForLoginSuccess();

        Assert.assertTrue(loginPage.isUserLoggedIn(), "User should be logged in with greeting visible");
        Assert.assertTrue(loginPage.getUserGreeting().contains("Admin") || loginPage.getUserGreeting().contains("Hi"),
                "Greeting should display user info");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify error message on invalid password")
    @Description("User attempts login with incorrect password and verifies validation error message")
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("admin@autogate.qa", "WrongPassword999");
        loginPage.waitForLoginError();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message container should be visible");
        Assert.assertEquals(loginPage.getErrorMessage(), "Invalid email or password");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify HTML5 required validation when fields are empty")
    @Description("User attempts submission with empty fields and verifies user remains unauthenticated")
    public void testLoginWithEmptyFields() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("", "");

        Assert.assertFalse(loginPage.isUserLoggedIn(), "User should not be logged in with empty inputs");
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, description = "Verify user can log out successfully")
    @Description("Logged in user clicks logout and verifies session termination")
    public void testUserLogout() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.navigateToLogin();
        loginPage.login("admin@autogate.qa", "Admin@123");
        loginPage.waitForLoginSuccess();
        Assert.assertTrue(loginPage.isUserLoggedIn());

        loginPage.logout();
        Assert.assertFalse(loginPage.isUserLoggedIn(), "User greeting should be hidden after logout");
    }
}
