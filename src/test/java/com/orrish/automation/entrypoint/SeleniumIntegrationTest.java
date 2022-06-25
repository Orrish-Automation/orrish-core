package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SeleniumIntegrationTest {
    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.seleniumGridURL("http://hub:4444");
        setUp.browser("chrome");
        setUp.takeScreenshotAtEachStep(false);
        setUp.reportEnabled(true);
        setUp.defaultWaitTimeInSeconds(10);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    //@Test
    public void sampleTest() {
        SeleniumAppiumSteps seleniumAppiumSteps = new SeleniumAppiumSteps();
        assertTrue(seleniumAppiumSteps.launchBrowserAndNavigateTo("https://playwright.dev/"));
        //TODO : Below steps fails on Github CI, check later.
        /*
        assertTrue(seleniumAppiumSteps.clickIcon("GitHub repository"));
        assertTrue(seleniumAppiumSteps.switchToNewTab());
        assertTrue(seleniumAppiumSteps.getPageUrl().contains("github.com"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateBack());
        assertTrue(seleniumAppiumSteps.closeCurrentTab());
        */
        assertTrue(seleniumAppiumSteps.hoverOn("Node.js"));
        assertTrue(seleniumAppiumSteps.click("Python"));
        assertTrue(seleniumAppiumSteps.getPageUrl().contains("python"));

        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://mui.com/material-ui/react-checkbox/"));
        assertTrue(seleniumAppiumSteps.selectCheckboxForText("Parent"));
        assertTrue(seleniumAppiumSteps.unselectCheckboxForText("Parent"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://mui.com/material-ui/react-radio-button/"));
        assertTrue(seleniumAppiumSteps.selectRadioForText("Male"));
        assertTrue(seleniumAppiumSteps.scrollTo("Male"));

        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertTrue(seleniumAppiumSteps.checkTextIsPresentInWebpage("Inputs"));
        assertTrue("Inputs".equals(seleniumAppiumSteps.getFullTextFor("//a[contains(text(), 'Inputs')]")));
        assertTrue("Sortable Data Tables".equals(seleniumAppiumSteps.getFullTextFor("Tables")));
        assertTrue(seleniumAppiumSteps.clickWithPartialText("Inputs"));
        assertTrue(seleniumAppiumSteps.waitUntilIsGone("//a[contains(text(), 'Inputs')]"));
        assertTrue(seleniumAppiumSteps.waitUntilIsGone("Hovers"));
        assertTrue(seleniumAppiumSteps.typeIn("123", "input"));
        assertTrue(seleniumAppiumSteps.click("input"));
        assertTrue(seleniumAppiumSteps.clickNumber("input", "1"));
        assertTrue(seleniumAppiumSteps.clearText());
        assertTrue(seleniumAppiumSteps.getFullTextFor("input").equals(""));
        assertTrue(seleniumAppiumSteps.typeInNumber("456", "1"));
        assertTrue(seleniumAppiumSteps.rightClick("input"));
        //TODO: Press key throws exception.
        //assertTrue(seleniumAppiumSteps.pressKey("Escape"));
        //TODO: Issue with frame
        //assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/tinymce"));
        //assertTrue(seleniumAppiumSteps.typeWithPartialText("hello", "Your content goes here."));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/disappearing_elements"));
        assertTrue(seleniumAppiumSteps.clickToTheOf("This example", "below", "Disappearing Elements"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertTrue(seleniumAppiumSteps.refreshWebPage());
        assertTrue(seleniumAppiumSteps.waitUntilIsDisplayed("//a[contains(text(), 'Inputs')]"));
        assertTrue(seleniumAppiumSteps.waitUntilIsDisplayed("Inputs"));
        assertTrue(seleniumAppiumSteps.waitUntilContains("//a[contains(text(), 'Inputs')]", "Inputs"));
        assertTrue(seleniumAppiumSteps.waitUntilDoesNotContain("//a[contains(text(), 'Inputs')]", "Hello"));
        assertTrue(seleniumAppiumSteps.clickWhicheverIsDisplayedIn("hello,,Sortable Data Tables"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateBack());
        assertTrue(seleniumAppiumSteps.waitUntilOneOfIsDisplayed("DoesNotExist,,Checkboxes"));
        assertTrue(seleniumAppiumSteps.waitUntilOneOfIsDisplayed("//a[contains(text(), 'Checkboxes')],,//a[contains(text(), 'DoesNotExist')]"));
        assertTrue(seleniumAppiumSteps.waitUntilOneOfIsEnabled("//a[contains(text(), 'Checkboxes')]"));
        assertTrue(seleniumAppiumSteps.clickWhicheverIsDisplayedIn("//a[contains(text(), 'Checkboxes')],,none"));
        assertTrue(seleniumAppiumSteps.clickIconNextTo("Fork me", "Checkboxes"));
        assertTrue(seleniumAppiumSteps.getPageUrl().contains("github.com"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/dropdown"));
        assertTrue(seleniumAppiumSteps.selectFromDropdown("Option 2", "Please select an option"));
        assertTrue(seleniumAppiumSteps.takeWebScreenshotWithText("Step1"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/tables"));
        assertEquals(seleniumAppiumSteps.getColumnWhere("Last Name", "First Name=Frank"), "Bach");
        assertTrue(seleniumAppiumSteps.clickColumnWhere("Last Name", "First Name=Frank"));
        assertTrue(seleniumAppiumSteps.clickInColumnWhere("edit", "Action", "First Name=Frank"));
        //assertTrue(playwrightAppiumSteps.typeInColumnWhere("Some value", "Last Name", "First Name=John"));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/upload"));
        assertTrue(seleniumAppiumSteps.uploadFile("filename.txt"));
        seleniumAppiumSteps.click("Upload");
        assertTrue(seleniumAppiumSteps.checkTextIsPresentInWebpage("File Uploaded!"));
        assertTrue(seleniumAppiumSteps.checkAccessibilityForPage(seleniumAppiumSteps.getPageTitle()));
        assertTrue(seleniumAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/javascript_alerts"));
        assertTrue(seleniumAppiumSteps.click("Click for JS Alert"));
        assertTrue("I am a JS Alert".equals(seleniumAppiumSteps.getAlertText()));
        assertTrue(seleniumAppiumSteps.acceptAlertIfPresent());
        assertEquals(seleniumAppiumSteps.getTextFromToTheOf("You ", "below", "Result:"), "You successfully clicked an alert");
        assertTrue(seleniumAppiumSteps.executeJavascript("a=2"));

        // */
    }

}
