package com.orrish.automation.entrypoint;

public class SeleniumAppiumSteps extends AppiumSteps {

    public boolean launchBrowserAndNavigateTo(String url) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("launchBrowserAndNavigateTo", url);
    }

    public boolean inBrowserNavigateTo(String url) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("inBrowserNavigateTo", url);
    }

    public boolean inBrowserNavigateBack() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("inBrowserNavigateBack");
    }

    public boolean refreshWebPage() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("refreshWebPage");
    }

    public boolean maximizeTheWindow() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("maximizeTheWindow");
    }

    public boolean takeWebScreenshotWithText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("takeWebScreenshotWithText", text);
    }

    public boolean closeBrowser() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("closeBrowser");
    }

    public boolean quitBrowser() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("quitBrowser");
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

    public boolean isElementDisplayedFor(String locator, String description) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("isElementDisplayedFor", locator, description);
    }

    public boolean isElementEnabledFor(String locator, String description) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("isElementEnabledFor", locator, description);
    }

    public boolean isElementSelectedFor(String locator, String description) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("isElementSelectedFor", locator, description);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean waitUntilOneOfTheElementsIsDisplayed(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheElementsIsDisplayed", locator);
    }

    public boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheElementsIsEnabled", locator);
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

    public boolean selectDropdownByText(String value) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("selectDropdownByText", value);
    }

    public String getTextFromElement(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnString("getTextFromElement", locator);
    }

    public boolean executeJavascript(String scriptToExecute) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("executeJavascript", scriptToExecute);
    }

    public boolean executeJavascriptOnElement(String scriptToExecute, String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("executeJavascriptOnElement", scriptToExecute, locator);
    }

    public String getCurrentWindowId() {
        return seleniumAppiumActions.executeOnWebAndReturnString("getCurrentWindowId");
    }

    public boolean switchToWindowId(String currentWindowId) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("switchToWindowId", currentWindowId);
    }

}
