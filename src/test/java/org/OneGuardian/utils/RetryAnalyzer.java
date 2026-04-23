package org.OneGuardian.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);

    private int retryCount = 0;          // tracks current attempt
    private static final int MAX_RETRY = 1; // max times to retry a failed test

    // retry() is called by TestNG automatically every time a test fails
    // Return true  → TestNG will re-run the test
    // Return false → TestNG marks it as finally failed, moves on
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            log.warn("🔄 RETRYING test: [" + result.getName() + "] "
                    + "Attempt " + retryCount + " of " + MAX_RETRY);
            return true;  // re-run the test
        }
        log.error("❌ Test FINALLY FAILED after " + MAX_RETRY
                + " retries: " + result.getName());
        return false; // stop retrying, mark as failed
    }
}