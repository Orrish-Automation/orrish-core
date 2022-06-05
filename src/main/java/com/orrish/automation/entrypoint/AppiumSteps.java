package com.orrish.automation.entrypoint;

import com.orrish.automation.appiumselenium.SeleniumAppiumActions;

public class AppiumSteps {

    SeleniumAppiumActions seleniumAppiumActions;

    public AppiumSteps() {
        seleniumAppiumActions = new SeleniumAppiumActions();
    }

    public boolean launchAppOnDevice() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("launchAppOnDevice");
    }

    public boolean takeMobileScreenshotWithText(String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("takeMobileScreenshotWithText", text);
    }

    public boolean quitAppOnDevice() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("quitAppOnDevice");
    }

    public boolean inMobileGoBackToPreviousPage() {
        return seleniumAppiumActions.executeOnWebAndReturnBoolean("inMobileGoBackToPreviousPage");
    }

    public boolean pressHomeKey() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("pressHomeKey");
    }

    public boolean pressBackKey() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("pressBackKey");
    }

    public boolean swipeOnceVertically() {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("swipeOnceVertically");
    }

    public boolean tap(String locator, String description) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tap", locator, description);
    }

    public boolean tapWithText(String locator, String textToClick) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapWithText", locator, textToClick);
    }

    public boolean tapWhicheverIsDisplayedIn(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapWhicheverIsDisplayedIn", locator);
    }

    public boolean inMobileWaitUntilTextIsDisplayed(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilTextIsDisplayed", locator);
    }

    public boolean inMobileWaitUntilTextIsGone(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilTextIsGone", locator);
    }

    public boolean inMobileWaitUntilElementContains(String locator, String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilElementContains", locator, text);
    }

    public boolean inMobileWaitUntilElementDoesNotContain(String locator, String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilElementDoesNotContain", locator, text);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean inMobileWaitUntilOneOfTheLocatorsIsEnabled(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilOneOfTheLocatorsIsEnabled", locator);
    }

    public boolean inMobileEnterInTextFieldIn(String inputText, String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileEnterInTextFieldIn", inputText, locator);
    }

    public String inMobileGetTextFromLocator(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnString("inMobileGetTextFromLocator", locator);
    }

}
