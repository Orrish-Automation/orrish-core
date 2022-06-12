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

    public boolean saveAsPdfWithName(String name) {
        return playwrightActions.executeOnWebAndReturnBoolean("saveAsPdfWithName", name);
    }

    public String getPageTitle() {
        return playwrightActions.executeOnWebAndReturnString("getPageTitle");
    }

    public String getPageUrl() {
        return playwrightActions.executeOnWebAndReturnString("getPageUrl");
    }

    public boolean switchToNewTab() {
        return playwrightActions.executeOnWebAndReturnBoolean("switchToNewTab");
    }

    public boolean closeCurrentTab() {
        return playwrightActions.executeOnWebAndReturnBoolean("closeCurrentTab");
    }

    public boolean checkAccessibilityForPage(String pageName) {
        return playwrightActions.executeOnWebAndReturnBoolean("checkAccessibilityForPage", pageName);
    }

    public boolean forRequestUseMockStatusAndResponse(String requestPattern, String mockHttpStatusCode, String mockResponse) {
        return SetUp.useMock ? playwrightActions.executeOnWebAndReturnBoolean("forRequestUseMockStatusAndResponse", requestPattern, mockHttpStatusCode, mockResponse) : true;
    }

    public boolean uploadFile(String filePath) {
        return playwrightActions.executeOnWebAndReturnBoolean("uploadFile", filePath);
    }

    public boolean scrollTo(String locatorText) {
        return playwrightActions.executeOnWebAndReturnBoolean("scrollTo", locatorText);
    }

    public boolean hoverOn(String textToHoverOn) {
        return playwrightActions.executeOnWebAndReturnBoolean("hoverOn", textToHoverOn);
    }

    public boolean click(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("click", locator);
    }

    public boolean clickExactly(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickExactly", locator);
    }

    public boolean rightClick(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("rightClick", locator);
    }

    public boolean clickToTheOf(String textToClick, String direction, String textToFind) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickToTheOf", textToClick, direction, textToFind);
    }

    public boolean clearText() {
        return playwrightActions.executeOnWebAndReturnBoolean("clearText");
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

    public boolean pressKey(String value) {
        return playwrightActions.executeOnWebAndReturnBoolean("pressKey", value);
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

    public boolean typeInTextFieldNumber(String inputText, String whichTextField) {
        return playwrightActions.executeOnWebAndReturnBoolean("typeInTextFieldNumber", inputText, whichTextField);
    }

    //For multiple checkboxes, you can separate with double comma
    public boolean selectCheckboxForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectCheckboxForText", text);
    }

    //For multiple checkboxes, you can separate with double comma
    public boolean unselectCheckboxForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("unselectCheckboxForText", text);
    }

    public boolean selectRadioForText(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectRadioForText", text);
    }

    public boolean selectFromDropdown(String inputString, String locatorText) {
        return playwrightActions.executeOnWebAndReturnBoolean("selectFromDropdown", inputString, locatorText);
    }

    public String getColumnWhere(String columnToGet, String columnToLocate) {
        return playwrightActions.executeOnWebAndReturnString("getColumnWhere", columnToGet, columnToLocate);
    }

    public boolean clickColumnWhere(String columnToGet, String columnToLocate) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickColumnWhere", columnToGet, columnToLocate);
    }

    public boolean clickInColumnWhere(String textToClick, String columnToGet, String columnToLocate) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickInColumnWhere", textToClick, columnToGet, columnToLocate);
    }

    public boolean typeInColumnWhere(String textToType, String columnToGet, String columnToLocate) {
        return playwrightActions.executeOnWebAndReturnBoolean("typeInColumnWhere", textToType, columnToGet, columnToLocate);
    }


    public boolean waitUntilIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilIsDisplayed", locator);
    }

    public boolean waitUntilIsGone(String locatorText) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilIsGone", locatorText);
    }

    //Double comma separated values, single comma may be present in xpath, hence cannot be used.
    public boolean waitUntilOneOfIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfIsDisplayed", locator);
    }

    public boolean waitUntilElementContains(String locator, String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementContains", locator, text);
    }

    public boolean waitUntilElementDoesNotContain(String locator, String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilElementDoesNotContain", locator, text);
    }

    public boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheElementsIsEnabled", locator);
    }

    public boolean checkTextIsPresentInWebpage(String text) {
        return playwrightActions.executeOnWebAndReturnBoolean("checkTextIsPresentInWebpage", text);
    }

    public String getFullTextFor(String text) {
        return playwrightActions.executeOnWebAndReturnString("getFullTextFor", text);
    }

    public String getTextFromToTheOf(String finalText, String direction, String initialText) {
        return playwrightActions.executeOnWebAndReturnString("getTextFromToTheOf", finalText, direction, initialText);
    }

    public String clickAndGetAlertText(String locator) {
        return playwrightActions.executeOnWebAndReturnString("clickAndGetAlertText", locator);
    }

    public boolean clickAndAcceptAlertIfPresent(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("clickAndAcceptAlertIfPresent", locator);
    }

    public boolean executeJavascript(String scriptToExecute) {
        return playwrightActions.executeOnWebAndReturnBoolean("executeJavascript", scriptToExecute);
    }

}
