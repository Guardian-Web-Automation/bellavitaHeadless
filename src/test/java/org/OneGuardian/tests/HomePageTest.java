package org.OneGuardian.tests;

import org.OneGuardian.base.BasePage;
import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.HomePage;
import org.OneGuardian.utils.ExcelUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
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
    @Test(enabled = true)
    public void verifyNavigateToFooter() {
        log.info("TEST: Verifying navigating to  footer...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        homePage.navigateToFooter();

        Assert.assertTrue(homePage.isFooterDisplayed(),
                "Footer is not visible on the homepage!");
        log.info("Footer is visible on homepage ✅");
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

    @DataProvider(name = "headerLinksData")
    public Object[][] HeaderLinkData() {
        return new Object[][] {
                {"Crazy Deals", "Crazy Deals", "build-a-box"},
                {"Shop All", "All Products", "all-products"},
                {"BestSellers", "Best Sellers", "best-sellers"},
                {"Perfumes", "All perfumes", "all-perfumes"},
                {"Bath & Body", "Bath & Body", "bath-body"},
                {"Little Luxuries", "Little Luxuries", "little-luxuries"},
                {"New Arrivals", "New Arrivals", "new-arrivals"}
        };
    }

    @Test(dataProvider = "headerLinksData", enabled = true)
    public void verifyHeaderLinks(String collectionName, String expectedHeader, String expectedUrl) {
        log.info("TEST: Verifying Header link at HomePage...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        homePage.navigateToHeader(collectionName,expectedHeader);
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+collectionName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains("/"+expectedUrl+""),
                "Did not navigate to HeaderLInk!--"+collectionName+"--");
        log.info("The "+collectionName +" link verified successfully ✅");
    }



    @DataProvider(name = "luxuryCategoriesData")
    public Object[][] luxuryCategoriesData() {
        return new Object[][] {
                {"Luxury Perfumes", "Premium Fragrances", "/premium-fragrances"},
                {"Body Washes", "Shower Gel", "/shower-gel"},
                {"Body Lotions", "BodyLotion", "/bodylotion"},
                {"Body Deos", "Body Parfum", "/body-parfum"},
                {"Gift Sets", "All Gift Sets", "all-gift-sets"}
        };
    }
    @Test(dataProvider = "luxuryCategoriesData", enabled = true)
    public void verifyCollectionUnderLuxuryCategories(String collectionName,String expectedHeader, String expectedUrl) {
        log.info("TEST: Verifying Luxury Perfumes link at Luxury Categories at home page...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        homePage.navigateToLuxuryCategory();
        homePage.clickLuxuryCategory(collectionName,expectedHeader);
        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+collectionName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains(""+expectedUrl+""),
                "Did not navigate to New Arrival page!");
        log.info("The "+collectionName +" link verified successfully ✅");
    }

    @DataProvider(name = "footerLinksData")
    public Object[][] FooterCategoryData() {
        return new Object[][] {
                { "Blogs", "blogs"},
                { "Terms & Conditions", "terms-and-conditions"},
                { "Privacy Policy", "privacy-policy"},
                { "Refund and Return", "refund-and-return"},
                { "Shipping Policy", "shipping-and-delivery"},
                { "About Us", "about-us"},
                { "Contact Us", "contact-us"},
                { "Order Tracking", "tracking"},
                { "All Products", "all"},
                { "FAQ", "frequently-asked-questions"},

        };
    }
    @Test(dataProvider = "footerLinksData", enabled = true)
    public void verifyFooterLinks(String CategiryName, String expectedUrl) {
        log.info("TEST: Verifying Footer link  at home page...");
        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();
        homePage.navigateToFooter();
        homePage.clickFooterCategory(CategiryName,expectedUrl);

        String actualUrl = homePage.getCurrentUrl();
        log.info("Current URL after clicking "+CategiryName+": " + actualUrl);

        Assert.assertTrue(actualUrl.contains(""+expectedUrl+""),
                "Did not navigate to Footer link page!");
        log.info("The "+CategiryName +" link verified successfully ✅");
    }

}