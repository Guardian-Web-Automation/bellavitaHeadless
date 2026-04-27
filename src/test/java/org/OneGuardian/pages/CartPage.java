package org.OneGuardian.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartPage {

    WebDriver driver;
    WebDriverWait wait;
    private static final Logger log = LogManager.getLogger(CartPage.class);

    // =====================
    // Locators
    // =====================

    // Cart icon in header
    @FindBy(xpath = "//div[@class='header-actions']//button[@aria-label='Cart']")
    private WebElement cartIcon;

    // Cart page title
    @FindBy(xpath = "//div[@class='side-cart']//h2[text()='CART']")
    private WebElement cartTitle;

    // Cart item rows (each product in cart is a row in the table)
    @FindBy(xpath = "//div[@class='side-cart-body']//div[@class='side-cart-line']")
    private WebElement cartItemRows;

    // First product name in cart
    @FindBy(xpath = "(//div[@class='side-cart-body']//div[@class='side-cart-line'])[1]//div//a")
    private WebElement FirstcartItemName;

    // Quantity input field of first item
    @FindBy(xpath = "(//div[@class='side-cart-body']//div[@class='side-cart-line'])[1]//div[@class='side-cart-quantity']/span")
    private WebElement quantityInput;

    // Increase quantity button ( + )
    @FindBy(xpath = "(//div[@class='side-cart-body']//div[@class='side-cart-line'])[1]//div[@class='side-cart-quantity']//button[@aria-label='Increase quantity']")
    private WebElement increaseQtyButton;

    // Decrease quantity button ( - )
    @FindBy(xpath = "(//div[@class='side-cart-body']//div[@class='side-cart-line'])[1]//div[@class='side-cart-quantity']//button[@aria-label='Decrease quantity']")
    private WebElement decreaseQtyButton;

    // Remove item button
    @FindBy(xpath = "(//div[@class='side-cart-body']//div[@class='side-cart-line'])[1]//button[@aria-label='Remove item']")
    private WebElement removeItemButton;

    // Empty cart message
    @FindBy(xpath = "//div[@class='side-cart-empty']//h2")
    private WebElement emptyCartMessage;

    // Cart total price
    @FindBy(xpath = "//span[@class='side-cart-checkout-total']//div")
    private WebElement cartTotalPrice;

    // Checkout button
    @FindBy(xpath = "//span[@class='side-cart-checkout-text']")
    private WebElement checkoutButton;

    // Cart item count in header bubble
    @FindBy(xpath = "//div[@class='header-actions']//button[@aria-label='Cart']//span")
    private WebElement cartCountBubble;

    //Cart close button (X) in cart sidebar
    @FindBy(xpath = "//button[@aria-label='Close cart']")
    private WebElement cartCloseButton;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        int timeout = Boolean.parseBoolean(
                System.getProperty("headless", "false")) ? 20 : 10;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        PageFactory.initElements(driver, this);
    }

    // =====================
    // Actions
    // =====================

    public void clickCartIcon() {
        try{
            wait.until(ExpectedConditions.elementToBeClickable(cartIcon));
            cartIcon.click();
            log.info("Clicked cart icon");
        } catch (Exception e) {
            // If close button isn't found or clickable, ignore — test can continue
        }
    }


    public boolean isCartPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(cartTitle));
            return cartTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartItemName() {
        try{
            wait.until(ExpectedConditions.visibilityOf(FirstcartItemName));
            return FirstcartItemName.getText();
        }catch (Exception e){
            return "";
        }
    }

    public String getCartItemCount() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", cartCountBubble);
            wait.until(ExpectedConditions.visibilityOf(cartCountBubble));
            return cartCountBubble.getText().trim();
        } catch (Exception e) {
            return "0";
        }
    }

    public String getQuantity() {
        wait.until(ExpectedConditions.visibilityOf(quantityInput));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", quantityInput);
        return quantityInput.getText().trim();
    }

    public void clickIncreaseQuantity() {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", increaseQtyButton);
        wait.until(ExpectedConditions.elementToBeClickable(increaseQtyButton));
        increaseQtyButton.click();
        log.info("Clicked increase quantity button");
        // Wait for quantity to update
        try { Thread.sleep(1000); } catch (InterruptedException e) { }
    }

    public void clickDecreaseQuantity() {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", decreaseQtyButton);
        wait.until(ExpectedConditions.elementToBeClickable(decreaseQtyButton));
        decreaseQtyButton.click();
        log.info("Clicked decrease quantity button");
        try { Thread.sleep(1000); } catch (InterruptedException e) { }
    }

    public void clickRemoveItem() {
        wait.until(ExpectedConditions.elementToBeClickable(removeItemButton));
        removeItemButton.click();
        log.info("Clicked remove item button");
        try { Thread.sleep(1500); } catch (InterruptedException e) { }
    }

    public boolean isCartEmpty() {
        try {
            wait.until(ExpectedConditions.visibilityOf(emptyCartMessage));
            return emptyCartMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartTotalPrice() {
       try{
           ((JavascriptExecutor) driver).executeScript(
                   "arguments[0].scrollIntoView(true);", cartTotalPrice);
           wait.until(ExpectedConditions.visibilityOf(cartTotalPrice));
           return cartTotalPrice.getText().trim();
       }catch (Exception e) {
           return "";
       }
    }

    public void clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
        checkoutButton.click();
        log.info("Clicked checkout button");
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