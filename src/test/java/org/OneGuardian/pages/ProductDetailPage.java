package org.OneGuardian.pages;

import org.OneGuardian.base.BasePage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductDetailPage extends BasePage {


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

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
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


    public boolean isCartNotificationVisible() {
        try {
            // Wait for notification to appear after ATC click
            wait.until(ExpectedConditions.visibilityOf(cartNotification));
            return cartNotification.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartNotificationProductTitle() {
        wait.until(ExpectedConditions.visibilityOf(cartNotificationProductTitle));
        return cartNotificationProductTitle.getText();
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