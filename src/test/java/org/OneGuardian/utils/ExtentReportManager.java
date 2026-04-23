package org.OneGuardian.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {

    // Single shared ExtentReports instance for the entire test run
    private static ExtentReports extent;

    public static ExtentReports getInstance() {

        // Only create once — if already created, return existing instance
        if (extent == null) {

            // ExtentSparkReporter → defines WHERE the HTML file is saved and HOW it looks
            String reportPath = System.getProperty("user.dir")
                    + "/reports/TestReport.html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            // Report appearance config
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("BellaVita Headless Test Report");
            sparkReporter.config().setReportName("Automation Test Results");
            sparkReporter.config().setTimeStampFormat("dd/MM/yyyy HH:mm:ss");

            extent = new ExtentReports();

            // Attach the spark reporter to ExtentReports
            extent.attachReporter(sparkReporter);

            // Add system/environment info — shows up in the report dashboard
            extent.setSystemInfo("Project", "BellaVita Luxury HeadLess Testing");
            extent.setSystemInfo("Tester", "Gourav");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Browser", "Chrome");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }

        return extent;
    }
}