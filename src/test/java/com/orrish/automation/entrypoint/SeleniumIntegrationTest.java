package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SeleniumIntegrationTest {
    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.seleniumGridURL("http://hub:4444");
        setUp.browser("FIREFOX");
        setUp.takeScreenshotAtEachStep(false);
        setUp.reportEnabled(true);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    @Test
    public void sampleTest() {
        SeleniumAppiumSteps seleniumAppiumSteps = new SeleniumAppiumSteps();
        assertTrue(seleniumAppiumSteps.launchBrowserAndNavigateTo("https://the-internet.herokuapp.com/"));
        //assertTrue(seleniumAppiumSteps.launchBrowserAndNavigateTo("https://the-internet.herokuapp.com/disappearing_elements"));
        //assertTrue(seleniumAppiumSteps.clickToTheOf("About", "right-of", "Home"));
        assertTrue(seleniumAppiumSteps.refreshWebPage());
        assertTrue(seleniumAppiumSteps.maximizeTheWindow());
        assertTrue(seleniumAppiumSteps.executeJavascript("a=2"));
        assertTrue(seleniumAppiumSteps.waitUntilElementTextContains("//*[contains(text(),'Inputs')]", "Inputs"));
        assertTrue(seleniumAppiumSteps.click("Inputs"));
        assertTrue(seleniumAppiumSteps.waitUntilIsGoneFor("//a[contains(text(),'Inputs')]", "input link"));
        assertTrue(seleniumAppiumSteps.enterInTextFieldFor("123", "input", "input text"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateBack());
        assertTrue(seleniumAppiumSteps.waitUntilElementTextDoesNotContain("//a", "Does not exist"));
        assertTrue(seleniumAppiumSteps.executeJavascriptOnElement("arguments[0].scrollIntoView(true);", "//*[contains(text(),'Sortable Data Tables')]"));
        assertTrue(seleniumAppiumSteps.clickExactly("Sortable Data Tables"));
        assertTrue(seleniumAppiumSteps.clickRowContainingText("Frank"));
        assertTrue(seleniumAppiumSteps.isElementDisplayedFor("#table1", "Table 1"));
        assertTrue(seleniumAppiumSteps.waitUntilOneOfTheElementsIsDisplayed("#table1,,#error"));
        assertTrue(seleniumAppiumSteps.takeWebScreenshotWithText("Sample"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/javascript_alerts"));
        seleniumAppiumSteps.checkAccessibilityForPage(seleniumAppiumSteps.getPageTitle());
        assertTrue(seleniumAppiumSteps.clickWithText("//button", "Click for JS Alert"));
        seleniumAppiumSteps.getAlertText();
        assertTrue(seleniumAppiumSteps.dismissAlertIfPresent());
        assertTrue(seleniumAppiumSteps.closeBrowser());
        assertTrue(seleniumAppiumSteps.quitBrowser());
        // */
    }

}
