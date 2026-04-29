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
        homePage.navigateTo();

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
        homePage.navigateTo();

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
        homePage.navigateTo();

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
        homePage.navigateTo();

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
        homePage.navigateTo();

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
        homePage.navigateTo();

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

        homePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        crazyDealsPage.addProductToBox("WHITE OUD");
        crazyDealsPage.addProductToBox("GLAM");
        crazyDealsPage.addProductToBox("KLUB");

        String dealPrice = crazyDealsPage.getItemValuePrice();

        String originalPrice = crazyDealsPage.getOriginalPrice();

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



// TEST 8 — Verify 1 Product × 3 Quantity Completes the Box
// Flow: WHITE OUD × 3 → 3/3 ADDED → Deal Unlocked
// ─────────────────────────────────────────
    @Test
    public void verifyOneProductThreeQuantityCompletesDeal() {
        log.info("TEST: Verifying 1 product × 3 qty completes the deal...");
        HomePage homePage = new HomePage(driver);
        homePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        // Step 1 — Add WHITE OUD 3 times
        log.info("Step 1: Adding WHITE OUD × 3...");
        boolean added = crazyDealsPage.addProductToBoxMultipleTimes("WHITE OUD", 3);
        Assert.assertTrue(added,
                "Failed to add WHITE OUD 3 times!");

        // Step 2 — Verify progress shows 3/3
        String progress = crazyDealsPage.getProgressText();
        log.info("Step 2: Progress → " + progress);
        Assert.assertTrue(progress.contains("3/3"),
                "Progress should be 3/3! Actual: " + progress);

        // Step 3 — Verify deal is unlocked
        Assert.assertTrue(crazyDealsPage.isDealUnlocked(),
                "Deal should be unlocked after 1 product × 3 qty!");

        // Step 4 — Verify Pay Now button appears
        Assert.assertTrue(crazyDealsPage.isPayNowButtonDisplayed(),
                "Pay Now button should appear!");

        // Step 5 — Verify deal price shows ₹1,298
        String dealPrice = crazyDealsPage.getItemValuePrice();
        Assert.assertTrue(dealPrice.contains("1,298"),
                "Deal price should be ₹1,298! Actual: " + dealPrice);

        log.info("✅ 1 product × 3 qty completes the deal | Price: " + dealPrice);
    }

    // ─────────────────────────────────────────
// TEST 09 — Verify 1 Product × 2 Qty + 1 Different Product Completes Box
// Flow: WHITE OUD × 2 + SKAI × 1 → 3/3 ADDED → Deal Unlocked
// ─────────────────────────────────────────
    @Test
    public void verifyTwoQtyOneProductPlusOneOtherCompletesDeal() {
        log.info("TEST: Verifying 2 qty of 1 product + 1 other completes deal...");

        HomePage homePage = new HomePage(driver);
        homePage.navigateTo();

        CrazyDealsPage crazyDealsPage = new CrazyDealsPage(driver);
        crazyDealsPage.clickCrazyDealsNav();
        crazyDealsPage.clickBuildYourBox("Ultimate Perfume Box");

        // Step 1 — Add WHITE OUD × 2
        log.info("Step 1: Adding WHITE OUD × 2...");
        boolean addedTwo = crazyDealsPage.addProductToBoxMultipleTimes("WHITE OUD", 2);
        Assert.assertTrue(addedTwo,
                "Failed to add WHITE OUD 2 times!");

        // Step 2 — Verify progress is 2/3
        String progressAfterTwo = crazyDealsPage.getProgressText();
        log.info("Step 2: Progress after 2 items → " + progressAfterTwo);
        Assert.assertTrue(progressAfterTwo.contains("2/3"),
                "Progress should be 2/3! Actual: " + progressAfterTwo);

        // Step 3 — Add SKAI × 1
        log.info("Step 3: Adding SKAI × 1...");
        boolean addedOne = crazyDealsPage.addProductToBox("SKAI");
        Assert.assertTrue(addedOne,
                "Failed to add SKAI!");

        // Step 4 — Verify progress is 3/3
        String progressAfterThree = crazyDealsPage.getProgressText();
        log.info("Step 4: Progress after 3 items → " + progressAfterThree);
        Assert.assertTrue(progressAfterThree.contains("3/3"),
                "Progress should be 3/3! Actual: " + progressAfterThree);

        // Step 5 — Verify deal is unlocked
        Assert.assertTrue(crazyDealsPage.isDealUnlocked(),
                "Deal should be unlocked!");

        // Step 6 — Verify Pay Now button appears
        Assert.assertTrue(crazyDealsPage.isPayNowButtonDisplayed(),
                "Pay Now button should appear!");

        // Step 7 — Verify deal price shows ₹1,298
        String dealPrice = crazyDealsPage.getItemValuePrice();
        log.info("Step 7: Deal price → " + dealPrice);
        Assert.assertTrue(dealPrice.contains("1,298"),
                "Deal price should be ₹1,298! Actual: " + dealPrice);

        log.info("✅ 2 qty WHITE OUD + 1 SKAI completes the deal | Price: "
                + dealPrice);
    }
}