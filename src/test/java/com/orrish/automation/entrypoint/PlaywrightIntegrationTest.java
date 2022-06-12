package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class PlaywrightIntegrationTest {

    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.playwrightHeadless(true);
        setUp.browser("chrome");
        setUp.reportEnabled(true);
        setUp.fullPageScreenshot(false);
        setUp.takeScreenshotAtEachStep(false);
        setUp.playwrightDefaultNavigationWaitTimeInSeconds(20);
        setUp.defaultWaitTimeInSeconds(10);
        //setUp.executionCapabilities("enableVideo=true");
        setUp.useMock(true);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    @Test
    public void playwrightTest() {

        /*
        PlaywrightAppiumSteps playwrightAppiumSteps = new PlaywrightAppiumSteps();
        assertTrue(playwrightAppiumSteps.launchBrowserAndNavigateTo("https://jsonplaceholder.typicode.com/users/1"));
        ///*
    }

    public void anotherTest() {
        //*/
        PlaywrightAppiumSteps playwrightAppiumSteps = new PlaywrightAppiumSteps();
        assertTrue(playwrightAppiumSteps.launchBrowserAndNavigateTo("https://jsonplaceholder.typicode.com/users/1"));
        playwrightAppiumSteps.forRequestUseMockStatusAndResponse("**/users/2", "200", "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Orrish Automation\",\n" +
                "  \"username\": \"Orrish\",\n" +
                "  \"email\": \"Sincere@april.biz\",\n" +
                "  \"phone\": \"1-770-736-8031 x56442\",\n" +
                "  \"website\": \"hildegard.org\"\n" +
                "}"
        );
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://jsonplaceholder.typicode.com/users/2"));
        assertTrue(playwrightAppiumSteps.checkTextIsPresentInWebpage("Orrish"));

        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://playwright.dev/"));
        assertTrue(playwrightAppiumSteps.clickIcon("GitHub repository"));
        assertTrue(playwrightAppiumSteps.switchToNewTab());
        assertTrue(playwrightAppiumSteps.getPageUrl().contains("github.com"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateBack());
        assertTrue(playwrightAppiumSteps.closeCurrentTab());
        assertTrue(playwrightAppiumSteps.hoverOn("Node.js"));
        assertTrue(playwrightAppiumSteps.click("Python"));
        assertTrue(playwrightAppiumSteps.getPageUrl().contains("python"));

        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://mui.com/material-ui/react-checkbox/"));
        assertTrue(playwrightAppiumSteps.scrollTo("Parent"));
        assertTrue(playwrightAppiumSteps.selectCheckboxForText("Parent"));
        assertTrue(playwrightAppiumSteps.unselectCheckboxForText("Parent"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://mui.com/material-ui/react-radio-button/"));
        assertTrue(playwrightAppiumSteps.selectRadioForText("Male"));

        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertTrue(playwrightAppiumSteps.checkTextIsPresentInWebpage("Inputs"));
        assertTrue("Inputs".equals(playwrightAppiumSteps.getFullTextFor("//a[contains(text(), 'Inputs')]")));
        assertTrue("Sortable Data Tables".equals(playwrightAppiumSteps.getFullTextFor("//a[contains(text(), 'Tables')]")));
        assertTrue(playwrightAppiumSteps.clickExactly("Inputs"));
        assertTrue(playwrightAppiumSteps.waitUntilIsGone("//a[contains(text(), 'Inputs')]"));
        assertTrue(playwrightAppiumSteps.waitUntilIsGone("Hovers"));
        assertTrue(playwrightAppiumSteps.typeIn("123", "input"));
        assertTrue(playwrightAppiumSteps.click("input"));
        assertTrue(playwrightAppiumSteps.clearText());
        assertTrue(playwrightAppiumSteps.getFullTextFor("input").equals(""));
        assertTrue(playwrightAppiumSteps.typeInTextFieldNumber("456", "1"));
        assertTrue(playwrightAppiumSteps.rightClick("input"));
        assertTrue(playwrightAppiumSteps.pressKey("Escape"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/tinymce"));
        assertTrue(playwrightAppiumSteps.typeInExactly("hello", "Your content goes here."));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/disappearing_elements"));
        assertTrue(playwrightAppiumSteps.clickToTheOf("About", "right-of", "Home"));
        assertTrue(playwrightAppiumSteps.getPageUrl().contains("about"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertTrue(playwrightAppiumSteps.refreshWebPage());
        assertTrue(playwrightAppiumSteps.waitUntilIsDisplayed("//a[contains(text(), 'Inputs')]"));
        assertTrue(playwrightAppiumSteps.waitUntilIsDisplayed("Inputs"));
        assertTrue(playwrightAppiumSteps.waitUntilElementContains("//a[contains(text(), 'Inputs')]", "Inputs"));
        assertTrue(playwrightAppiumSteps.waitUntilElementDoesNotContain("//a[contains(text(), 'Inputs')]", "Hello"));
        assertTrue(playwrightAppiumSteps.clickWhicheverIsDisplayedIn("hello,,Sortable Data Tables"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateBack());
        assertTrue(playwrightAppiumSteps.waitUntilOneOfIsDisplayed("DoesNotExist,,Checkboxes"));
        assertTrue(playwrightAppiumSteps.waitUntilOneOfIsDisplayed("//a[contains(text(), 'Checkboxes')],,//a[contains(text(), 'DoesNotExist')]"));
        assertTrue(playwrightAppiumSteps.waitUntilOneOfTheElementsIsEnabled("//a[contains(text(), 'Checkboxes')]"));
        assertTrue(playwrightAppiumSteps.clickWhicheverIsDisplayedIn("//a[contains(text(), 'Checkboxes')],,none"));
        assertTrue(playwrightAppiumSteps.clickIconNextTo("Fork me", "Checkboxes"));
        assertTrue(playwrightAppiumSteps.getPageUrl().contains("github.com"));
        assertTrue(playwrightAppiumSteps.pressKey("Escape"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/dropdown"));
        assertTrue(playwrightAppiumSteps.selectFromDropdown("Option 2", "Please select an option"));
        assertTrue(playwrightAppiumSteps.takeWebScreenshotWithText("Step1"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/tables"));
        assertEquals(playwrightAppiumSteps.getColumnWhere("Last Name", "First Name=Frank"), "Bach");
        assertTrue(playwrightAppiumSteps.clickColumnWhere("Last Name", "First Name=Frank"));
        assertTrue(playwrightAppiumSteps.clickInColumnWhere("Edit", "Action", "First Name=Frank"));
        //assertTrue(playwrightAppiumSteps.typeInColumnWhere("Some value", "Last Name", "First Name=John"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/upload"));
        assertTrue(playwrightAppiumSteps.uploadFile("filename.txt"));
        playwrightAppiumSteps.clickExactly("Upload");
        assertTrue(playwrightAppiumSteps.checkTextIsPresentInWebpage("File Uploaded!"));
        assertTrue(playwrightAppiumSteps.checkAccessibilityForPage(playwrightAppiumSteps.getPageTitle()));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/javascript_alerts"));
        assertTrue(playwrightAppiumSteps.clickAndAcceptAlertIfPresent("Click for JS Alert"));
        assertEquals(playwrightAppiumSteps.getTextFromToTheOf("You ", "below", "Result:"), "You successfully clicked an alert");
        assertTrue(playwrightAppiumSteps.executeJavascript("a=2"));
        assertTrue("I am a JS Alert".equals(playwrightAppiumSteps.clickAndGetAlertText("Click for JS Alert")));
        if (SetUp.isPlaywrightHeadless)
            playwrightAppiumSteps.saveAsPdfWithName("delete.pdf");
        //*/

    }

}
