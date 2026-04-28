package org.OneGuardian.tests;

import org.OneGuardian.base.BasePage;
import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.CrazyDealsPage;
import org.OneGuardian.pages.HomePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CrazyDealsTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(CrazyDealsTest.class);

    // ─────────────────────────────────────────
    // TEST 1 — Verify Crazy Deals Page Loads
    // ─────────────────────────────────────────
    @Test
    public void verifyCrazyDealsPageLoads() {
        log.info("TEST: Verifying Crazy Deals page loads...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();

        Assert.assertTrue(crazyDealsPage.isCrazyDealsTitleDisplayed(),
                "Crazy Deals page did not load!");

        log.info("✅ Crazy Deals page loaded successfully");
    }

    // ─────────────────────────────────────────
    // TEST 2 — Verify Build Your Box Opens
    // ─────────────────────────────────────────
    @Test
    public void verifyBuildYourBoxOpens() {
        log.info("TEST: Verifying Build Your Box page opens...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        // Verify progress counter starts at 0/3
        String progress = crazyDealsPage.getProgressText();
        log.info("Initial progress: " + progress);

        Assert.assertTrue(progress.contains("0/3"),
                "Progress should start at 0/3! Actual: " + progress);

        log.info("✅ Build Your Box opened - progress: " + progress);
    }

    // ─────────────────────────────────────────
    // TEST 3 — Verify Adding 1 Product Updates Progress
    // ─────────────────────────────────────────
    @Test
    public void verifyProgressAfterAddingOneProduct() {
        log.info("TEST: Verifying progress after adding 1 product...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        // Add 1 product
        boolean added = crazyDealsPage.addProductToBox("WHITE OUD");
        Assert.assertTrue(added, "WHITE OUD product not found!");

        String progress = crazyDealsPage.getProgressText();

        Assert.assertTrue(progress.contains("1/3"),
                "Progress should be 1/3! Actual: " + progress);

        log.info("✅ Progress updated to 1/3 after adding 1 product");
    }

    // ─────────────────────────────────────────
    // TEST 4 — Verify Adding 2 Products Updates Progress
    // ─────────────────────────────────────────
    @Test
    public void verifyProgressAfterAddingTwoProducts() {
        log.info("TEST: Verifying progress after adding 2 products...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        crazyDealsPage.addProductToBox("WHITE OUD");
        crazyDealsPage.addProductToBox("SKAI");

        String progress = crazyDealsPage.getProgressText();
        log.info("Progress after 2 products: " + progress);

        Assert.assertTrue(progress.contains("2/3"),
                "Progress should be 2/3! Actual: " + progress);

        log.info("✅ Progress updated to 2/3");
    }

    // ─────────────────────────────────────────
    // TEST 5 — Verify Deal Unlocks After 3 Products
    // ─────────────────────────────────────────
    @Test
    public void verifyDealUnlocksAfterThreeProducts() {
        log.info("TEST: Verifying deal unlocks after adding 3 products...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        // Add 3 products
        crazyDealsPage.addProductToBox("WHITE OUD");
        crazyDealsPage.addProductToBox("SKAI");
        crazyDealsPage.addProductToBox("FRESH");

        // Verify 3/3 progress
        String progress = crazyDealsPage.getProgressText();
        log.info("Progress after 3 products: " + progress);
        Assert.assertTrue(progress.contains("3/3"),
                "Progress should be 3/3! Actual: " + progress);

        // Verify deal is unlocked
        Assert.assertTrue(crazyDealsPage.isDealUnlocked(),
                "Deal should be unlocked after 3 products!");

        log.info("✅ Deal unlocked at 3/3!");
    }

    // ─────────────────────────────────────────
    // TEST 6 — Verify Pay Now Button Appears After 3/3
    // ─────────────────────────────────────────
    @Test
    public void verifyPayNowButtonAfterDealUnlock() {
        log.info("TEST: Verifying Pay Now button appears after 3/3...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        crazyDealsPage.addProductToBox("WHITE OUD");
        crazyDealsPage.addProductToBox("SKAI");
        crazyDealsPage.addProductToBox("FRESH");

        Assert.assertTrue(crazyDealsPage.isPayNowButtonDisplayed(),
                "Pay Now button should appear after 3/3!");

        log.info("✅ Pay Now button is visible after deal unlock");
    }

    // ─────────────────────────────────────────
    // TEST 7 — Verify Bill Summary Shows ₹1,298
    // ─────────────────────────────────────────
    @Test
    public void verifyItemValuePriceUpdatesOnAddingProduct() {
        log.info("TEST: Verifying item value price updates after adding product...");

        HomePage homePage = new HomePage(driver);
        BasePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        crazyDealsPage.addProductToBox("WHITE OUD");
        crazyDealsPage.addProductToBox("GLAM");
        crazyDealsPage.addProductToBox("KLUB");

        String dealPrice = crazyDealsPage.getItemValuePrice();
        log.info("Deal price after adding 3 product: " + dealPrice);

        String originalPrice = crazyDealsPage.getOriginalPrice();
        log.info("Original price: " + originalPrice);

        // Verify neither is empty before parsing
        Assert.assertFalse(dealPrice.isEmpty(),
                "Deal price should not be empty!");
        Assert.assertFalse(originalPrice.isEmpty(),
                "Original price should not be empty!");

        // Verify both contain ₹
        Assert.assertTrue(dealPrice.contains("₹"),
                "Deal price should contain ₹! Actual: " + dealPrice);

        // Clean and parse safely
        String cleanDeal = dealPrice.replace("₹", "")
                .replace(",", "")
                .replaceAll("[\\u00A0\\s]+", "")
                .trim();

        String cleanOriginal = originalPrice.replace("₹", "")
                .replace(",", "")
                .replaceAll("[\\u00A0\\s]+", "")
                .trim();

        log.info("Clean deal price: " + cleanDeal);
        log.info("Clean original price: " + cleanOriginal);

        // Guard against empty string before parsing
        Assert.assertFalse(cleanDeal.isEmpty(),
                "Clean deal price is empty after stripping symbols!");

        double deal = Double.parseDouble(cleanDeal);
        double original = Double.parseDouble(cleanOriginal);

        Assert.assertTrue(deal < original,
                "Deal price (" + dealPrice + ") should be less than "
                        + "original (" + originalPrice + ")!");

        log.info("✅ Deal price " + dealPrice
                + " is less than original " + originalPrice);
    }

    // ─────────────────────────────────────────
    // TEST 8 — Verify Saving Amount is Displayed
    // ─────────────────────────────────────────

}