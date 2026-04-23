package org.OneGuardian.utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

// IAnnotationTransformer lets us modify @Test annotations at runtime
// We use it to inject RetryAnalyzer into every @Test automatically
public class RetryListener implements IAnnotationTransformer {

    // transform() is called by TestNG for every @Test annotation it finds
    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {

        // Attach our RetryAnalyzer to every single @Test automatically
        // Without this you'd have to write @Test(retryAnalyzer = RetryAnalyzer.class)
        // on every test method manually
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}