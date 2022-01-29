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
