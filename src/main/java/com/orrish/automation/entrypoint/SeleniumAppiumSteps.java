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

    public String getPageUrl() {
        return seleniumAppiumActions.executeOnWebAndReturnString("getPageUrl");
    }

    public String getPageTitle() {
        return seleniumAppiumActions.executeOnWebAndReturnString("getPageTitle");
    }

    public boolean checkAccessibilityForPage(String pageName) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("checkAccessibilityForPage", pageName);
    }

    public boolean switchToNewTab() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("switchToNewTab");
    }

    public boolean closeCurrentTab() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("closeCurrentTab");
    }

    public boolean click(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("click", locator);
    }

    public boolean clickNumber(String locator, String number) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickNumber", locator, number);
    }

    public boolean clickWithPartialText(String textToClick) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickWithPartialText", textToClick);
    }

    public boolean clickIcon(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickIcon", locator);
    }

    public boolean clickIconNextTo(String locator, String pivotElement) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickIconNextTo", locator, pivotElement);
    }

    public boolean clickToTheOf(String locator, String direction, String pivotElement) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickToTheOf", locator, direction, pivotElement);
    }

    public boolean clickWhicheverIsDisplayedIn(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickWhicheverIsDisplayedIn", locator);
    }

    public boolean clickColumnWhere(String text, String where) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickColumnWhere", text, where);
    }

    public String getColumnWhere(String text, String where) {
        return seleniumAppiumActions.executeOnWebAndReturnString("getColumnWhere", text, where);
    }

    public boolean clickInColumnWhere(String textToClick, String column, String where) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clickInColumnWhere", textToClick, column, where);
    }

    public boolean rightClick(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("rightClick", locator);
    }

    public boolean pressKey(String keyToPress) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("pressKey", keyToPress);
    }

    public boolean hoverOn(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("hoverOn", locator);
    }

    public boolean scrollTo(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("scrollTo", locator);
    }

    public boolean clearText() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("clearText");
    }

    public boolean selectRadioForText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("selectRadioForText", text);
    }

    public boolean selectCheckboxForText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("selectCheckboxForText", text);
    }

    public boolean unselectCheckboxForText(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("unselectCheckboxForText", text);
    }

    public boolean typeIn(String inputText, String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("typeIn", inputText, locator);
    }

    public boolean typeWithPartialText(String inputText, String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("typeWithPartialText", inputText, locator);
    }

    public boolean typeInNumber(String inputText, String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("typeInNumber", inputText, locator);
    }

    public boolean waitUntilContains(String locator, String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilContains", locator, text);
    }

    public boolean waitUntilDoesNotContain(String locator, String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilDoesNotContain", locator, text);
    }

    public boolean waitUntilIsGone(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilIsGone", locator);
    }

    public boolean waitUntilIsDisplayed(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilIsDisplayed", locator);
    }

    public boolean isDisplayed(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("isDisplayed", locator);
    }

    public boolean isEnabled(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("isEnabled", locator);
    }

    public boolean isSelected(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("isSelected",locator);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean waitUntilOneOfIsDisplayed(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilOneOfIsDisplayed", locator);
    }

    public boolean waitUntilOneOfIsEnabled(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("waitUntilOneOfIsEnabled", locator);
    }

    public boolean checkTextIsPresentInWebpage(String text) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("checkTextIsPresentInWebpage", text);
    }

    public boolean uploadFile(String filePath) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("uploadFile", filePath);
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

    public String getFullTextFor(String locator) {
        return seleniumAppiumActions.executeOnWebAndReturnString("getFullTextFor", locator);
    }

    public String getTextFromToTheOf(String textToClick, String direction, String textToFind) {
        return seleniumAppiumActions.executeOnWebAndReturnString("getTextFromToTheOf", textToClick, direction, textToFind);
    }

    public boolean executeJavascript(String scriptToExecute) {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("executeJavascript", scriptToExecute);
    }

}
