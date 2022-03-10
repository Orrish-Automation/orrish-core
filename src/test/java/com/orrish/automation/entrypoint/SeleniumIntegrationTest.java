package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class SeleniumIntegrationTest {
    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.seleniumGridURL("http://hub:4444");
        setUp.browser("FIREFOX");
        setUp.takeScreenshotAtEachStep(false);
        setUp.reportEnabled(false);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    @Test
    public void sampleTest() {
        SeleniumAppiumSteps seleniumAppiumSteps = new SeleniumAppiumSteps();
        assertTrue(seleniumAppiumSteps.launchBrowserAndNavigateTo("http://github.com"));
        assertTrue(seleniumAppiumSteps.enterInTextFieldFor("Orrish core", "input", "search text box"));
        assertTrue(seleniumAppiumSteps.clickFor("#jump-to-suggestion-search-global", "suggestion box"));
        assertTrue(seleniumAppiumSteps.waitUntilIsGoneFor("#jump-to-suggestion-search-global", "suggestion box"));
    }

}
