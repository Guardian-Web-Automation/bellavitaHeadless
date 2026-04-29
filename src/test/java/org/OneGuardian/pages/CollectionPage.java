package org.OneGuardian.pages;

import org.OneGuardian.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CollectionPage extends BasePage {
    protected static final Logger log = LogManager.getLogger(CollectionPage.class);

    // =====================
    // Locators
    // =====================

    // Shop All nav link
    @FindBy(xpath = "//div[@class='header-nav-inner']//a[contains(text(),'Shop All')]")
    private WebElement shopAllLink;

    // Sort button — "SORT BY"
    @FindBy(css = "button.custom-sort-trigger")
    private WebElement sortButton;

    // All sort options in dropdown — appear after clicking sort button
    @FindBy(xpath = "//button[contains(@class,'custom-sort-option underline-expand')]")
    private List<WebElement> sortOptions;

    // Filter button
    @FindBy(css = "button.filter-btn")
    private WebElement filterButton;

    // Filter drawer close button
    @FindBy(css = "button.filter-drawer-close")
    private WebElement filterDrawerCloseButton;

    // All product SALE prices on collection page
    @FindBy(css = "div.price-sale")
    private List<WebElement> productSalePrices;

    @FindBy(css = "div.price-sale")
    private List<WebElement> productCount;

    // All product names on collection page
    @FindBy(css = "div.card__heading a")
    private List<WebElement> productNames;

    // Filter category options — e.g. "Price", "Occasions", "Category"
    // Each filter row in the main drawer
    @FindBy(xpath = "//button[@class='filter-aside-option']")
    private List<WebElement> filterDrawerItems;

    // Apply button inside filter sub-drawer
    @FindBy(xpath = "//div[@class='filter-sub-drawer-footer']//button[contains(text(),'APPLY')]")
    private WebElement applyFilterButton;

    // Clear button inside filter sub-drawer
    @FindBy(css = "button.filter-clear-btn")
    private WebElement clearFilterButton;

    // Active filter tags shown on page after applying
    @FindBy(css = "div.active-filters span.filter-tag")
    private List<WebElement> activeFilterTags;

    // Product count text — "X products"
    @FindBy(xpath = "//h1[@class='collection-page-title' and contains(text(),'All Products')]")
    private WebElement AllProductsTitle;

    @FindBy(xpath = "//button[@class='collection-applied-filter-chip']")
    private WebElement filterApplied;

    // =====================
    // Constructor
    // =====================

    public CollectionPage(WebDriver driver) {
        super(driver);
    }

    // =====================
    // Navigation
    // =====================

    public void navigateToShopAll() {
        safeClick(shopAllLink);
        log.info("Navigated to Shop All page");
        waitForProductsToLoad();
    }

    public void navigateTo(String url) {
        driver.get(url);
        log.info("Navigated to: " + url);
        waitForProductsToLoad();
    }

    private void waitForProductsToLoad() {
        waitForVisibility(AllProductsTitle);
        log.info("Products loaded on collection page");
    }

    // =====================
    // Sort Actions
    // =====================

    public void clickSortButton() {
        safeClick(sortButton);
        log.info("Clicked Sort button");
        // Wait for dropdown to appear
        scrollToElement(sortOptions.get(0));
        wait.until(ExpectedConditions.visibilityOfAllElements(sortOptions));
        log.info("Sort options are visible");
    }

    // sortOptionText → "Price, low to high", "Price, high to low" etc.
    public void selectSortOption(String sortOptionText) {
        clickSortButton();

        for (WebElement option : sortOptions) {
            if (option.getText().trim().toLowerCase()
                    .contains(sortOptionText.toLowerCase())) {
                log.info("Selected sort option: " + option.getText().trim());
                jsClick(option);
                return;
            }
        }
        log.error("Sort option not found: " + sortOptionText);
    }

    // =====================
    // Price Extraction
    // =====================

    // Returns list of all visible sale prices as doubles
    // e.g. ["₹549.00", "₹299.00"] → [549.0, 299.0]
    public List<Double> getAllProductPrices() {
        List<Double> prices = new ArrayList<>();

        for (WebElement priceEl : productSalePrices) {
            try {
                String priceText = priceEl.getText().trim()
                        .replace("₹", "")
                        .replace(",", "")
                        .trim();
                prices.add(Double.parseDouble(priceText));
            } catch (Exception e) {
                log.warn("Could not parse price: " + priceEl.getText());
            }
        }
        log.info("Extracted " + prices.size() + " prices");
        return prices;
    }

    // =====================
    // Filter Actions
    // =====================

    public void clickFilterButton() {
        safeClick(filterButton);
        log.info("Clicked Filter button");
        wait.until(ExpectedConditions.visibilityOfAllElements(filterDrawerItems));
    }

    // filterName → "Price", "Occasions", "Category", "Product type"
    public void clickFilterOption(String filterName) {
        for (WebElement item : filterDrawerItems) {
            if (item.getText().trim().toLowerCase()
                    .contains(filterName.toLowerCase())) {
                safeClick(item);
                log.info("Clicked filter option: " + filterName);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                return;
            }
        }
        log.error("Filter option not found: " + filterName);
    }

    // filterValue → "For Him", "Fragrance", "Perfume", "Bergamot" etc.
    public void selectFilterValue(String filterValue) {
        String xpath = "//label[@class='filter-value-item']//span[1][contains(text(),'" + filterValue + "')]//..//input[@class='filter-value-checkbox']";

        WebElement checkbox = driver.findElement(By.xpath(xpath));

        scrollToElement(checkbox);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }

        if (!checkbox.isSelected()) {
            checkbox.click();
            log.info("Selected filter value: " + filterValue);
        } else {
            log.info("Filter value already selected: " + filterValue);
        }
    }

    public void clickApplyFilter() {
        safeClick(applyFilterButton);
        log.info("Clicked Apply filter");
        waitForProductsToLoad();
    }



    public boolean isFilterApplied(String filterName) {
        waitForVisibility(filterApplied);
        System.out.println("Active filter tag text: " + filterApplied.getText().trim());
        if(filterApplied.getText().trim().toLowerCase().contains(filterName.toLowerCase())){
            log.info("Filter tag found on UI for: " + filterName);
            return true;
        } else {
            log.warn("Filter tag not found on UI for: " + filterName);
            return false;
        }

    }

    // ─────────────────────────────────────────
