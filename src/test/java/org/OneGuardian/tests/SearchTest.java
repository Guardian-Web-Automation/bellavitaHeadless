package org.OneGuardian.tests;

import org.OneGuardian.base.BasePage;
import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.HomePage;
import org.OneGuardian.pages.ProductDetailPage;
import org.OneGuardian.utils.ExcelUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(SearchTest.class);

    // @DataProvider feeds multiple rows of data to the @Test method
    // name = "searchData" → links this provider to the @Test below
    // Each Object[] row in the 2D array becomes one separate test execution
    @DataProvider(name = "searchData")
    public Object[][] getSearchData() {
        String filePath = System.getProperty("user.dir")
                + "/testdata/SearchData(1).xlsx";

        // "Sheet1" → must match exactly the sheet tab name in your Excel file
        return ExcelUtility.getTestData(filePath, "Sheet1");
    }

    // dataProvider = "searchData" → TestNG calls this 3 times (once per Excel row)
    // Each call injects the next row's values as (keyword, expected)
    @Test(dataProvider = "searchData",enabled = true)
    public void verifySearchFunctionality(String keyword, String expected) {
        log.info("TEST: Searching for keyword → " + keyword);

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        homePage.clickSearchIcon();
        homePage.enterSearchText(keyword);
        homePage.submitSearch();

        String result = homePage.getFirstSearchResultText();
        log.info("First search result: " + result);

        Assert.assertTrue(result.contains(expected),
                "Search result did not contain: " + expected
                        + " | Actual: " + result);

        log.info("Search verified for keyword: " + keyword + " ✅");
    }

    @Test(enabled = true)
    public void verifyAddToCartFromSearchResult() {
        log.info("TEST: Verifying Add to Cart from search result to PDP than ATC...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        // Step 1 — Search for a product
        log.info("Searching for: Ceo");
        homePage.clickSearchIcon();
        homePage.enterSearchText("Ceo");
        homePage.submitSearch();

        // Step 2 — Click first product from search results
        log.info("Clicking first search result...");
        homePage.clickFirstSearchResult();

        // Step 3 — On product page, click Add to Cart
        ProductDetailPage productPage = new ProductDetailPage(driver);
        String productName = productPage.getProductTitle();
        log.info("On product page: " + productName);

        productPage.clickAddToCart();
        log.info("Clicked Add to Cart");

        productPage.closeCart();
        log.info("Closed cart notification");

        // Step 4 — Verify cart count is greater than 0
        String cartCount = productPage.getCartCount();
        log.info("Cart count after ATC: " + cartCount);
        Assert.assertNotEquals(cartCount, "0",
                "Cart count did not increase after Add to Cart!");

        log.info("Add to Cart verified successfully ✅ | Product: " + productName);
    }

    @Test(enabled = true)
    public void verifyAddToCartDirectlyFromSearchPage() {
        log.info("TEST: Verifying Add to Cart from search result...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        // Step 1 — Search for a product
        log.info("Searching for: Ceo");
        homePage.clickSearchIcon();
        homePage.enterSearchText("ceo");
        homePage.submitSearch();

        // Step 2 — Click first product from search results
        ProductDetailPage productPage = new ProductDetailPage(driver);
//        Thread.sleep(2000); // Temporary sleep to allow product cards to load (replace with better wait)

        String productName = productPage.getProductTitleOfProductCard();
        log.info("First product card: " + productName);

        productPage.clickAddToCartAtProductCard();
        log.info("Clicked Add to Cart of product card from search page");

        productPage.closeCart();
        log.info("Closed cart notification");

        // Step 4 — Verify cart count is greater than 0
        String cartCount = productPage.getCartCount();
        log.info("Cart count after ATC: " + cartCount);
        Assert.assertNotEquals(cartCount, "0",
                "Cart count did not increase after Add to Cart!");

        log.info("Add to Cart verified successfully ✅ | Product: " + productName);
    }






}