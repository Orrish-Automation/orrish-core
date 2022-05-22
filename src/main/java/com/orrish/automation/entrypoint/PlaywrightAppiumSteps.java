package com.orrish.automation.entrypoint;

import com.orrish.automation.playwright.PlaywrightActions;

public class PlaywrightAppiumSteps extends AppiumSteps {

    PlaywrightActions playwrightActions;

    public PlaywrightAppiumSteps() {
        playwrightActions = new PlaywrightActions();
    }

    public boolean launchBrowserAndNavigateTo(String url) {
        return playwrightActions.executeOnWebAndReturnBoolean("launchBrowserAndNavigateTo", url);
    }

    public boolean inBrowserNavigateTo(String url) {
        return playwrightActions.executeOnWebAndReturnBoolean("inBrowserNavigateTo", url);
    }

    public boolean inBrowserNavigateBack() {
        return playwrightActions.executeOnWebAndReturnBoolean("inBrowserNavigateBack");
    }

    public boolean maximizeTheWindow() {
        return playwrightActions.executeOnWebAndReturnBoolean("maximizeTheWindow");
    }

    public boolean refreshWebPage() {
        return playwrightActions.executeOnWebAndReturnBoolean("refreshWebPage");
    }

    public boolean takeWebScreenshotWithText(String step) {
        return playwrightActions.executeOnWebAndReturnBoolean("takeWebScreenshotWithText", step);
    }

    public String getPageTitle() {
        return playwrightActions.executeOnWebAndReturnString("getPageTitle");
    }

    public boolean checkAccessibilityForPage(String pageName) {
        return playwrightActions.executeOnWebAndReturnBoolean("checkAccessibilityForPage", pageName);
    }

    public boolean forRequestUseMockStatusAndResponse(String requestPattern, int mockHttpStatusCode, String mockResponse) {
        return SetUp.useMock ? playwrightActions.executeOnWebAndReturnBoolean("forRequestUseMockStatusAndResponse", requestPattern, mockHttpStatusCode, mockResponse) : true;
    }

    //
    //Click steps
    //
    public boolean clickFor(String locator, String locatorName) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickFor", locator);
    }

    public boolean clickWithText(String locator, String textToClick) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickWithText", locator, textToClick);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean clickWhicheverIsDisplayedIn(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickWhicheverIsDisplayedIn", locator);
    }

    public boolean clickRowContainingText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickRowContainingText", text);
    }

    public boolean selectCheckboxForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectCheckboxForText", text);
    }

    public boolean unselectCheckboxForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("unselectCheckboxForText", text);
    }

    public boolean waitUntilElementTextContains(String locator, String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementTextContains", locator, text);
    }

    public boolean waitUntilElementTextDoesNotContain(String locator, String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementTextDoesNotContain", locator, text);
    }

    public boolean waitUntilIsGoneFor(String locator, String description) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilIsGoneFor", locator, description);
    }

    public boolean waitUntilIsDisplayedFor(String locator, String description) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilIsDisplayedFor", locator, description);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean waitUntilOneOfTheElementsIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheElementsIsDisplayed", locator);
    }

    public boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheElementsIsEnabled", locator);
    }

    public boolean enterInTextFieldFor(String inputText, String locator, String description) {
        return playwrightActions.executeOnWebAndReturnBoolean("enterInTextFieldFor", inputText, locator, description);
    }

    public boolean enterInTextFieldNumber(String inputText, int whichTextField) {
        return playwrightActions.executeOnWebAndReturnBoolean("enterInTextFieldNumber", inputText, whichTextField);
    }

    public boolean isTextPresentInWebpage(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("isTextPresentInWebpage", text);
    }

    public String clickAndGetAlertText(String locator) {
        return playwrightActions.executeOnWebAndReturnString("clickAndReturnAlertText", locator);
    }

    public boolean dismissAlertIfPresent() {
        return playwrightActions.executeOnWebAndReturnBoolean("dismissAlertIfPresent");
    }

    public boolean clickAndAcceptAlertIfPresent(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickAndAcceptAlertIfPresent", locator);
    }

    public boolean selectFromDropdown(String inputString, String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectFromDropdown", inputString, locator);
    }

    public String getTextFromElement(String locator) {
        return playwrightActions.executeOnWebAndReturnString("getTextFromElement", locator);
    }

    public boolean executeJavascript(String scriptToExecute) {
        return playwrightActions.executeOnWebAndReturnBoolean("executeJavascript", scriptToExecute);
    }

    public boolean executeJavascriptOnElement(String scriptToExecute, String element) {
        return playwrightActions.executeOnWebAndReturnBoolean("executeJavascriptOnElement", scriptToExecute, element);
    }

}
