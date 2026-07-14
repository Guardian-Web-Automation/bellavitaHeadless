package org.OneGuardian.tests;

import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.CartPage;
import org.OneGuardian.pages.CheckoutPage;
import org.OneGuardian.pages.CollectionPage;
import org.OneGuardian.pages.HomePage;
import org.OneGuardian.utils.GmailOtpReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * End-to-end guest checkout journey: Shop All -> add first product to cart -> checkout ->
 * GoKwik OTP login -> Cash on Delivery.
 * Runs as part of the daily CI suite (testng.xml) and places a real COD order every run;
 * requires GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET / GOOGLE_REFRESH_TOKEN to be available
 * (via .env locally, or environment variables in CI) for GmailOtpReader to fetch the OTP.
 */
public class CheckoutUserJourneyTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(CheckoutUserJourneyTest.class);

    // Test mobile number wired to receive its GoKwik login OTP by email instead of SMS
    private static final String CHECKOUT_PHONE_NUMBER = "9729222530";
    // GoKwik forwards both OTP and order-confirmation mail from this address using the same
    // subject line ("GKKWIK-S"), so GmailOtpReader tells them apart by body text, not subject.
    private static final String OTP_SENDER_EMAIL = "gauravrana7354@gmail.com";

    @Test
    public void verifyShopAllToCodCheckoutJourney() {
        HomePage homePage = new HomePage(driver);
        CollectionPage collectionPage = new CollectionPage(driver);
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        homePage.navigateTo();
        collectionPage.navigateToShopAll();
        collectionPage.addFirstProductToCart();

        Assert.assertTrue(cartPage.isCartPageDisplayed(), "Cart drawer did not open after adding product");
        cartPage.clickCheckout();

        checkoutPage.switchToCheckoutIframe();
        long otpSearchStartEpochSeconds = Instant.now().minusSeconds(30).getEpochSecond();
        checkoutPage.enterMobileNumberAndContinue(CHECKOUT_PHONE_NUMBER);

        GmailOtpReader otpReader = new GmailOtpReader();
        String otp = otpReader.waitForOtp(OTP_SENDER_EMAIL, otpSearchStartEpochSeconds, Duration.ofSeconds(60));
        log.info("Fetched OTP from email");

        checkoutPage.enterOtp(otp);
        checkoutPage.selectCashOnDelivery();

        Assert.assertTrue(checkoutPage.isThankYouPageDisplayed(), "Order thank-you page was not displayed after COD checkout");
        log.info("Order confirmation: " + checkoutPage.getOrderConfirmationText());
    }
}
