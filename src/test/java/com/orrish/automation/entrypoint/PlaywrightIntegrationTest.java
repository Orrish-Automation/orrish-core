package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class PlaywrightIntegrationTest {

    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.playwrightHeadless(true);
        setUp.browser("chrome");
        setUp.takeScreenshotAtEachStep(false);
        setUp.reportEnabled(false);
        setUp.useMock(true);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    @Test
    public void playwrightTest() {
        PlaywrightAppiumSteps playwrightAppiumSteps = new PlaywrightAppiumSteps();
        assertTrue(playwrightAppiumSteps.launchBrowserAndNavigateTo("https://jsonplaceholder.typicode.com/users/1"));
        playwrightAppiumSteps.forRequestUseMockStatusAndResponse("**/users/2", 200, "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Orrish Automation\",\n" +
                "  \"username\": \"Orrish\",\n" +
                "  \"email\": \"Sincere@april.biz\",\n" +
                "  \"phone\": \"1-770-736-8031 x56442\",\n" +
                "  \"website\": \"hildegard.org\"\n" +
                "}"
        );
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://jsonplaceholder.typicode.com/users/2"));
        assertTrue(playwrightAppiumSteps.isTextPresentInWebpage("Orrish"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertFalse(playwrightAppiumSteps.checkAccessibilityForPage(playwrightAppiumSteps.getPageTitle()));
        assertTrue(playwrightAppiumSteps.refreshWebPage());
        assertTrue(playwrightAppiumSteps.isTextPresentInWebpage("Inputs"));
        assertEquals("Inputs", playwrightAppiumSteps.getTextFromElement("//a[contains(text(), 'Inputs')]"));
        assertTrue(playwrightAppiumSteps.clickFor("//a[contains(text(), 'Inputs')]", ""));
        assertTrue(playwrightAppiumSteps.waitUntilIsGoneFor("//a[contains(text(), 'Inputs')]", ""));
        assertTrue(playwrightAppiumSteps.enterInTextFieldFor("123", "input", ""));
        assertTrue(playwrightAppiumSteps.enterInTextFieldNumber("456", 1));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertTrue(playwrightAppiumSteps.waitUntilIsDisplayedFor("//a[contains(text(), 'Inputs')]", ""));
        assertTrue(playwrightAppiumSteps.waitUntilElementTextContains("//a[contains(text(), 'Inputs')]", "Inputs"));
        assertTrue(playwrightAppiumSteps.waitUntilElementTextDoesNotContain("//a[contains(text(), 'Inputs')]", "Hello"));
        assertTrue(playwrightAppiumSteps.clickWithText("a", "Inputs"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateBack());
        assertTrue(playwrightAppiumSteps.clickWhicheverIsDisplayedIn("hello,,//a[contains(text(), 'Sortable Data Tables')]"));
        assertTrue(playwrightAppiumSteps.clickRowContainingText("Frank"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateBack());
        assertTrue(playwrightAppiumSteps.waitUntilOneOfTheElementsIsDisplayed("//a[contains(text(), 'Checkboxes')]"));
        assertTrue(playwrightAppiumSteps.waitUntilOneOfTheElementsIsEnabled("//a[contains(text(), 'Checkboxes')]"));
        assertTrue(playwrightAppiumSteps.clickWhicheverIsDisplayedIn("//a[contains(text(), 'Checkboxes')],,none"));
        assertTrue(playwrightAppiumSteps.executeJavascript("a=2"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/dropdown"));
        assertTrue(playwrightAppiumSteps.selectFromDropdown("Option 2", "#dropdown"));
        assertTrue(playwrightAppiumSteps.takeWebScreenshotWithText("Step1"));

    }

}
