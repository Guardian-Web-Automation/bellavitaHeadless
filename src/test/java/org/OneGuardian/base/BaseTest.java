package org.OneGuardian.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {
    public static WebDriver driver;
    protected static final Logger log = LogManager.getLogger(BaseTest.class);


    @BeforeMethod
    public void setUp() {
        log.info("Setting up WebDriver...");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--window-size=1920,1080");

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
            log.info("Running in HEADLESS mode ✅");
        } else {
            options.addArguments("--start-maximized");
            log.info("Running in UI mode ✅");
        }

        // ✅ Suppresses CDP version mismatch warning
        java.util.logging.Logger.getLogger("org.openqa.selenium")
                .setLevel(java.util.logging.Level.SEVERE);

        driver = new ChromeDriver(options);
        // In --headless=new on a displayless CI runner, window().maximize() can shrink the window to a
        // small default, overriding --window-size and collapsing the storefront header into its mobile
        // layout (which hides the desktop Account icon the login flow clicks). Pin an explicit desktop
        // viewport in headless and only maximize for real headed runs.
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        } else {
            driver.manage().window().maximize();
        }
        driver.manage().timeouts()
                .implicitlyWait(java.time.Duration.ofSeconds(10));

        log.info("Browser launched successfully ✅");
    }

    @AfterMethod
    public void tearDown(){
        log.info("Tearing down WebDriver...");
        if (driver != null) {
            driver.quit();
        }
        log.info("Browser closed ✅");
    }
}
