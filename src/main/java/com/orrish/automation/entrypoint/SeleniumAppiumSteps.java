package com.orrish.automation.entrypoint;

import com.orrish.automation.appiumselenium.SeleniumAppiumActions;

public class SeleniumAppiumSteps {

    SeleniumAppiumActions seleniumAppiumActions;

    public SeleniumAppiumSteps() {
        seleniumAppiumActions = new SeleniumAppiumActions();
    }

    public boolean launchBrowserAndNavigateTo(String url) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("launchBrowserAndNavigateTo", url);
    }

    public boolean inBrowserNavigateTo(String url) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("inBrowserNavigateTo", url);
    }

    public boolean maximizeTheWindow() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("maximizeTheWindow");
    }

    public boolean takeWebScreenshotWithText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("takeWebScreenshotWithText", text);
    }

    public boolean clickFor(String locator, String description) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickFor", locator, description);
    }

    //If multiple locators separated by ,, provided, the first found locator will be clicked
    public boolean clickWithText(String locator, String textToClick) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickWithText", locator, textToClick);
    }

    //Use double comma separated values as single comma may be present in xpath
    public boolean clickWhicheverIsDisplayedIn(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickWhicheverIsDisplayedIn", locator);
    }

    public boolean clickRowContainingText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickRowContainingText", text);
    }

    public boolean selectCheckboxForText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("selectCheckboxForText", text);
    }

    public boolean unselectCheckboxForText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("unselectCheckboxForText", text);
    }

    public boolean waitUntilElementTextContains(String locator, String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilElementTextContains", locator, text);
    }

    public boolean waitUntilElementTextDoesNotContain(String locator, String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilElementTextDoesNotContain", locator, text);
    }

    public boolean waitUntilIsGoneFor(String locator, String description) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilIsGoneFor", locator, description);
    }

    public boolean waitUntilIsDisplayedFor(String locator, String description) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilIsDisplayedFor", locator, description);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean waitUntilOneOfTheLocatorsIsEnabled(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheLocatorsIsEnabled", locator);
    }

    public boolean enterInTextFieldFor(String inputText, String locator, String description) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("enterInTextFieldFor", inputText, locator, description);
    }

    public boolean enterInTextFieldNumber(String inputText, int locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("enterInTextFieldNumber", inputText, locator);
    }

    public boolean isTextPresentInWebpage(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("isTextPresentInWebpage", text);
    }

    public String getAlertText() {
        return seleniumAppiumActions.executeOnWebAndReturnString("getAlertText");
    }

    public boolean dismissAlertIfPresent() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("dismissAlertIfPresent");
    }

    public boolean acceptAlertIfPresent() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("acceptAlertIfPresent");
    }

    public boolean selectFromDropdown(String inputString, String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("selectFromDropdown", inputString, locator);
    }

    public String getTextFromLocator(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnString("getTextFromLocator", locator);
    }

    public boolean executeJavascript(String scriptToExecute) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("executeJavascript", scriptToExecute);
    }

    public boolean scrollTo(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("scrollTo", locator);
    }

    public boolean scrollToBottom() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("scrollToBottom");
    }

    public String getCurrentWindowId() {
        return seleniumAppiumActions.executeOnWebAndReturnString("getCurrentWindowId");
    }

    public boolean switchToWindowId(String currentWindowId) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("switchToWindowId", currentWindowId);
    }

    public boolean selectDropdownByText(String value) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("selectDropdownByText", value);
    }

    public boolean launchAppOnDevice() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("launchAppOnDevice");
    }

    public boolean takeMobileScreenshotWithText(String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("takeMobileScreenshotWithText", text);
    }

    public boolean closeAppOnDevice() {
        return seleniumAppiumActions.closeAppOnDevice();
    }

    public boolean goBackToPreviousPageInMobile() {
        return seleniumAppiumActions.goBackToPreviousPageInMobile();
    }

    public boolean pressHomeKey() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("pressHomeKey");
    }

    public boolean pressBackKey() {
        return seleniumAppiumActions.pressBackKey();
    }

    public boolean swipeOnceVertically() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("swipeOnceVertically");
    }

    public boolean tapFor(String locator, String description) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapFor", locator, description);
    }

    public boolean tapWithText(String locator, String textToClick) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapWithText", locator, textToClick);
    }

    public boolean tapWhicheverIsDisplayedIn(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapWhicheverIsDisplayedIn", locator);
    }

    public boolean inMobileWaitUntilElementTextContains(String locator, String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilElementTextContains", locator, text);
    }

    public boolean inMobileWaitUntilElementTextDoesNotContain(String locator, String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilElementTextDoesNotContain", locator, text);
    }

    public boolean inMobileWaitUntilIsGoneFor(String locator, String description) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilIsGoneFor", locator, description);
    }

    public boolean inMobileWaitUntilIsDisplayedFor(String locator, String description) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilIsDisplayedFor", locator, description);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean inMobileWaitUntilOneOfTheLocatorsIsEnabled(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilOneOfTheLocatorsIsEnabled", locator);
    }

    public boolean inMobileEnterInTextFieldFor(String inputText, String locator, String description) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileEnterInTextFieldFor", inputText, locator, description);
    }

    public String inMobileGetTextFromLocator(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnString("inMobileGetTextFromLocator", locator);
    }

}
