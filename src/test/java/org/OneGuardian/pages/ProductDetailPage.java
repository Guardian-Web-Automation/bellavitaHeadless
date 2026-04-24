package org.OneGuardian.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ProductDetailPage {

    private static final Logger log = LoggerFactory.getLogger(ProductDetailPage.class);
    WebDriver driver;
    WebDriverWait wait;

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
        this.driver = driver;
        int timeout = Boolean.parseBoolean(
                System.getProperty("headless", "false")) ? 20 : 10;
        // Wait up to 10 seconds for elements — ATC button can be slow to load
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
        // Scroll the element into view first — critical for headless mode
        // In headless, viewport is small so elements below fold are "invisible"
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", addToCartButton);

        // Small pause after scroll to let page settle
        try { Thread.sleep(500); } catch (InterruptedException e) { }

        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        addToCartButton.click();
    }

//    public void clickAddToCart() {
//        // Explicit wait — ensures button is clickable before clicking
//        // ATC button can be disabled briefly while page loads variants
//        scrollToElementAndPerformClick(addToCartButton);
//    }

    private void scrollToElementAndPerformClick(WebElement element) {
        try {
            log.info("Scrolling to element and clicking - Element located by: {}",
                     element.toString().contains("xpath") ? "XPath" : "CSS Selector");
            // Wait for element to be visible
            wait.until(ExpectedConditions.visibilityOf(element));

            // Wait for element to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(element));

            // Scroll to element using JavaScript (scrollIntoView with true = top aligned)
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", element);

            // Small delay to ensure element is fully in view and rendered
            Thread.sleep(300);
            // Click the element
            element.click();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while scrolling and clicking element", e);
        }
    }

            public void clickAddToCartAtProductCard() {
        // Explicit wait — ensures button is clickable before clicking
        // ATC button can be disabled briefly while page loads variants
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButtonAtProductCard));
        addToCartButtonAtProductCard.click();
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
            wait.until(ExpectedConditions.elementToBeClickable(cartCloseButton));
            cartCloseButton.click();
        } catch (Exception e) {
            // If close button isn't found or clickable, ignore — test can continue
        }
    }
}