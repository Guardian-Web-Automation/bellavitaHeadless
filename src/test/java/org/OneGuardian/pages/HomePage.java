package org.OneGuardian.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    WebDriver driver;
    private WebDriverWait wait;

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


    // Constructor — PageFactory.initElements wires up all @FindBy fields
    // Must be called with the driver so Selenium knows which browser to look in
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ==============================
    // Page Actions — what a user can DO on this page
    // ==============================

    public void navigateTo() {
        driver.get("https://www.bellavitaluxury.co.in/");
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void clickSearchIcon() {
        searchIcon.click();
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
        wait.until(ExpectedConditions.elementToBeClickable(firstProductLink));
        firstProductLink.click();
    }
}