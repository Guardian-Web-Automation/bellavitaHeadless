package org.OneGuardian.pages;

import org.OneGuardian.base.BasePage;
import org.openqa.selenium.By;
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

    @FindBy(xpath = "//button[contains(@class,'section-tab') and contains(text(),'NEW ARRIVALS')]")
    private WebElement newArrivalsLink;

    @FindBy(xpath = "//button[contains(@class,'section-tab') and contains(text(),'NEW ARRIVALS')]")
    private WebElement crazyDealButtonHeader;

    // All product titles in bestseller page
    @FindBy(xpath = "//div[@class='product-grid no-scrollbar']//h3")
    private List<WebElement> productTitles;

    // All ATC buttons in bestseller page — same index as productTitles
    @FindBy(xpath = "//div[@class='product-grid no-scrollbar']//button[@class='product-card-add-btn']")
    private List<WebElement> atcButtons;

    @FindBy(xpath = "//a[@href='/collections/best-sellers' and contains(text(),'VIEW ALL')]")
    private WebElement viewAllBestsellersLink;

    @FindBy(xpath = "//a[@href='/collections/new-arrivals' and contains(text(),'VIEW ALL')]")
    private WebElement viewAllNewArrivalsLink;

    @FindBy(xpath = "//h1[@class='collection-page-title' and contains(text(),'Best Sellers')]")
    private WebElement bestSellerCollectionTitle;

    @FindBy(xpath = "//h1[@class='collection-page-title' and contains(text(),'New Arrivals')]")
    private WebElement NewArrivalCollectionTitle;

    @FindBy(xpath = "(//div[@class='container']//section[@class='section'])[2]")
    private WebElement LuxuryCategories;

    @FindBy(xpath = "//footer[@class='site-footer']//div[@class='footer-grid-desktop']")
    private WebElement footer;





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

    public boolean isFooterDisplayed() {
        return footer.isDisplayed();
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

    public void navigateToLuxuryCategory() {
        try{
            scrollToElement(LuxuryCategories);
            log.info("Navigated to Luxury Categories section");
        }catch (Exception e) {
            log.error("Failed to navigate to Luxury Categories: " + e.getMessage());
        }
    }

    public void navigateToFooter() {
        try{
            scrollToElement(footer);
            log.info("Navigated to footer section");
        }catch (Exception e) {
            log.error("Failed to navigate to footer Categories: " + e.getMessage());
        }
    }

    public void clickLuxuryCategory(String categoryName,String ExpextedPageTitle) {
        	try {
                WebElement categoryTab = driver.findElement(By.xpath("//h3[@class='category-card-title'and contains(text(),'" + categoryName + "')]"));
                safeClick(categoryTab);
                // Wait for products to load
                WebElement expectedTitleElement = driver.findElement(By.xpath("//h1[@class='collection-page-title' and contains(text(),'"+ExpextedPageTitle+"')]"));
                wait.until(ExpectedConditions.visibilityOf(expectedTitleElement));
            } catch (Exception e) {
                log.error("Failed to click on category: " + categoryName + " | " + e.getMessage());
            }
    }

    public void navigateToNewArrivals() {
        try{
            safeClick(newArrivalsLink);
            // Wait for products to load
            wait.until(ExpectedConditions.visibilityOfAllElements(productTitles));
            log.info("Navigated to New Arrival  section");
        }catch (Exception e) {
            log.error("Failed to navigate to New Arrivals: " + e.getMessage());
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

    public void clickViewAllBestsellers() {
        	try {
            safeClick(viewAllBestsellersLink);
            waitForVisibility(bestSellerCollectionTitle);
            log.info("Clicked 'VIEW ALL' in Bestsellers");
        } catch (Exception e) {
            log.error("Failed to click 'VIEW ALL' in Bestsellers: " + e.getMessage());

            }
    }

    public void clickViewAllNewArrivals() {
        try {
            safeClick(viewAllNewArrivalsLink);
            waitForVisibility(NewArrivalCollectionTitle);
            log.info("Clicked 'VIEW ALL' in New Arrivals");
        } catch (Exception e) {
            log.error("Failed to click 'VIEW ALL' in New Arrivals: " + e.getMessage());

        }
    }

    public void navigateToHeader(String CollectionName, String expectedTitle) {
        try{
            WebElement headerTab = driver.findElement(By.xpath("//div[@class='header-nav-inner']//a[contains(text(),'"+CollectionName+"')]"));
            jsClick(headerTab);
            // Wait for products to load
            WebElement expectedTitleElement = driver.findElement(By.xpath("//h1[@class='collection-page-title' and contains(text(),'"+expectedTitle+"')]"));
            wait.until(ExpectedConditions.visibilityOfAllElements(expectedTitleElement));
            log.info("Navigated to "+CollectionName+"  section");
        }catch (Exception e) {
            log.error("Failed to navigate to "+CollectionName+": " + e.getMessage());
        }
    }

    public void clickFooterCategory(String categiryName, String expectedUrl) {
        try {
            WebElement categiryNameElement = driver.findElement(By.xpath("//h4[@class='footer-column-title']//..//li/a[contains(text(),'" + categiryName + "')]"));
            jsClick(categiryNameElement);
            wait.until(ExpectedConditions.urlContains(expectedUrl));

            log.info("Clicked footer category: " + categiryName);
        }catch (Exception e) {
            log.error("Failed to click footer category: " + categiryName + " | " + e.getMessage());
        }
    }
}