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

    @Test
    public void verifyBestsellersLink() {
        log.info("TEST: Verifying Bestsellers link...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        homePage.navigateToBestsellers();
        homePage.clickViewAllBestsellers();
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking Bestsellers: " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/best-sellers"),
                "Did not navigate to Bestsellers page!");
        log.info("Bestsellers link verified successfully ✅");
    }

    @Test
    public void verifyNewArrivalsLink() {
        log.info("TEST: Verifying New Arrivals link...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        homePage.navigateToNewArrivals();
        homePage.clickViewAllNewArrivals();
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking New Arrivals: " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/new-arrivals"),
                "Did not navigate to New Arrival page!");
        log.info("New Arrival link verified successfully ✅");
    }

    @Test
    public void verifyCrazyDealLinkHeader() {
        log.info("TEST: Verifying New Crazy deal link at Header...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        String collectionName = "Crazy Deals";
        String expectedHeader = "Crazy Deals";
        homePage.navigateToHeader(collectionName,expectedHeader);
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+collectionName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/build-a-box"),
                "Did not navigate to New Arrival page!");
        log.info("The "+collectionName +" link verified successfully ✅");
    }

    @Test
    public void verifyShopAllLinkHeader() {
        log.info("TEST: Verifying New Shop All link at Header...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        String collectionName = "Shop All";
        String expectedHeader = "All Products";
        homePage.navigateToHeader(collectionName,expectedHeader);
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+collectionName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/all-products"),
                "Did not navigate to New Arrival page!");
        log.info("The "+collectionName +" link verified successfully ✅");
    }

    @Test
    public void verifyBestSellersLinkHeader() {
        log.info("TEST: Verifying New BestSellers link at Header...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        String collectionName = "BestSellers";
        String expectedHeader = "Best Sellers";
        homePage.navigateToHeader(collectionName,expectedHeader);
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+collectionName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/best-sellers"),
                "Did not navigate to New Arrival page!");
        log.info("The "+collectionName +" link verified successfully ✅");
    }

    @Test
    public void verifyLittleLuxuriesLinkHeader() {
        log.info("TEST: Verifying New Little Luxuries link at Header...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        String collectionName = "Little Luxuries";
        String expectedHeader = "Little Luxuries";
        homePage.navigateToHeader(collectionName,expectedHeader);
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+collectionName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/little-luxuries"),
                "Did not navigate to New Arrival page!");
        log.info("The "+collectionName +" link verified successfully ✅");
    }


    @Test
    public void verifyNewArrivalsLinkHeader() {
        log.info("TEST: Verifying New New Arrivals link at Header...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        String collectionName = "New Arrivals";
        String expectedHeader = "New Arrivals";
        homePage.navigateToHeader(collectionName,expectedHeader);
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+collectionName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/new-arrivals"),
                "Did not navigate to New Arrival page!");
        log.info("The "+collectionName +" link verified successfully ✅");
    }


}