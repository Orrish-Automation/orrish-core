package com.orrish.automation.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.orrish.automation.entrypoint.SetUp;

import java.util.Arrays;
import java.util.List;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;

public class ElementActions {

    protected static Playwright playwright;
    protected static Page playwrightPage;
    protected boolean isPlaywrightStepPassed = true;

    List<String> tags = Arrays.asList(new String[]{"a", "input", "img", "button", "div", "span"});

    protected Locator getFirstElementWithExactText(String text) throws Exception {
        //Cannot do exact text locator directly because some elements have newline or space in the front or in the back
        Locator locator = getAllElementsContainingText(text);
        for (int i = 0; i < locator.count(); i++) {
            if (locator.nth(i).textContent().trim().equals(text))
                return locator.nth(i);
        }
        throw new Exception("Could not find an element with text : " + text);
    }

    protected Locator getAllElementsContainingText(String text) {
        waitUntilTextIsDisplayed(text);
        String locator = "text=" + text;
        Locator locators = playwrightPage.locator(locator + " >> visible=true");
        if (locators.count() > 0)
            return locators;

        locator = "xpath=//input[contains(@placeholder,'" + text + "')]";
        Locator placeholderSelectors = playwrightPage.locator(locator + " >> visible=true");
        return placeholderSelectors;
    }

    protected boolean waitUntilElementIsDisplayed(String locatorText) {
        return playwrightPage.waitForSelector(locatorText).isVisible();
    }

    protected boolean waitUntilExactlyTextIsDisplayed(String locatorText) {
        return isElementVisible("text='" + locatorText + "'");
    }

    protected boolean waitUntilTextIsDisplayed(String locatorText) {
        return isElementVisible("text=" + locatorText);
    }

    private boolean isElementVisible(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            Locator locator = playwrightPage.locator(locatorText + " >> visible=true");
            if (locator.count() > 0)
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected Locator getIconCorrespondingTo(String iconToClick, String locatorText) {
        Locator locators = playwrightPage.locator(locatorText + " >> visible=true");
        for (int i = 0; i < locators.count(); i++) {
            Locator eachLocator = locators.nth(i);
            String attributeAria = String.valueOf(eachLocator.getAttribute("aria-label"));
            String attributeAlt = String.valueOf(eachLocator.getAttribute("alt"));
            String attributeTitle = String.valueOf(eachLocator.getAttribute("title"));
            String combinedAlt = attributeAlt + attributeAria + attributeTitle;

            if (combinedAlt.contains(iconToClick))
                return eachLocator;
            Locator parent = eachLocator.locator("xpath=..");
            String hrefProperty = String.valueOf(parent.getAttribute("href"));
            String idProperty = String.valueOf(parent.getAttribute("id"));
            String classProperty = String.valueOf(parent.getAttribute("class"));
            if (hrefProperty.contains(iconToClick) || idProperty.contains(iconToClick) || classProperty.contains(iconToClick))
                return eachLocator;
        }
        return locators;
    }

    protected Locator getRelativeTextElement(String finalTextToClick, String direction, String pivotTextToFind) {
        String finalTargetElement = tags.contains(finalTextToClick)
                ? finalTextToClick.replace(" ", "").trim().toLowerCase()
                : ":has-text(\"" + finalTextToClick + "\")";
        String locatorString = finalTargetElement + ":" + getDirection(direction) + "(:text(\"" + pivotTextToFind + "\"))";
        Locator probableLocator = playwrightPage.locator(locatorString + " >> visible=true");
        if (probableLocator.count() > 1) {
            return tags.contains(finalTextToClick) ? probableLocator.first() : probableLocator.last();
        }
        //Check if the text is in placeholder
        if (locatorString.startsWith(":has-text")) {
            locatorString = locatorString.split("has-text")[1];
            locatorString = locatorString.replaceFirst(":", "###").split("###")[1];
            locatorString = "[placeholder='" + finalTextToClick + "']:" + locatorString;
            probableLocator = playwrightPage.locator(locatorString);
        }
        return probableLocator.first();
    }

    protected boolean isLocatorPlainText(String locator) {
        boolean isTag = tags.contains(locator.trim());
        if (!isTag && locator.contains("[")) {
            isTag = tags.contains(locator.split("\\[")[0]);
        }
        boolean isXpath = locator.trim().startsWith("//");
        return !(isTag || isXpath);
    }

    protected String getDirection(String direction) {
        if (direction.toLowerCase().contains("below"))
            return "below";
        if (direction.toLowerCase().contains("above"))
            return "above";
        if (direction.toLowerCase().contains("right"))
            return "right-of";
        if (direction.toLowerCase().contains("left"))
            return "left-of";
        return "";
    }

    protected boolean waitUntilOneOfTheElements(String locator, String value) {
        if (!conditionalStep) return true;
        String[] locators = locator.split(",,");
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            for (String eachLocator : locators) {
                if (playwrightPage.locator(eachLocator).count() > 0) {
                    if (playwrightPage.isVisible(eachLocator)) {
                        if ("visible".contains(value)) {
                            return true;
                        } else if ("enabled".contains(value)) {
                            if (playwrightPage.isEnabled(eachLocator))
                                return true;
                        }
                    }
                }
            }
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilElementContains(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, true);
    }

    protected boolean waitUntilElementDoesNotContain(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, false);
    }

    private boolean waitUntilElementTextCheck(String locator, String text, boolean shouldContain) {
        if (!conditionalStep) return true;
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            if (shouldContain && playwrightPage.textContent(locator).contains(text))
                return true;
            if (!shouldContain && !playwrightPage.textContent(locator).contains(text))
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean selectUnselectCheckboxesWithText(String stringToFind, boolean shouldBeSelected) throws Exception {
        List<String> checkBoxTextsToClick = Arrays.asList(stringToFind.split(",,"));
        for (String eachCheckboxText : checkBoxTextsToClick) {
            Locator checkboxLocators = playwrightPage.locator("[type=checkbox]:left-of(:text(\"" + eachCheckboxText + "\")) , [type=checkbox]:right-of(:text(\" + value + \"))");
            Locator checkbox = null;
            if (checkboxLocators.count() == 0) {
                checkboxLocators = playwrightPage.locator("input[type=checkbox]");
                for (int i = 0; i < checkboxLocators.count(); i++) {
                    Locator eachLocator = checkboxLocators.nth(i);
                    if (eachLocator.isVisible() && eachLocator.textContent().contains(stringToFind)) {
                        checkbox = eachLocator;
                        break;
                    }
                }
            } else {
                checkbox = checkboxLocators.first();
            }
            if (checkboxLocators.count() == 0)
                throw new Exception("Could not find checkbox corresponding to : " + stringToFind);

            if ((checkbox.isChecked() && !shouldBeSelected) || (!checkbox.isChecked() && shouldBeSelected))
                checkbox.click();
        }
        return true;
    }

}
