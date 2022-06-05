package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class PlaywrightIntegrationTest {

    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.playwrightHeadless(true);
        setUp.browser("chrome");
        setUp.takeScreenshotAtEachStep(false);
        setUp.reportEnabled(false);
        setUp.playwrightDefaultNavigationWaitTimeInSeconds(20);
        setUp.defaultWaitTimeInSeconds(10);
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
        assertTrue(playwrightAppiumSteps.clickIcon("Fork me"));
        assertTrue("https://github.com/saucelabs/the-internet".equals(playwrightAppiumSteps.getPageUrl()));
        assertTrue(playwrightAppiumSteps.clickIconNextTo("GitHub", "©"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateBack());
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertTrue(playwrightAppiumSteps.isTextPresentInWebpage("Inputs"));
        assertTrue("Inputs".equals(playwrightAppiumSteps.getTextFromElement("//a[contains(text(), 'Inputs')]")));
        assertTrue(playwrightAppiumSteps.clickExactly("Inputs"));
        assertTrue(playwrightAppiumSteps.waitUntilElementIsGone("//a[contains(text(), 'Inputs')]"));
        assertTrue(playwrightAppiumSteps.waitUntilTextIsGone("Hovers"));
        assertTrue(playwrightAppiumSteps.typeIn("123", "input"));
        assertTrue(playwrightAppiumSteps.typeInTextFieldNumber("456", 1));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/disappearing_elements"));
        assertTrue(playwrightAppiumSteps.clickToTheOf("About", "right-of", "Home"));
        assertTrue(playwrightAppiumSteps.getPageUrl().contains("about"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com"));
        assertTrue(playwrightAppiumSteps.refreshWebPage());
        assertTrue(playwrightAppiumSteps.waitUntilElementIsDisplayed("//a[contains(text(), 'Inputs')]"));
        assertTrue(playwrightAppiumSteps.waitUntilTextIsDisplayed("Inputs"));
        assertTrue(playwrightAppiumSteps.waitUntilElementContains("//a[contains(text(), 'Inputs')]", "Inputs"));
        assertTrue(playwrightAppiumSteps.waitUntilElementDoesNotContain("//a[contains(text(), 'Inputs')]", "Hello"));
        assertTrue(playwrightAppiumSteps.clickHtmlTagWithText("a", "Inputs"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateBack());
        assertTrue(playwrightAppiumSteps.clickWhicheverIsDisplayedIn("hello,,Sortable Data Tables"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateBack());
        assertTrue(playwrightAppiumSteps.waitUntilOneOfTheTextsIsDisplayed("DoesNotExist,,Checkboxes"));
        assertTrue(playwrightAppiumSteps.waitUntilOneOfTheElementsIsDisplayed("//a[contains(text(), 'Checkboxes')]|//a[contains(text(), 'DoesNotExist')]"));
        assertTrue(playwrightAppiumSteps.waitUntilOneOfTheElementsIsEnabled("//a[contains(text(), 'Checkboxes')]"));
        assertTrue(playwrightAppiumSteps.clickWhicheverIsDisplayedIn("//a[contains(text(), 'Checkboxes')]|none"));
        assertTrue(playwrightAppiumSteps.executeJavascript("a=2"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/dropdown"));
        assertTrue(playwrightAppiumSteps.selectFromDropdown("Option 2", "Please select an option"));
        assertTrue(playwrightAppiumSteps.takeWebScreenshotWithText("Step1"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://www.kayak.com/explore/IAD-anywhere"));
        assertTrue(playwrightAppiumSteps.selectRadioForText("City"));
        assertTrue(playwrightAppiumSteps.inBrowserNavigateTo("https://the-internet.herokuapp.com/javascript_alerts"));
        playwrightAppiumSteps.checkAccessibilityForPage(playwrightAppiumSteps.getPageTitle());
        assertTrue(playwrightAppiumSteps.clickAndAcceptAlertIfPresent("Click for JS Alert"));
        assertTrue("I am a JS Alert".equals(playwrightAppiumSteps.clickAndGetAlertText("Click for JS Alert")));
        playwrightAppiumSteps.saveAsPdfWithName("delete.pdf");
        //*/

    }

}
