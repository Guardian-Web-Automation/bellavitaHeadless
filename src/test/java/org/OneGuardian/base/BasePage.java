package org.OneGuardian.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {

    protected WebDriver driver;
    protected static WebDriverWait wait;
    protected JavascriptExecutor jsExecutor;
    protected static final Logger log = LogManager.getLogger(BasePage.class);

    // ========================================
    // CONSTRUCTOR
    // ========================================
    public BasePage(WebDriver driver) {

        if (driver == null) {
            throw new IllegalArgumentException(
                    "WebDriver cannot be null! Check BaseTest.setUp()");
        }
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;

        // Set timeout based on headless mode
        int timeout = Boolean.parseBoolean(
                System.getProperty("headless", "false")) ? 20 : 10;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        // Initialize all @FindBy elements
        PageFactory.initElements(driver, this);
    }

    // ========================================
    // SCROLL OPERATIONS
    // ========================================

    /**
     * Scrolls element into view (center of viewport)
     */
    public void scrollToElement(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
            log.debug("Scrolled to element: {}", element.getTagName());
            Thread.sleep(500); // Brief pause for scroll animation
        } catch (InterruptedException e) {
            log.warn("Sleep interrupted during scroll: {}", e.getMessage());
        }
    }
    public  void navigateTo() {
        driver.get("https://bvl---live-59c56cb80d07a4341b5e.o2.myshopify.dev");
        log.info("Navigated to homepage");
    }
    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Scrolls to element and clicks it
     */
    public void scrollAndClick(WebElement element) {
        try {
            scrollToElement(element);
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            log.info("✅ Scrolled and clicked element");
        } catch (StaleElementReferenceException e) {
            log.warn("Stale element during scroll-click, retrying with JS click");
            jsClick(element);
        } catch (Exception e) {
            log.warn("Normal click failed, using JS click: " + e.getMessage());
            jsClick(element);
        }
    }

    /**
     * Scrolls to element and gets text
     */
    public String scrollAndGetText(WebElement element) {
        try {
            scrollToElement(element);
            wait.until(ExpectedConditions.visibilityOf(element));
            String text = element.getText();
            log.debug("Retrieved text from scrolled element: " + text);
            return text;
        } catch (Exception e) {
            log.error("Failed to get text from scrolled element: " + e.getMessage());
            return "";
        }
    }

    // ========================================
    // CLICK OPERATIONS
    // ========================================

    /**
     * Safe click with explicit wait
     */
    public void safeClick(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            log.info("✅ Safe clicked element");
        } catch (Exception e) {
            log.warn("Safe click failed, using JS click: " + e.getMessage());
            jsClick(element);
        }
    }

    /**
     * JavaScript click (useful for elements hidden behind overlays)
     */
    public void jsClick(WebElement element) {
        try {
            scrollToElement(element);
            jsExecutor.executeScript("arguments[0].click();", element);
            log.info("✅ Clicked via JavaScript");
        } catch (Exception e) {
            log.error("JS click failed: " + e.getMessage());
            throw new RuntimeException("Failed to click element via JavaScript", e);
        }
    }

    /**
     * Click using coordinates (works with stubborn elements)
     */
    public void clickUsingCoordinates(WebElement element) {
        try {
            scrollToElement(element);
            org.openqa.selenium.interactions.Actions actions =
                    new org.openqa.selenium.interactions.Actions(driver);
            actions.moveToElement(element).click().perform();
            log.info("✅ Clicked using coordinates");
        } catch (Exception e) {
            log.warn("Coordinate click failed: " + e.getMessage());
            jsClick(element);
        }
    }

    // ========================================
    // WAIT OPERATIONS
    // ========================================

    /**
     * Wait for element to be visible
     */
    public void waitForVisibility(WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            log.debug("Element is now visible");
        } catch (Exception e) {
            log.error("Element visibility timeout: " + e.getMessage());
        }
    }

    /**
     * Wait for element to be clickable
     */
    public void waitForClickability(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            log.debug("Element is now clickable");
        } catch (Exception e) {
            log.error("Element clickability timeout: " + e.getMessage());
        }
    }

    /**
     * Wait for element invisibility
     */
    public void waitForInvisibility(WebElement element) {
        try {
            wait.until(ExpectedConditions.invisibilityOf(element));
            log.debug("Element is now invisible");
        } catch (Exception e) {
            log.error("Element invisibility timeout: " + e.getMessage());
        }
    }

    // ========================================
    // TEXT INPUT OPERATIONS
    // ========================================

    /**
     * Clear field and type text with wait
     */
    public void typeText(WebElement element, String text) {
        try {
            waitForVisibility(element);
            element.clear();
            element.sendKeys(text);
            log.info("✅ Typed text: " + text);
        } catch (Exception e) {
            log.error("Failed to type text: " + e.getMessage());
            throw new RuntimeException("Failed to type text", e);
        }
    }

    /**
     * Clear field using JavaScript
     */
    public void clearFieldWithJS(WebElement element) {
        try {
            jsExecutor.executeScript("arguments[0].value='';", element);
            log.info("Cleared field using JavaScript");
        } catch (Exception e) {
            log.warn("JS clear failed, using normal clear: " + e.getMessage());
            element.clear();
        }
    }

    // ========================================
    // VISIBILITY CHECK OPERATIONS
    // ========================================

    /**
     * Check if element is displayed
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            log.debug("Element is not displayed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if element exists in DOM
     */
    public boolean isElementPresent(WebElement element) {
        try {
            element.getTagName();
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            log.debug("Element not present in DOM");
            return false;
        }
    }

    /**
     * Check if element is enabled
     */
    public boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            log.debug("Element is not enabled: " + e.getMessage());
            return false;
        }
    }

    // ========================================
    // JAVASCRIPT OPERATIONS
    // ========================================

    /**
     * Execute JavaScript and return result
     */
    public Object executeJS(String script, Object... args) {
        try {
            return jsExecutor.executeScript(script, args);
        } catch (Exception e) {
            log.error("JavaScript execution failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Scroll page to top
     */
    public void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
        log.info("Scrolled to top of page");
    }

    /**
     * Scroll page to bottom
     */
    public void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        log.info("Scrolled to bottom of page");
    }

    /**
     * Get element's XPath using JavaScript
     */
    public String getElementXPath(WebElement element) {
        try {
            String xpathExpression =
                    "function getElementXPath(element) {" +
                            "  if (element.id !== '')" +
                            "    return \"//*[@id='\" + element.id + \"']\"; " +
                            "  if (element === document.body)" +
                            "    return \"/html/body\"; " +
                            "  var ix = 0; " +
                            "  var siblings = element.parentNode.childNodes; " +
                            "  for (var i = 0; i < siblings.length; i++) { " +
                            "    var sibling = siblings[i]; " +
                            "    if (sibling === element)" +
                            "      return getElementXPath(element.parentNode) + '//' + element.tagName.toLowerCase() + '[' + (ix + 1) + ']'; " +
                            "    if (sibling.nodeType === 1 && sibling.tagName.toLowerCase() === element.tagName.toLowerCase())" +
                            "      ix++; " +
                            "  } " +
                            "}" +
                            "return getElementXPath(arguments[0]);";
            return (String) jsExecutor.executeScript(xpathExpression, element);
        } catch (Exception e) {
            log.warn("Failed to get XPath: " + e.getMessage());
            return "";
        }
    }

    // ========================================
    // UTILITY OPERATIONS
    // ========================================

    /**
     * Wait for page load using document ready state
     */
    public void waitForPageLoad() {
        try {
            wait.until(driver ->
                    jsExecutor.executeScript("return document.readyState").equals("complete"));
            log.info("Page fully loaded");
        } catch (Exception e) {
            log.warn("Page load timeout: " + e.getMessage());
        }
    }

    /**
     * Switch to frame/iframe
     */
    public void switchToFrame(WebElement frameElement) {
        try {
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
            log.info("Switched to frame");
        } catch (Exception e) {
            log.error("Failed to switch to frame: " + e.getMessage());
        }
    }

    /**
     * Switch to default content (exit all frames)
     */
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        log.info("Switched to default content");
    }



    /**
     * Handle stale element reference with retry
     */
    public void handleStaleElement(WebElement element, Runnable action) {
        int retries = 3;
        while (retries > 0) {
            try {
                action.run();
                break;
            } catch (StaleElementReferenceException e) {
                retries--;
                if (retries == 0) {
                    log.error("Stale element after 3 retries");
                    throw e;
                }
                log.warn("Stale element reference, retrying... (" + retries + " retries left)");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public  void waitForUrlToContain(String s) {
        wait.until(ExpectedConditions.urlContains(s));
    }
}
