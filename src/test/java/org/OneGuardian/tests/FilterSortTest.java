package org.OneGuardian.tests;

import org.OneGuardian.base.BasePage;
import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.CollectionPage;
import org.OneGuardian.pages.HomePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;



public class FilterSortTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(FilterSortTest.class);

    // ─────────────────────────────────────────
    // TEST 1 — Verify Sort by Price Low to High
    // ─────────────────────────────────────────
    @Test
    public void verifySortByPriceLowToHigh() {
        log.info("TEST: Verifying sort by price low to high...");
        HomePage homePage = new HomePage(driver);
        homePage.navigateTo();

        CollectionPage collectionPage = new CollectionPage(driver);
        collectionPage.navigateToShopAll();

        // Apply sort
        collectionPage.selectSortOption("low to high");
        BasePage bs = new BasePage(driver);
        bs.waitForUrlToContain("sort=price-asc");

        // Get all prices after sorting
        Assert.assertTrue(collectionPage.checkPriceSorting("asc"),
                "Prices should be sorted ascending!");
    }

    // ─────────────────────────────────────────
    // TEST 2 — Verify Sort by Price High to Low
    // ─────────────────────────────────────────
    @Test
    public void verifySortByPriceHighToLow() {
        log.info("TEST: Verifying sort by price high to low...");
        HomePage homePage = new HomePage(driver);

        homePage.navigateTo();

        CollectionPage collectionPage = new CollectionPage(driver);
        collectionPage.navigateToShopAll();

        collectionPage.selectSortOption("high to low");
        BasePage bs = new BasePage(driver);
        bs.waitForUrlToContain("sort=price-desc");

        Assert.assertTrue(collectionPage.checkPriceSorting("desc"),
                "Prices should be sorted descending!");
    }

    // ─────────────────────────────────────────
    // TEST 3 — Verify Filter by Occasion (For Him)

    // ─────────────────────────────────────────

    @DataProvider(name = "occasionFilterData")
    public Object[][] filterData() {
        return new Object[][]{
                {"Perfume Notes", "Africanorange"},
                {"Perfume Notes", "Freesia"},
                {"Occasions", "For Him"},
                {"Occasions", "For Her"},
                {"Occasions", "Gifting"},
                {"Product type", "Attar"},
                {"Product type", "Gift Set"},
                {"Product type", "Perfume Combo"},
                {"Category", "Bath & Body"},
                {"Category", "Fragrance"}
        };
    }

    @Test(dataProvider = "occasionFilterData")
    public void verifyFilterAtCollectionPage(String filterOption, String filterValue) {
        log.info("TEST: Verifying filter at collection page...");
        HomePage homePage = new HomePage(driver);

        homePage.navigateTo();

        CollectionPage collectionPage = new CollectionPage(driver);
        collectionPage.navigateToShopAll();

        // Apply filter → Occasions → For Him
        collectionPage.clickFilterButton();
        collectionPage.clickFilterOption(filterOption);
        collectionPage.selectFilterValue(filterValue);
        collectionPage.clickApplyFilter();

        // Filtered count should be less than total
        Assert.assertTrue(collectionPage.isFilterApplied(filterValue), "Filter should be applied and visible on UI!");
    }


    @DataProvider(name = "sortAndFilterCombinedData")
    public Object[][] sortAndFilterData() {
        return new Object[][]{
                {"Occasions", "For Her", "low to high", "sort=price-asc", "asc"},
                {"Occasions", "For Her", "high to low", "sort=price-desc", "desc"},
                {"Occasions", "For Him", "low to high", "sort=price-asc", "asc"},
                {"Occasions", "For Him", "high to low", "sort=price-desc", "desc"},
                {"Perfume Notes", "Africanorange", "low to high", "sort=price-asc", "asc"},
                {"Perfume Notes", "Africanorange", "high to low", "sort=price-desc", "desc"}
        };
    }

    @Test(dataProvider = "sortAndFilterCombinedData")
    public void verifySortAndFilterCombined(String filterOption, String filterValue, String sortOption, String sortUrlParam, String PriceSortOrder) {
        log.info("TEST: Verifying sort + filter combined...");
        HomePage homePage = new HomePage(driver);

        homePage.navigateTo();

        CollectionPage collectionPage = new CollectionPage(driver);
        collectionPage.navigateToShopAll();

        // Step 1 — Apply filter first
        collectionPage.clickFilterButton();
        collectionPage.clickFilterOption(filterOption);
        collectionPage.selectFilterValue(filterValue);
        collectionPage.clickApplyFilter();


        // Step 2 — Apply sort on filtered results
        collectionPage.selectSortOption(sortOption);
        BasePage bs = new BasePage(driver);
        bs.waitForUrlToContain(sortUrlParam);

        Assert.assertTrue(collectionPage.checkPriceSorting(PriceSortOrder),
                "Prices should be sorted ascending!");
    }
}