// Single function for both asc and desc
// sortOrder → "asc" or "desc"
// Returns true if prices match expected order
// ─────────────────────────────────────────
    public boolean checkPriceSorting(String sortOrder) {
        List<Double> prices = getAllProductPrices();
        log.info("Checking " + sortOrder.toUpperCase()
                + " order for " + prices.size() + " prices");

        if (prices.isEmpty()) {
            log.warn("No prices found to verify sort order");
            return false;
        }

        for (int i = 0; i < prices.size() - 1; i++) {
            if (sortOrder.equalsIgnoreCase("asc")) {
                if (prices.get(i) > prices.get(i + 1)) {
                    log.error("❌ Prices NOT ascending at [" + i + "]: "
                            + prices.get(i) + " > " + prices.get(i + 1));
                    return false;
                }
            } else if (sortOrder.equalsIgnoreCase("desc")) {
                if (prices.get(i) < prices.get(i + 1)) {
                    log.error("❌ Prices NOT descending at [" + i + "]: "
                            + prices.get(i) + " < " + prices.get(i + 1));
                    return false;
                }
            } else {
                log.error("Invalid sortOrder: '" + sortOrder
                        + "' — use 'asc' or 'desc'");
                return false;
            }
        }

        log.info("✅ Prices correctly sorted " + sortOrder.toUpperCase()
                + " | " + prices.get(0)
                + " → " + prices.get(prices.size() - 1));
        return true;
    }
}
