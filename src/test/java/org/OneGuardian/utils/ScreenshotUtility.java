package org.OneGuardian.utils;

import org.OneGuardian.base.BaseTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtility extends BaseTest {

    // TakesScreenshot is an interface implemented by ChromeDriver
    // We cast our WebDriver to it to unlock the screenshot capability
    public static String captureScreenshot(String testName) {

        // Cast driver to TakesScreenshot — this is how Selenium captures screen

        TakesScreenshot ts =(TakesScreenshot)driver;
        File srcFile = ts.getScreenshotAs(OutputType.FILE);


        // Add timestamp to filename so screenshots never overwrite each other
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotPath = System.getProperty("user.dir")
                + "/screenshots/" + testName + "_" + timestamp + ".png";

        File destFile = new File(screenshotPath);

        try {
            // FileUtils.copyFile → copies the temp screenshot to our /screenshots folder
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("Screenshot saved: " + screenshotPath);
        } catch (IOException e) {
            System.out.println("Failed to save screenshot: " + e.getMessage());
        }

        // Return path — ExtentReports will use this later to embed image in report
        return screenshotPath;
    }
}