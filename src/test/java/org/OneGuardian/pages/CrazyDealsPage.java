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
import java.util.List;

public class CrazyDealsPage extends BasePage {

    // =====================
    // Locators
    // =====================

    // CRAZY DEALS nav link
    @FindBy(xpath = "//div[@class='header-nav-inner']//a[contains(text(),'Crazy Deals')]")
    private WebElement crazyDealsNavLink;

    // Crazy Deals page title
    @FindBy(xpath = "//h1[@class='collection-page-title' and contains(text(),'Crazy Deals')]")
    private WebElement crazyDealsTitle;

    // "Build Your Box" button on the deal card
    @FindBy(css = "a.button--secondary")
    private WebElement buildYourBoxButton;

    // All product titles on Build Your Box page
    @FindBy(xpath = "//button[contains(@class,'build-a-box-card-title' ) ]//..//..//button[@aria-label='Add to box']")
    private List<WebElement> productTitles;

    // All + (add) buttons on product cards
    @FindBy(xpath = "//button[contains(@class,'build-a-box-add-one')]")
    private List<WebElement> addButtons;

    // Progress bar text — "0/3 ADDED", "1/3 ADDED" etc
    @FindBy(xpath = "//div[@class='build-a-box-sticky-bar__count build-a-box-sticky-bar__count--top-right']")
    private WebElement progressText;

    @FindBy(xpath = "//span[@class='build-a-box-sticky-bar__count']")
    private WebElement progressTextAfterAdding;


    // Deal unlocked message — "You've unlocked ₹1,298.00 Deal Price"
    @FindBy(xpath = "//span[@class='build-a-box-sticky-bar__total' and contains(text(),'unlocked')]")
    private WebElement dealUnlockedMessage;

    // Pay Now button — appears after 3/3 added
    @FindBy(xpath = "//button[@class='build-a-box-drawer__pay-btn']")
    private WebElement payNowButton;

    // "Your Box (x/3)" bottom drawer title
    @FindBy(css = "div.bundle-drawer__title")
    private WebElement yourBoxTitle;

    // Bill summary total in bottom drawer
    @FindBy(xpath = "//div[@class='build-a-box-sticky-bar__price-block']//p[2]")
    private WebElement billTotal;

    // Saving amount shown in bottom drawer
    @FindBy(css = "span.bundle-drawer__saving-value")
    private WebElement savingAmount;

    // Items count in bottom drawer — "Subtotal (x items)"
    @FindBy(css = "span.bundle-drawer__subtotal-label")
    private WebElement subtotalLabel;

    // Close button on product detail modal
    @FindBy(css = "button.modal-close-btn")
    private WebElement modalCloseButton;

    // "Add To Box" button inside product detail modal
    @FindBy(css = "button.add-to-box-btn")
    private WebElement addToBoxModalButton;

    // Item value price block — "₹565.00" (deal price)
    @FindBy(css = "div.build-a-box-sticky-bar__price-block p.text-base")
    private WebElement itemValuePrice;

    // Strikethrough original price — "₹999.00"
    @FindBy(css = "div.build-a-box-sticky-bar__price-block span.line-through")
    private WebElement originalPrice;

    // =====================
    // Constructor
    // =====================

    public CrazyDealsPage(WebDriver driver) {
        super(driver);
    }

    // =====================
    // Actions
    // =====================

    public void clickCrazyDealsNav() {
        safeClick(crazyDealsNavLink);
        log.info("Clicked CRAZY DEALS nav link");
    }

