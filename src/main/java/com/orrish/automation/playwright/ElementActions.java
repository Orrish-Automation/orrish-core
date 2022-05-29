package com.orrish.automation.playwright;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.orrish.automation.entrypoint.SetUp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;

public class ElementActions {

    protected static Playwright playwright;
    protected static Page playwrightPage;
    protected boolean isPlaywrightStepPassed = true;

    protected ElementHandle getElementWithExactText(String text) throws Exception {
        try {
            return getElementsWithText(text, true).get(0);
        } catch (Exception ex) {
            waitUntilExactlyTextIsDisplayed(text);
            try {
                return getElementsWithText(text, true).get(0);
            } catch (Exception ex1) {
                throw new Exception("Could not find an element with the given criteria.");
            }
        }
    }

    protected boolean waitUntilElementIsDisplayed(String locatorText) {
        return playwrightPage.waitForSelector(locatorText).isVisible();
    }

    protected boolean waitUntilExactlyTextIsDisplayed(String locatorText) {
        return isAnyElementVisible("text='" + locatorText + "'");
    }

    protected boolean waitUntilTextIsDisplayed(String locatorText) {
        return isAnyElementVisible("text=" + locatorText);
    }

    private boolean isAnyElementVisible(String locatorText) {
        List<ElementHandle> elementHandleList = playwrightPage.querySelectorAll(locatorText);
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            for (ElementHandle elementHandle : elementHandleList)
                if (elementHandle.isVisible())
                    return true;
            waitSeconds(1);
            elementHandleList = playwrightPage.querySelectorAll(locatorText);
        }
        return false;
    }

    protected List<ElementHandle> getElementsWithText(String text, boolean isExact) {
        //radioHandles = playwrightPage.querySelectorAll("xpath=//label[contains(.,'" + text + "')]");
        //Locator probableLocator = playwrightPage.locator("text=" + text);
        //String locator = isExact ? "text='" + text + "'" : "xpath=//*[contains(.,'" + text + "')]";

        //String locator = "xpath=//*[contains(.,'" + text + "')]";
        String locator = "text=" + text;
        List<ElementHandle> selectors = getElementHandles(text, isExact, locator);
        if (selectors.size() > 0)
            return selectors;

        //String xpath = "xpath=//svg[contains(@aria-label,'" + eachList.toLowerCase() + "')]";
        //locator = isExact ? "[placeholder='" + text + "']" : "xpath=//input[contains(@placeholder,'" + text + "')]";
        locator = "xpath=//input[contains(@placeholder,'" + text + "')]";
        List<ElementHandle> placeholderSelectors = getElementHandles(text, isExact, locator);
        return placeholderSelectors;
    }

    private List<ElementHandle> getElementHandles(String text, boolean isExact, String locator) {
        List<ElementHandle> selectors = playwrightPage.querySelectorAll(locator);
        selectors.removeIf(e -> !e.isVisible());
        if (selectors.size() != 0) {
            if (!isExact)
                return selectors;
            selectors.removeIf(e -> !e.textContent().trim().equalsIgnoreCase(text.trim()));
            if (selectors.size() != 0)
                return selectors;
        }
        return new ArrayList<>();
    }

    protected ElementHandle getContainingText(String text) throws Exception {
        try {
            return getElementsWithText(text, false).get(0);
        } catch (Exception ex) {
            waitUntilTextIsDisplayed(text);
            try {
                return getElementsWithText(text, false).get(0);
            } catch (Exception ex1) {
                throw new Exception("Could not find an element with the given criteria.");
            }
        }
    }

    protected List<ElementHandle> getIconsCorrespondingTo(String textToClick, String locator) {
        List<ElementHandle> elementHandles = new ArrayList<>();
        List<String> list = Arrays.asList(textToClick.split(" "));
        for (String eachList : list) {
            //String xpath = "xpath=//svg[contains(@aria-label,'" + eachList.toLowerCase() + "')]";
            if (elementHandles.size() == 0)
                elementHandles = playwrightPage.querySelectorAll(locator);
            elementHandles.removeIf(e -> !e.isVisible());
            elementHandles.removeIf(e -> {
                String attributeAria = e.getAttribute("aria-label");
                String attributeAlt = e.getAttribute("alt");
                String attributeTitle = e.getAttribute("title");
                if (attributeAlt == null && attributeAria == null && attributeTitle == null) return true;
                String combinedAlt = attributeAlt + attributeAria + attributeTitle;
                return !combinedAlt.toLowerCase().contains(eachList.toLowerCase());
            });
        }
        return elementHandles;
    }


    protected Locator getRelativeTextElement(String textToClick, String direction, String textToFind) {
        //:has-text("About"):right-of(:text("Home"))
        String locatorString = getTypeOfTargetElement(textToClick) + ":" + getDirection(direction) + "(:text(\"" + textToFind + "\"))";
        Locator probableLocator = playwrightPage.locator(locatorString);
        if (probableLocator.count() > 1) {
            probableLocator = (isTagElement(textToClick)) ? probableLocator.first() : probableLocator.last();
        }
        //Check if the text is in placeholder
        if (probableLocator.count() == 0 && locatorString.startsWith(":has-text")) {
            locatorString = locatorString.split("has-text")[1];
            locatorString = locatorString.replaceFirst(":", "###").split("###")[1];
            locatorString = "[placeholder='" + textToClick + "']:" + locatorString;
            probableLocator = playwrightPage.locator(locatorString);
        }
        return probableLocator;
    }

    private String getTypeOfTargetElement(String textToClick) {
        return isTagElement(textToClick)
                ? textToClick.replace(" ", "").trim().toLowerCase()
                : ":has-text(\"" + textToClick + "\")";
    }

    protected boolean isLocatorPlainText(String locator) {
        List<String> tags = Arrays.asList(new String[]{"a", "input", "button", "img", "div"});
        boolean isTag = tags.contains(locator.trim());
        if (!isTag && locator.contains("[")) {
            isTag = tags.contains(locator.split("\\[")[0]);
        }
        boolean isXpath = locator.trim().startsWith("//");
        return !(isTag || isXpath);
    }

    private boolean isTagElement(String elementText) {
        List<String> tags = Arrays.asList(new String[]{"link", "input", "textbox", "image", "img", "icon"});
        String modifiedText = elementText.replace(" ", "").trim().toLowerCase();
        return tags.contains(modifiedText);
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

    protected boolean selectUnselectCheckboxesWithText(String value, boolean shouldBeSelected) throws Exception {
        List<ElementHandle> checkboxHandles = playwrightPage.querySelectorAll("[type=checkbox]:left-of(:text(\"" + value + "\")) , [type=checkbox]:right-of(:text(\" + value + \"))");
        ElementHandle checkbox = null;
        if (checkboxHandles.size() == 0) {
            checkboxHandles = playwrightPage.querySelectorAll("input[type=checkbox]");
            for (ElementHandle elementHandle : checkboxHandles) {
                if (elementHandle.isVisible() && elementHandle.textContent().contains(value)) {
                    checkbox = elementHandle;
                    break;
                }
            }
        } else {
            checkbox = checkboxHandles.get(0);
        }
        if (checkboxHandles.size() == 0)
            throw new Exception("Could not find checkbox corresponding to " + value);

        if ((checkbox.isChecked() && !shouldBeSelected) || (!checkbox.isChecked() && shouldBeSelected))
            checkbox.click();
        return true;
    }

}
