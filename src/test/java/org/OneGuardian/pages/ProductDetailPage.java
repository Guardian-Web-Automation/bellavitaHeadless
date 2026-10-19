package org.OneGuardian.pages;

import org.OneGuardian.base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductDetailPage extends BasePage {
    protected static final Logger log = LogManager.getLogger(ProductDetailPage.class);


    // =====================
    // Locators
    // =====================

    // Product title on the product detail page
    @FindBy(xpath = "//div[@class='order-1 lg:col-start-2 lg:row-start-1 flex flex-col px-4 lg:pl-12']/h1")
    private WebElement productTitle;

    @FindBy(xpath = "(//h3[contains(@class,'product-card-title')]//a)[1]")
    private WebElement productTitleOfProductCard;

    // Add to Cart button on product page
    @FindBy(xpath = "(//button[contains(@class,'bg-black') and contains(text(),'ADD TO CART')])[1]")
    private WebElement addToCartButton;
    // Sticky footer ATC button — visible in headless/mobile view
// Has unique class 'pdp-sticky-add-shimmer' to distinguish it
    @FindBy(css = "button.pdp-sticky-add-shimmer")
    private WebElement stickyFooterATCButton;

    @FindBy(xpath = "(//button[@class='product-card-add-btn'])[1]")
    private WebElement addToCartButtonAtProductCard;

    // Cart notification popup that appears after ATC click
    @FindBy(xpath = "//div[@class='side-cart-header-row']/h2")
    private WebElement cartNotification;

    @FindBy(xpath = "//button[@aria-label='Close cart']")
    private WebElement cartCloseButton;

    // "Added to cart" or product name inside the notification
    @FindBy(css = "cart-notification-product h3")
    private WebElement cartNotificationProductTitle;

    // Cart item count bubble in header (e.g. shows "1")
    @FindBy(xpath = "(//button[@aria-label='Cart'])[1]//span")
    private WebElement cartCountBubble;

    // Main product heading (robust: the product title is the page's first H1).
    @FindBy(css = "h1")
    private WebElement pdpHeading;

    // Sale and compare-at prices in the product info block (first occurrence is the main product;
    // later .price-sale nodes belong to related-product cards further down the page).
    @FindBy(css = ".price-sale")
    private WebElement salePrice;

    @FindBy(css = ".price-compare")
    private WebElement comparePrice;

    // Quantity stepper on the product form: "-" | value span | "+".
    @FindBy(css = "button[aria-label='Increase quantity']")
    private WebElement increaseQtyButton;

    @FindBy(css = "button[aria-label='Decrease quantity']")
    private WebElement decreaseQtyButton;

    // The quantity value is the span sitting immediately before the "+" button.
    @FindBy(xpath = "(//button[@aria-label='Increase quantity']/preceding-sibling::span[1])[1]")
    private WebElement quantityValue;

    // Judge.me review widget container — marks that the reviews section rendered.
    @FindBy(css = ".jdgm-rev-widg, [class*='jdgm-widget'], [class*='review']")
    private WebElement reviewWidget;

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    // =====================
    // PDP info getters (PDP-01)
    // =====================

    /** Robust product title read (first H1 on the PDP). */
    public String getPdpTitle() {
        waitForVisibility(pdpHeading);
        return pdpHeading.getText().trim();
    }

    public String getSalePrice() {
        try {
            waitForVisibility(salePrice);
            return salePrice.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getComparePrice() {
        try {
            return comparePrice.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isReviewWidgetPresent() {
        return isElementPresent(reviewWidget);
    }

    // =====================
    // Quantity stepper (PDP-02 / PDP-04)
    // =====================

    public int getQuantityValue() {
        try {
            waitForVisibility(quantityValue);
            return Integer.parseInt(quantityValue.getText().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    public void increaseQuantity() {
        safeClick(increaseQtyButton);
    }

    public void decreaseQuantity() {
        safeClick(decreaseQtyButton);
    }

    /** Clicks "+" until the stepper shows the requested quantity (starts at 1). */
    public void setQuantity(int target) {
        for (int i = getQuantityValue(); i > 0 && i < target && i < 50; i = getQuantityValue()) {
            increaseQuantity();
        }
        log.info("Set PDP quantity to " + getQuantityValue());
    }

    /**
     * Clicks the main (in-form) ADD TO CART rather than the sticky footer button, so the product
     * form's selected quantity is honoured. Uses a JS click so it works even when the button is
     * scrolled out of the viewport in headless runs.
     */
    public void clickMainAddToCart() {
        jsClick(addToCartButton);
        log.info("Clicked main (in-form) ADD TO CART");
    }

    private final By relatedProductLink = By.cssSelector("[class*='related'] a[href*='/products/'], [class*='recommend'] a[href*='/products/']");

    public boolean hasRelatedProducts() {
        return !driver.findElements(relatedProductLink).isEmpty();
    }

    // =====================
    // Actions
    // =====================

    public String getProductTitle() {
        return productTitle.getText();
    }
    public String getProductTitleOfProductCard() {
        wait.until(ExpectedConditions.visibilityOf(productTitleOfProductCard));
        return productTitleOfProductCard.getText();
    }

    public void clickAddToCart() {

        // Check if running in headless mode
        boolean isHeadless = Boolean.parseBoolean(
                System.getProperty("headless", "false"));

        if (isHeadless) {
            // In headless mode — page renders like mobile
            // Sticky footer ATC is visible, main ATC is off screen
            log.info("Headless mode — using sticky footer ATC button");
            clickElement(stickyFooterATCButton);
        } else {
            // In UI mode — main ATC near product image is visible
            log.info("UI mode — using main ATC button");
            clickElement(addToCartButton);
        }
    }

    // Reusable private method — scroll + wait + click with JS fallback
    private void clickElement(WebElement element) {
        try {
            // Scroll into view first
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", element);

            Thread.sleep(500);

            // Try normal click
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            log.info("Clicked using normal click ✅");

        } catch (Exception e) {
            // JS click fallback
            log.warn("Normal click failed — using JS click: " + e.getMessage());
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element);
            log.info("Clicked using JS click ✅");
        }
    }


    public void clickAddToCartAtProductCard() {
        safeClick(addToCartButtonAtProductCard);
    }


    public String getCartCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(cartCountBubble));
            return cartCountBubble.getText().trim();
        } catch (Exception e) {
            return "0";
        }
    }

    public void closeCart() {
        try{
            safeClick(cartCloseButton);
        } catch (Exception e) {
            // If close button isn't found or clickable, ignore — test can continue
        }
    }

}