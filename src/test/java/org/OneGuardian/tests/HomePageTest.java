package org.OneGuardian.tests;

import org.OneGuardian.base.BasePage;
import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.HomePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {

    // Each test method gets a fresh browser from BaseTest @BeforeMethod
    private static final Logger log = LogManager.getLogger(HomePageTest.class);


    @Test(enabled = true)
    public void verifyHomePageTitle() {
        log.info("TEST: Verifying homepage title...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        String actualTitle = homePage.getPageTitle();
        String expectedTitle = "Bella Vita Luxury | Buy Best Perfumes Online";

        // Assert.assertEquals → fails test immediately if values don't match
        Assert.assertEquals(actualTitle, expectedTitle,
                "Page title does not match!");
        log.info("Homepage title verified successfully ✅");
    }

    @Test(enabled = true)
    public void verifyHomePageUrl() {
        log.info("TEST: Verifying homepage URL...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        String actualUrl = homePage.getCurrentUrl();

        // Assert.assertTrue → fails if condition is false
        Assert.assertTrue(actualUrl.contains("bellavitaluxury.co.in"),
                "URL does not contain expected domain!");
        log.info("Homepage URL verified successfully ✅");
    }

    @Test(enabled = true)
    public void verifyLogoIsDisplayed() {
        log.info("TEST: Verifying logo visibility...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        Assert.assertTrue(homePage.isLogoDisplayed(),
                "Logo is not visible on the homepage!");
        log.info("Logo is visible on homepage ✅");
    }
}