package org.OneGuardian.pages;

import org.OneGuardian.base.BasePage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class HomePage extends BasePage {

    // ==============================
    // @FindBy = PageFactory locator
    // PageFactory initializes all @FindBy fields lazily —
    // the element is looked up in DOM only when you actually use it
    // ==============================

    // Search icon in the navbar
    @FindBy(xpath = "//form[@action='/search']")
    private WebElement searchIcon;

    // Search input box (visible after clicking search icon)
    @FindBy(xpath = "//input[@aria-label='Search']")
    private WebElement searchInput;

    // Logo at top of page
    @FindBy(css = ".logo-image")
    private WebElement logo;

    // "Shop All" link in nav
    @FindBy(linkText = "Shop All")
    private WebElement shopAllLink;

    @FindBy(xpath = "(//section[@aria-label='Search results']//div//h3/a)[1]")
    private WebElement firstSearchResult;

    @FindBy(xpath = "(//div[@class='product-card min-w-0']//a)[1]")
    private WebElement firstProductLink;

    @FindBy(xpath = "//button[contains(@class,'section-tab') and contains(text(),'BESTSELLERS')]")
    private WebElement bestsellerLink;

    // All product titles in bestseller page
    @FindBy(xpath = "//div[@class='product-grid no-scrollbar']//h3")
    private List<WebElement> productTitles;

    // All ATC buttons in bestseller page — same index as productTitles
    @FindBy(xpath = "//div[@class='product-grid no-scrollbar']//button[@class='product-card-add-btn']")
    private List<WebElement> atcButtons;



    // Constructor — PageFactory.initElements wires up all @FindBy fields
    // Must be called with the driver so Selenium knows which browser to look in
    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ==============================
    // Page Actions — what a user can DO on this page
    // ==============================


    public void clickSearchIcon() {
        safeClick(searchIcon);
    }

    public void enterSearchText(String text) {
        searchInput.clear();
        searchInput.sendKeys(text);
    }

    public void clickShopAll() {
        shopAllLink.click();
    }

    public boolean isLogoDisplayed() {
        return logo.isDisplayed();
    }

    public void submitSearch() {
        searchInput.sendKeys(org.openqa.selenium.Keys.RETURN);
        wait.until(ExpectedConditions.visibilityOf(firstSearchResult));
    }

    public String getFirstSearchResultText() {
        return firstSearchResult.getText().toUpperCase();
    }

    public void clickFirstSearchResult() {
        safeClick(firstProductLink);
    }

    // ─────────────────────────────────────────
    public void navigateToBestsellers() {
        try{
            safeClick(bestsellerLink);
            // Wait for products to load
            wait.until(ExpectedConditions.visibilityOfAllElements(productTitles));
            log.info("Navigated to Bestsellers section");
        }catch (Exception e) {
            log.error("Failed to navigate to Bestsellers: " + e.getMessage());
        }
    }

    public boolean clickATCByProductName(String productName) {
        log.info("Looking for product: " + productName + " in Bestsellers...");

        // Loop through all product titles
        for (int i = 0; i < productTitles.size(); i++) {
            String title = productTitles.get(i).getText();

            // Case insensitive partial match
            // e.g. passing "CEO" will match "CEO Man - 100ml"
            if (title.toLowerCase().contains(productName.toLowerCase())) {
                log.info("Found product at index [" + i + "]: " + title);

                // Scroll product into view before clicking
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView(true);", atcButtons.get(i));

                try { Thread.sleep(500); } catch (InterruptedException e) { }

                try {
                    // Try normal click first
                    wait.until(ExpectedConditions
                            .elementToBeClickable(atcButtons.get(i)));
                    atcButtons.get(i).click();
                    log.info("✅ Clicked ATC for: " + title);

                } catch (Exception e) {
                    // JS click fallback — same headless fix we applied before
                    log.warn("Normal click failed — using JS click");
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].click();", atcButtons.get(i));
                    log.info("✅ Clicked ATC via JS for: " + title);
                }

                return true; // product found and ATC clicked
            }
        }
        log.error("❌ Product not found: " + productName + " in Bestsellers");
        return false; // caller can use this to fail the test
    }

    // ─── Reusable helper — adds a product to cart before each cart test ───
    // Avoids code duplication across all cart test methods
    public void addProductToCart() {
        navigateTo();
        clickSearchIcon();
        enterSearchText("CEO Man");
        submitSearch();
        clickFirstSearchResult();

        ProductDetailPage productPage = new ProductDetailPage(driver);
        productPage.clickAddToCart();

        // Wait for cart to update
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
    }

}