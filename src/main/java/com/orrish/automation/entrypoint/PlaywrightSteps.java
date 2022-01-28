package com.orrish.automation.entrypoint;

import com.orrish.automation.playwright.PlaywrightActions;

public class PlaywrightSteps {

    PlaywrightActions playwrightActions;

    public PlaywrightSteps() {
        playwrightActions = new PlaywrightActions();
    }

    public boolean launchBrowserAndNavigateTo(String url) {
        return playwrightActions.executeOnWebAndReturnBoolean("launchBrowserAndNavigateTo", url);
    }

    public boolean inBrowserNavigateTo(String url) {
        return playwrightActions.executeOnWebAndReturnBoolean("inBrowserNavigateTo", url);
    }

    public boolean maximizeTheWindow() {
        return playwrightActions.executeOnWebAndReturnBoolean("maximizeTheWindow");
    }

    public boolean takeWebScreenshotWithText(String step) {
        return playwrightActions.executeOnWebAndReturnBoolean("takeWebScreenshotWithText", step);
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
    public boolean waitUntilOneOfTheLocatorsIsDisplayed(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheLocatorsIsDisplayed", locator);
    }

    public boolean waitUntilOneOfTheLocatorsIsEnabled(String locator) {
        return playwrightActions.executeOnWebAndReturnBoolean("waitUntilOneOfTheLocatorsIsEnabled", locator);
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

    public String clickAndReturnAlertText(String locator) {
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

    public String getTextFromLocator(String locator) {
        return playwrightActions.executeOnWebAndReturnString("getTextFromLocator", locator);
    }

    public boolean executeJavascript(String scriptToExecute) {
        return playwrightActions.executeOnWebAndReturnBoolean("executeJavascript", scriptToExecute);
    }

}
