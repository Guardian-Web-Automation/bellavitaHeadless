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
    protected static WebDriver driver;
    protected static final Logger log = LogManager.getLogger(BaseTest.class);


    @BeforeMethod
    public void setUp(){
        log.info("Setting up WebDriver...");
        // Initialize WebDriver and set up the browser
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        System.setProperty("webdriver.chrome.silentOutput", "true");

        driver= new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
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
