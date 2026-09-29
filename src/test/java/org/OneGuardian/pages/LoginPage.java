package org.OneGuardian.pages;

import org.OneGuardian.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Storefront account login via GoKwik KwikPass (phone number -> OTP).
 * <p>
 * Clicking the header Account icon opens the KwikPass login flow inside an iframe overlaid on the
 * current page (iframe#iframe-kp, served from pdp.gokwik.co) rather than routing to a Shopify
 * /account page — direct navigation to /account while logged out just redirects to the homepage.
 * The same OTP-by-email mechanism used by {@link CheckoutPage} applies here, so the login OTP is
 * fetched with {@code GmailOtpReader} in the test.
 */
public class LoginPage extends BasePage {
    protected static final Logger log = LogManager.getLogger(LoginPage.class);

    // Header account icon — opens the KwikPass login iframe when logged out, or routes to the
    // account dashboard when already logged in. Two nodes carry this locator (desktop + mobile
    // dock); PageFactory takes the first (desktop header), and safeClick's JS fallback covers the
    // case where it is behind an overlay in headless runs.
    @FindBy(css = "a.header-action-btn[aria-label='Account']")
    private WebElement accountIcon;

    @FindBy(css = "iframe#iframe-kp")
    private WebElement loginIframe;

    @FindBy(id = "phone-input")
    private WebElement mobileNumberInput;

    // Same id serves both steps: it reads "Submit" on the phone screen and "Verify" on the OTP screen.
    @FindBy(id = "submit-button")
    private WebElement submitButton;

    // First of four single-digit OTP boxes (#input-0 .. #input-3); its presence marks the OTP screen.
    @FindBy(id = "input-0")
    private WebElement otpFirstInput;

    // Logged-in indicators on the account dashboard (parent page, outside the iframe).
    @FindBy(xpath = "//button[normalize-space()='Sign Out']")
    private WebElement signOutButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Clicks the header Account icon and switches into the KwikPass login iframe. */
    public void openLoginModal() {
        safeClick(accountIcon);
        switchToFrame(loginIframe);
        waitForVisibility(mobileNumberInput);
        log.info("Opened KwikPass login modal");
    }

    /** Enters the mobile number and submits it to trigger the OTP. Must already be inside the iframe. */
    public void submitMobileNumber(String phoneNumber) {
        mobileNumberInput.clear();
        mobileNumberInput.sendKeys(phoneNumber);
        safeClick(submitButton);
        log.info("Submitted mobile number for OTP");
    }

    /** True once the OTP entry screen is shown (the first OTP box is visible). */
    public boolean isOtpScreenDisplayed() {
        waitForVisibility(otpFirstInput);
        return isElementDisplayed(otpFirstInput);
    }

    /**
     * Types the OTP across the four single-digit boxes and clicks Verify. Each digit is written to
     * its own box (#input-0 .. #input-3) so this works regardless of the widget's auto-advance.
     */
    public void submitOtp(String otp) {
        waitForVisibility(otpFirstInput);
        for (int i = 0; i < otp.length() && i < 4; i++) {
            WebElement box = driver.findElement(By.id("input-" + i));
            box.clear();
            box.sendKeys(String.valueOf(otp.charAt(i)));
        }
        safeClick(submitButton);
        log.info("Submitted OTP");
    }

    /** True if KwikPass shows the "OTP verification failed." error (invalid/expired OTP). */
    public boolean isOtpErrorDisplayed() {
        try {
            WebElement error = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(normalize-space(),'OTP verification failed')]")));
            return error.isDisplayed();
        } catch (Exception e) {
            log.error("OTP failure message not shown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifies login succeeded. On success KwikPass closes the iframe; re-opening the Account icon
     * then routes to the account dashboard (Sign Out button) instead of reopening the login modal.
     */
    public boolean isLoginSuccessful() {
        switchToDefaultContent();
        try {
            waitForInvisibility(loginIframe);
        } catch (Exception ignored) {
            // Iframe already gone — treat as closed and continue to the definitive check below.
        }
        safeClick(accountIcon);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOf(signOutButton));
            log.info("Login successful — account dashboard shown");
            return true;
        } catch (Exception e) {
            log.error("Sign Out button not found — login not confirmed: " + e.getMessage());
            return false;
        }
    }
}