    public boolean isCrazyDealsTitleDisplayed() {
        try {
            waitForVisibility(crazyDealsTitle);
            return isElementDisplayed(crazyDealsTitle);
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBuildYourBox(String dealName) {
        log.info("Looking for deal: " + dealName);
        WebElement buildYourBoxBtnForDeal = driver.findElement(By.xpath("//h3[contains(text(),'" + dealName + "')]/ancestor::div[contains(@class,'collection-product-card')]//a[2]")
        );
        safeClick(buildYourBoxBtnForDeal);
        log.info("Clicked Build Your Box button");
        // Wait for products to load
        wait.until(ExpectedConditions.visibilityOfAllElements(productTitles));
    }

    // ─────────────────────────────────────────
    // Dynamic — add product by name
    // ─────────────────────────────────────────
    public boolean addProductToBox(String productName) {
        log.info("Looking for product: " + productName);

        try {
            // Build dynamic XPath — productName injected directly
            // Finds the product title button, goes up 2 levels,
            // then finds the Add to box button in the same card
            String xpath = "//button[contains(@class,'build-a-box-card-title')" +
                    " and contains(text(),'" + productName + "')]" +
                    "//..//..//button[@aria-label='Add to box']";

            WebElement addBtn = driver.findElement(By.xpath(xpath));

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", addBtn);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            // Click
            try {
                wait.until(ExpectedConditions.elementToBeClickable(addBtn));
                addBtn.click();
                log.info("✅ Added to box: " + productName);
            } catch (Exception e) {
                log.warn("Normal click failed — using JS click");
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();", addBtn);
                log.info("✅ Added via JS: " + productName);
            }

            // Wait for progress to update
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return true;

        } catch (Exception e) {
            log.error("❌ Product not found: " + productName
                    + " | Error: " + e.getMessage());
            return false;
        }
    }


    public String getProgressText() {
        // driver.findElements() → returns INSTANTLY with empty list if not found
        // Never throws exception — no timeout wasted ✅

        // Check SPAN first — active state after adding items
        List<WebElement> spanProgress = driver.findElements(
                By.xpath("//span[@class='build-a-box-sticky-bar__count']"));

        if (!spanProgress.isEmpty()) {
            String text = spanProgress.get(0).getText().trim();
            return text;
        }

        // Fallback to DIV — initial 0/3 state
        List<WebElement> divProgress = driver.findElements(
                By.xpath("//div[@class='build-a-box-sticky-bar__count " +
                        "build-a-box-sticky-bar__count--top-right']"));

        if (!divProgress.isEmpty()) {
            String text = divProgress.get(0).getText().trim();
            log.info("Progress text (div): " + text);
            return text;
        }

        log.warn("Progress text not found in either state");
        return "";
    }

    public boolean isDealUnlocked() {
        try {
            waitForVisibility(dealUnlockedMessage);
            return isElementDisplayed(dealUnlockedMessage);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPayNowButtonDisplayed() {
        try {
            waitForVisibility(payNowButton);
            return isElementDisplayed(payNowButton);
        } catch (Exception e) {
            return false;
        }
    }

    public String getBillTotal() {
        try {
            waitForVisibility(billTotal);
            return billTotal.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getItemValuePrice() {
        try {
            waitForVisibility(itemValuePrice);

            // Get full text — returns "₹565.00  ₹999.00"
            // Then remove the original/strikethrough price part
            // getOriginalPrice() gives us "₹999.00" to strip out
            String fullText = itemValuePrice.getText().trim();

            // Get strikethrough price text to remove it
            String strikePrice = originalPrice.getText().trim();

            // Remove strikethrough price + extra spaces → leaves "₹565.00"
            String dealPrice = fullText.replace(strikePrice, "").trim();

            // Clean up any remaining &nbsp; characters
            dealPrice = dealPrice.replaceAll("[\\u00A0\\s]+", "").trim();

            log.info("Item value price: " + dealPrice);
            return dealPrice;

        } catch (Exception e) {
            log.warn("Could not get item value price: " + e.getMessage());
            return "";
        }
    }

    public String getOriginalPrice() {
        try {
            waitForVisibility(originalPrice);
            String price = originalPrice.getText().trim();
            log.info("Original price: " + price);
            return price;
        } catch (Exception e) {
            log.warn("Could not get original price: " + e.getMessage());
            return "";
        }
    }

    public String getSavingAmount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(savingAmount));
            return savingAmount.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getYourBoxTitle() {
        try {
            wait.until(ExpectedConditions.visibilityOf(yourBoxTitle));
            return yourBoxTitle.getText().trim();
        } catch (Exception e) {
            return "";
        }

    }

    // Click + button multiple times for same product
    public boolean addProductToBoxMultipleTimes(String productName, int times) {
        log.info("Adding product: " + productName + " × " + times);

        for (int t = 0; t < times; t++) {
            try {
                WebElement addBtn;

                if (t == 0) {
                    // ── First click ──
                    // Button says "Add to box" — initial state
                    String xpath = "//button[contains(@class,'build-a-box-card-title')" +
                            " and contains(text(),'" + productName + "')]" +
                            "//..//..//button[@aria-label='Add to box']";
                    addBtn = driver.findElement(By.xpath(xpath));
                    log.info("Using 'Add to box' button for first click");

                } else {
                    // ── Subsequent clicks ──
                    // Button changes to "Increase quantity" after first click
                    String xpath = "//button[contains(@class,'build-a-box-card-title')" +
                            " and contains(text(),'" + productName + "')]" +
                            "//..//..//button[@aria-label='Increase quantity']";
                    addBtn = driver.findElement(By.xpath(xpath));
                    log.info("Using 'Increase quantity' button for click " + (t + 1));
                }

                // Scroll into view
                scrollToElement(addBtn);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                try {
                    safeClick(addBtn);
                    log.info("✅ Click " + (t + 1) + "/" + times
                            + " for: " + productName);
                } catch (Exception e) {
                    log.warn("Normal click failed — using JS click");
                   jsClick(addBtn);
                    log.info("✅ JS click " + (t + 1) + "/"
                            + times + " for: " + productName);
                }

                // Wait for DOM to update after each click
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

            } catch (Exception e) {
                log.error("❌ Failed at click " + (t + 1)
                        + " for: " + productName + " | " + e.getMessage());
                return false;
            }
        }

        log.info("✅ Successfully added " + productName + " × " + times);
        return true;
    }
}