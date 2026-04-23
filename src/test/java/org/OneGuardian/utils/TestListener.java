package org.OneGuardian.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);
    private static ExtentReports extent = ExtentReportManager.getInstance();
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ TEST STARTED: " + result.getName());
        ExtentTest test = extent.createTest(result.getName());
        extentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✅ TEST PASSED: " + result.getName());
        extentTest.get().log(Status.PASS, "Test Passed ✅");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("❌ TEST FAILED: " + result.getName());
        log.error("Reason: " + result.getThrowable().getMessage());

        extentTest.get().log(Status.FAIL,
                "Test Failed ❌ : " + result.getThrowable().getMessage());

        try {
            String screenshotPath = ScreenshotUtility.captureScreenshot(result.getName());
            extentTest.get().fail("Screenshot at failure:",
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } catch (Exception e) {
            log.error("Could not attach screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠ TEST SKIPPED: " + result.getName());
        extentTest.get().log(Status.SKIP, "Test Skipped ⚠");
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("🚀 SUITE STARTED: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("🏁 SUITE FINISHED: " + context.getName());
        extent.flush();
    }
}