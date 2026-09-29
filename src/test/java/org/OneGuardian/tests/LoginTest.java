package org.OneGuardian.tests;

import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.LoginPage;
import org.OneGuardian.utils.GmailOtpReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * Account login via the GoKwik KwikPass phone + OTP flow (opened from the header Account icon).
 * <p>
 * The valid-OTP case fetches the real login OTP from Gmail with {@link GmailOtpReader}, so it needs
 * GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET / GOOGLE_REFRESH_TOKEN available (via .env locally, or
 * environment variables in CI) — the same credentials the checkout journey uses.
 */
public class LoginTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(LoginTest.class);

    // Test mobile number wired to receive its KwikPass login OTP by email instead of SMS.
    private static final String LOGIN_PHONE_NUMBER = "9729222530";
    // GoKwik forwards the OTP mail from this address; GmailOtpReader matches the OTP by body wording.
    private static final String OTP_SENDER_EMAIL = "gauravrana7354@gmail.com";

    @Test(enabled = true, priority = 1)
    public void verifyLoginWithValidOtp() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();

        loginPage.openLoginModal();
        // Anchor the OTP mail search just before the OTP is actually requested.
        long otpSearchStartEpochSeconds = Instant.now().minusSeconds(30).getEpochSecond();
        loginPage.submitMobileNumber(LOGIN_PHONE_NUMBER);

        Assert.assertTrue(loginPage.isOtpScreenDisplayed(),
                "OTP entry screen did not appear after submitting the mobile number");

        GmailOtpReader otpReader = new GmailOtpReader();
        String otp = otpReader.waitForOtp(OTP_SENDER_EMAIL, otpSearchStartEpochSeconds, Duration.ofSeconds(60));
        log.info("Fetched login OTP from email");

        loginPage.submitOtp(otp);

        Assert.assertTrue(loginPage.isLoginSuccessful(),
                "User was not logged in after entering a valid OTP");
    }

    @Test(enabled = true, priority = 2)
    public void verifyLoginFailsWithInvalidOtp() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();

        loginPage.openLoginModal();
        loginPage.submitMobileNumber(LOGIN_PHONE_NUMBER);

        Assert.assertTrue(loginPage.isOtpScreenDisplayed(),
                "OTP entry screen did not appear after submitting the mobile number");

        loginPage.submitOtp("0000");

        Assert.assertTrue(loginPage.isOtpErrorDisplayed(),
                "Expected an 'OTP verification failed' error for an invalid OTP");
    }
}
