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

    public boolean quitPlaywright(){
        return playwrightActions.executeOnWebAndReturnBoolean("quitPlaywright");
    }

    public boolean takeWebScreenshotWithText(String step) {
        return playwrightActions.executeOnWebAndReturnBoolean("takeWebScreenshotWithText", step);
    }

    public boolean saveAsPdfWithName(String name) {
        return playwrightActions.executeOnWebAndReturnBoolean("saveAsPdfWithName", name);
    }

    public String getPageTitle() {
        return playwrightActions.executeOnWebAndReturnString("getPageTitle");
    }

    public String getPageUrl() {
        return playwrightActions.executeOnWebAndReturnString("getPageUrl");
    }

    public boolean checkAccessibilityForPage(String pageName) {
        return playwrightActions.executeOnWebAndReturnBoolean("checkAccessibilityForPage", pageName);
    }

    public boolean forRequestUseMockStatusAndResponse(String requestPattern, int mockHttpStatusCode, String mockResponse) {
        return SetUp.useMock ? playwrightActions.executeOnWebAndReturnBoolean("forRequestUseMockStatusAndResponse", requestPattern, mockHttpStatusCode, mockResponse) : true;
    }

    public boolean click(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("click", locator);
    }

    public boolean clickExactly(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickExactly", locator);
    }

    //TODO : Check if you can combine with click
    public boolean clickHtmlTagWithText(String locator, String textToClick) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickHtmlTagWithText", locator, textToClick);
    }

    public boolean clickToTheOf(String textToClick, String direction, String textToFind) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickToTheOf", textToClick, direction, textToFind);
    }

    public boolean clickToTheOfAndClearText(String textToClick, String direction, String textToFind) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickToTheOfAndClearText", textToClick, direction, textToFind);
    }

    public boolean clickAndClearText(String value) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickAndClearText", value);
    }

    public boolean clickIcon(String iconText) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickIcon", iconText);
    }

    public boolean clickIconNextTo(String iconText, String textToFind) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickIconNextTo", iconText, textToFind);
    }

    //Pipe separated values, single comma may be present in xpath, hence pipe.
    public boolean clickWhicheverIsDisplayedIn(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickWhicheverIsDisplayedIn", locator);
    }

    public boolean type(String inputText) {
        return playwrightActions.executeOnWebAndReturnBoolean("type", inputText);
    }

    public boolean typeIn(String inputText, String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("typeIn", inputText, locator);
    }

    public boolean typeInExactly(String inputText, String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("typeInExactly", inputText, locator);
    }

    public boolean typeInTextFieldNumber(String inputText, int whichTextField) {
        return playwrightActions.executeOnWebAndReturnBoolean("typeInTextFieldNumber", inputText, whichTextField);
    }

    public boolean selectCheckboxForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectCheckboxForText", text);
    }

    public boolean unselectCheckboxForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("unselectCheckboxForText", text);
    }

    public boolean selectRadioForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectRadioForText", text);
    }

    public boolean selectFromDropdown(String inputString, String locatorText) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectFromDropdown", inputString, locatorText);
    }

    public boolean waitUntilTextIsGone(String locatorText) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilTextIsGone", locatorText);
    }

    public boolean waitUntilTextIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilTextIsDisplayed", locator);
    }

    //TODO : Pipe separated values, single comma may be present in xpath, hence cannot be used.
    public boolean waitUntilOneOfTheTextsIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheTextsIsDisplayed", locator);
    }

    //TODO : Optimize all wait to contain both text and locator
    public boolean waitUntilElementContains(String locator, String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementContains", locator, text);
    }

    public boolean waitUntilElementDoesNotContain(String locator, String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementDoesNotContain", locator, text);
    }

    public boolean waitUntilElementIsGone(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementIsGone", locator);
    }

    public boolean waitUntilElementIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementIsDisplayed", locator);
    }

    public boolean waitUntilOneOfTheElementsIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheElementsIsDisplayed", locator);
    }

    public boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheElementsIsEnabled", locator);
    }

    public boolean isTextPresentInWebpage(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("isTextPresentInWebpage", text);
    }

    public String getTextFromElement(String locator) {
        return playwrightActions.executeOnWebAndReturnString("getTextFromElement", locator);
    }

    public String getCompleteTextFor(String text) {
        return playwrightActions.executeOnWebAndReturnString("getCompleteTextFor", text);
    }

    public boolean getTextFromToTheOf(String finalText, String direction, String initialText) {
        return playwrightActions.executeOnWebAndReturnBoolean("getTextFromToTheOf", finalText, direction, initialText);
    }

    public String clickAndGetAlertText(String locator) {
        return playwrightActions.executeOnWebAndReturnString("clickAndReturnAlertText", locator);
    }

    public boolean clickAndAcceptAlertIfPresent(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickAndAcceptAlertIfPresent", locator);
    }

    public boolean executeJavascript(String scriptToExecute) {
        return playwrightActions.executeOnWebAndReturnBoolean("executeJavascript", scriptToExecute);
    }

    public boolean executeJavascriptOnElement(String scriptToExecute, String element) {
        return playwrightActions.executeOnWebAndReturnBoolean("executeJavascriptOnElement", scriptToExecute, element);
    }

}
