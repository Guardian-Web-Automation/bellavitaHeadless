package org.OneGuardian.pages;

import org.OneGuardian.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * GoKwik renders the entire checkout flow (mobile login, OTP, payment) inside an iframe
 * overlaid on the cart page rather than redirecting to a separate checkout domain.
 */
public class CheckoutPage extends BasePage {
    protected static final Logger log = LogManager.getLogger(CheckoutPage.class);

    @FindBy(css = "iframe.gokwik-iframe")
    private WebElement checkoutIframe;

    @FindBy(id = "phone-input")
    private WebElement mobileNumberInput;

    @FindBy(id = "continue-button")
    private WebElement continueButton;

    // The OTP field itself has no id/name/data-testid, but "new-input" is a class unique to it —
    // the other inputs on this screen are #discount (coupon code), #phone-input, and #notifications.
    @FindBy(css = "input.new-input")
    private WebElement otpInput;

    @FindBy(css = "[data-testid='payment-method-cod']")
    private WebElement codPaymentOption;

    // GoKwik sometimes shows a "Please confirm the order" upsell popup (nudging toward prepaid)
    // before actually placing a COD order, and sometimes places it directly — no id/data-testid
    // on this button, so it's matched by its exact text instead.
    @FindBy(xpath = "//button[normalize-space()='Confirm']")
    private WebElement orderConfirmButton;

    @FindBy(xpath = "//h2[contains(text(),'Thanks for your order')]")
    private WebElement thankYouHeading;

    @FindBy(xpath = "//p[contains(text(),'Order ID')]")
    private WebElement orderIdText;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void switchToCheckoutIframe() {
        switchToFrame(checkoutIframe);
    }

    public void enterMobileNumberAndContinue(String phoneNumber) {
        waitForVisibility(mobileNumberInput);
        mobileNumberInput.clear();
        mobileNumberInput.sendKeys(phoneNumber);
        safeClick(continueButton);
        log.info("Entered mobile number and clicked continue");
    }

    public void enterOtp(String otp) {
        waitForVisibility(otpInput);
        otpInput.sendKeys(otp);
        log.info("Entered OTP");
    }

    public void selectCashOnDelivery() {
        safeClick(codPaymentOption);
        log.info("Selected Cash on Delivery payment option");
        confirmOrderIfPromptShown();
    }

    // The confirmation popup only shows up intermittently, so this is a short opportunistic
    // wait rather than the class-wide explicit wait — a normal (no-popup) run shouldn't be
    // slowed down waiting for something that isn't coming.
    private void confirmOrderIfPromptShown() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(orderConfirmButton))
                    .click();
            log.info("Confirmed order on the COD confirmation popup");
        } catch (Exception e) {
            log.info("No order-confirmation popup appeared — COD order placed directly");
        }
    }

    // Selecting COD places the order immediately and top-level-navigates (out of the iframe)
    // to GoKwik's own thank-you page, so callers must return to default content first.
    public boolean isThankYouPageDisplayed() {
        switchToDefaultContent();
        try {
            waitForUrlToContain("thank-you");
            return isElementDisplayed(thankYouHeading);
        } catch (Exception e) {
            log.error("Thank-you page not displayed: " + e.getMessage());
            return false;
        }
    }

    public String getOrderConfirmationText() {
        return orderIdText.getText();
    }
}
