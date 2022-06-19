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

    public boolean tap(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tap", locator);
    }

    public boolean tapNumber(String locator, String number) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapNumber", locator, number);
    }

    public boolean tapWithPartialText(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapWithPartialText", locator);
    }

    public boolean tapWhicheverIsDisplayedIn(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("tapWhicheverIsDisplayedIn", locator);
    }

    public boolean inMobileEnterInTextField(String inputText, String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileEnterInTextField", inputText, locator);
    }

    public boolean inMobileEnterInTextFieldNumber(String inputText, String number) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileEnterInTextFieldNumber", inputText, number);
    }

    public String inMobileGetFullTextFrom(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnString("inMobileGetFullTextFrom", locator);
    }

    public boolean inMobileWaitUntilIsDisplayed(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilIsDisplayed", locator);
    }

    public boolean inMobileWaitUntilIsGone(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilIsGone", locator);
    }

    public boolean inMobileWaitUntilContains(String locator, String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilContains", locator, text);
    }

    public boolean inMobileWaitUntilDoesNotContain(String locator, String text) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilDoesNotContain", locator, text);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean inMobileWaitUntilOneOfIsDisplayed(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilOneOfIsDisplayed", locator);
    }

    //Double comma separated values, single comma may be present in xpath, hence double comma
    public boolean inMobileWaitUntilOneOfIsEnabled(String locator) {
        return seleniumAppiumActions.executeOnMobileAndReturnBoolean("inMobileWaitUntilOneOfIsEnabled", locator);
    }

}
