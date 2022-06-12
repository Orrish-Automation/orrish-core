package com.orrish.automation.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.SelectOption;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.SetUp.defaultWaitTime;

public class ElementActions {

    protected static Playwright playwright;
    protected static Page playwrightPage;
    protected boolean isPlaywrightStepPassed = true;
    protected Locator lastLocatorInteractedWith;

    protected List<String> htmlTags = Arrays.asList(new String[]{"a", "input", "img", "button", "div", "span", "p"});

    protected boolean isTextPresentInWebpage(String text) {
        Locator locator = getElementWithinDefaultTime("text=" + text);
        for (int i = 0; i < locator.count(); i++) {
            if (locator.nth(i).textContent().contains(text))
                return true;
        }
        return false;
    }

    protected boolean hoverOn(String text) throws Exception {
        getFirstElementWithExactText(text).hover();
        return true;
    }

    protected boolean scrollTo(String locatorText) {
        waitUntilIsDisplayed(locatorText);
        getAllElementsContainingText(locatorText).first().scrollIntoViewIfNeeded();
        return true;
    }

    protected boolean click(String text, boolean isExact) throws Exception {
        return clickWhichButton(text, isExact, false);
    }

    protected boolean rightClick(String text) throws Exception {
        return clickWhichButton(text, false, true);
    }

    protected boolean clearText() {
        lastLocatorInteractedWith.fill("");
        return true;
    }

    protected boolean clickIcon(String textToClick) throws Exception {

        Locator icons = getIconCorrespondingTo(textToClick, "svg");
        icons = (icons.count() == 0) ? getIconCorrespondingTo(textToClick, "img") : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(textToClick, "button") : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(textToClick, "a") : icons;

        if (icons == null) {
            throw new Exception("Could not find icon with the given criteria.");
        }
        icons.first().click();
        return true;
    }

    protected boolean clickColumnWhere(String columnToGet, String valueToFind) throws Exception {
        getTargetTableCell(columnToGet, valueToFind).click();
        return true;
    }

    protected boolean clickInColumnWhere(String textToClick, String columnToGet, String valueToFind) throws Exception {
        Locator locator = getTargetTableCell(columnToGet, valueToFind);
        String locatorText = isPlaywrightLocator(textToClick) ? textToClick : "text=" + textToClick;
        locator.locator(locatorText).click();
        return true;
    }

    protected boolean typeInColumnWhere(String textToType, String columnToGet, String valueToFind) throws Exception {
        Locator locator = getTargetTableCell(columnToGet, valueToFind);
        locator.locator("input").type(textToType);
        return true;
    }

    protected String getColumnWhere(String columnToGet, String valueToFind) throws Exception {
        return getTargetTableCell(columnToGet, valueToFind).textContent();
    }

    protected Locator getRelativeImage(String iconTextToClick, String direction, String textToFind) {
        //svg:right-of(:text("Home"))
        String relativeLocatorString = ":" + getDirection(direction) + "(:text(\"" + textToFind + "\"))";
        Locator icons = getIconCorrespondingTo(iconTextToClick, "svg" + relativeLocatorString);
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "img" + relativeLocatorString) : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "button" + relativeLocatorString) : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "a" + relativeLocatorString) : icons;
        return icons;
    }

    protected boolean clickToTheOf(String textToClick, String direction, String textToFind) {
        lastLocatorInteractedWith = getRelativeTextElement(textToClick, direction, textToFind);
        lastLocatorInteractedWith.click();
        return true;
    }

    protected String getTextFromToTheOf(String textToClick, String direction, String textToFind) {
        return getRelativeTextElement(textToClick, direction, textToFind).textContent();
    }

    protected boolean clickIconNextTo(String iconToClick, String textToFind) throws Exception {
        Locator icons = getEmptyLocator();
        for (int i = 0; i < defaultWaitTime; i++) {
            icons = getRelativeImage(iconToClick, "right", textToFind);
            icons = (icons.count() == 0) ? getRelativeImage(iconToClick, "left", textToFind) : icons;
            if (icons.count() > 0)
                break;
            waitSeconds(1);
        }
        if (icons.count() == 0) {
            throw new Exception("Could not find icon with the given criteria.");
        }
        icons.first().click();
        return true;
    }

    protected boolean clickWhicheverIsDisplayedIn(String locator) throws Exception {
        //page.locator("button:has-text(\"Log in\"), button:has-text(\"Sign in\")").click();
        String[] eachParts = locator.split(",,");
        for (String eachPart : eachParts) {
            try {
                eachPart = isPlaywrightLocator(eachPart) ? eachPart : "text=" + eachPart;
                if (isElementDisplayed(eachPart)) {
                    getElementWithinDefaultTime(eachPart).first().click();
                    return true;
                }
            } catch (Exception ex) {
            }
        }
        throw new Exception("Could not find any element with the selected criteria. " + locator);
    }

    protected boolean type(String value) {
        lastLocatorInteractedWith.type(value);
        return true;
    }

    protected boolean pressKey(String value) {
        playwrightPage.keyboard().press(value);
        return true;
    }

    protected boolean typeIn(String value, String locatorString) {
        boolean locatorFound = false;
        List<String> inputElements = Arrays.asList(new String[]{"input", "textarea", "contenteditable"});
        if (inputElements.contains(locatorString)) {
            Locator locator = getElementWithinDefaultTime(locatorString);
            locator.first().fill(value);
            return true;
        }
        for (int i = 0; i < SetUp.defaultWaitTime && !locatorFound; i++) {
            Locator locators = getElementWithinDefaultTime("text=" + locatorString);
            locators = locators.count() == 0 ? getElementWithinDefaultTime("xpath=//input[contains(@placeholder,'" + locatorString + "')]") : locators;
            if (locators.count() > 0) {
                locators.first().fill(value);
                return true;
            }
            waitSeconds(1);
        }
        Locator locators = getAllElementsContainingText(locatorString);
        for (int i = 0; i < locators.count(); i++) {
            Locator eachLocator = locators.nth(i);
            String tagName = eachLocator.elementHandle().getProperty("tagName").jsonValue().toString();
            if (tagName.equalsIgnoreCase("label") || tagName.equalsIgnoreCase("input")) {
                eachLocator.fill(value);
                return true;
            }
        }
        return false;
    }

    protected boolean typeInExactly(String value, String locatorString) throws Exception {
        getElementWithinDefaultTime("text='" + locatorString + "'");
        List<String> inputElements = Arrays.asList(new String[]{"input", "textarea", "contenteditable"});
        if (inputElements.contains(locatorString)) {
            Locator locator = getElementWithinDefaultTime(locatorString);
            locator.first().fill(value);
        } else {
            Locator locators = getAllElementsContainingText(locatorString);
            for (int i = 0; i < locators.count(); i++) {
                Locator eachLocator = locators.nth(i);
                eachLocator.fill(value);
                return true;
            }
        }
        throw new Exception("Could not find the locator : " + locatorString);
    }

    protected boolean typeInTextFieldNumber(String text, int whichField) {
        playwrightPage.waitForSelector("input");
        Locator locator = playwrightPage.locator("input");
        locator.nth(whichField - 1).fill(text); //Convert to zero based index.
        return true;
    }

    protected boolean selectFromDropdown(String value, String locatorText) {
        Locator locator = playwrightPage.locator("text=" + locatorText);
        locator = locator.locator("xpath=..");
        locator.selectOption(new SelectOption().setLabel(value));
        return true;
    }

    protected String clickAndReturnAlertText(String locatorText) throws Exception {
        final String[] message = new String[1];
        playwrightPage.onDialog(dialog -> {
            message[0] = dialog.message();
        });
        Locator locator = getFirstElementWithExactText(locatorText);
        locator.click();
        return message[0];
    }

    protected boolean clickAndAcceptAlertIfPresent(String locatorText) {
        playwrightPage.onDialog(dialog -> {
            ReportUtility.reportInfo("Alert with text \"" + dialog.message() + "\" on clicking " + locatorText + " is accepted.");
            dialog.accept();
        });
        Locator locator = getAllElementsContainingText(locatorText);
        locator.first().click();
        return true;
    }

    protected boolean waitUntilIsGone(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            locatorText = isPlaywrightLocator(locatorText) ? locatorText : "text=" + locatorText;
            if (!isElementDisplayed(locatorText))
                return true;
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilOneOfIsDisplayed(String locatorString) {
        List<String> list = Arrays.asList(locatorString.split(",,"));
        for (int i = 0; i < defaultWaitTime; i++) {
            for (String eachItem : list) {
                locatorString = isPlaywrightLocator(eachItem) ? eachItem : "text=" + eachItem;
                if (isElementDisplayed(locatorString))
                    return true;
            }
            waitSeconds(1);
        }
        return false;
    }

    protected boolean waitUntilOneOfTheElementsIsEnabled(String locator) {
        return waitUntilOneOfTheElements(locator, "enabled");
    }

    protected boolean waitUntilIsDisplayed(String locatorText) {
        locatorText = isPlaywrightLocator(locatorText) ? locatorText : "text=" + locatorText;
        return getElementWithinDefaultTime(locatorText).count() > 0;
    }

    private boolean isElementDisplayed(String locatorText) {
        Locator locator = playwrightPage.locator(locatorText + " >> visible=true");
        locator = locator.count() == 0 ? getElementFromFrameIfPresent(locatorText + " >> visible=true") : locator;
        return locator.count() > 0;
    }

    private Locator getElementWithinDefaultTime(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            Locator locator = playwrightPage.locator(locatorText + " >> visible=true");
            locator = locator.count() == 0 ? getElementFromFrameIfPresent(locatorText + " >> visible=true") : locator;
            if (locator.count() > 0)
                return locator;
            waitSeconds(1);
        }
        return getEmptyLocator();
    }

    private Locator getElementFromFrameIfPresent(String locatorText) {
        List<Frame> frames = playwrightPage.frames();
        for (Frame frame : frames) {
            Locator locator = frame.locator(locatorText);
            if (locator.count() > 0)
                return locator;
        }
        return getEmptyLocator();
    }

    private Locator getIconCorrespondingTo(String iconToClick, String locatorText) {
        if (!isElementDisplayed(locatorText))
            return getEmptyLocator();
        Locator locators = getElementWithinDefaultTime(locatorText);
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
        //Cannot return original locators as it may contain values.
        return getEmptyLocator();
    }

    private Locator getRelativeTextElement(String finalTextToClick, String direction, String pivotTextToFind) {
        String finalTargetElement = (isPlaywrightLocator(finalTextToClick) || finalTextToClick.trim().length() == 0)
                ? finalTextToClick
                : ":has-text(\"" + finalTextToClick + "\")";
        String locatorString = finalTargetElement + ":" + getDirection(direction) + "(:text(\"" + pivotTextToFind + "\"))";
        Locator probableLocator = getElementWithinDefaultTime(locatorString);
        if (probableLocator.count() > 0) {
            return probableLocator.first();
        }
        //Check if the text is in placeholder
        if (probableLocator.count() == 0 && locatorString.startsWith(":has-text")) {
            locatorString = locatorString.split("has-text")[1];
            locatorString = locatorString.replaceFirst(":", "###").split("###")[1];
            locatorString = "[placeholder='" + finalTextToClick + "']:" + locatorString;
            probableLocator = getElementWithinDefaultTime(locatorString);
        }
        return probableLocator.first();
    }

    protected boolean isPlaywrightLocator(String locatorString) {
        boolean isTag = htmlTags.contains(locatorString.trim());
        if (!isTag && locatorString.contains("[")) {
            isTag = htmlTags.contains(locatorString.split("\\[")[0]);
        }
        boolean isId = locatorString.startsWith("#");
        boolean isClass = locatorString.startsWith(".");
        boolean isContainingText = locatorString.contains(":has-text") || locatorString.contains(":text");
        boolean isXpath = locatorString.trim().startsWith("//");
        return isTag || isId || isClass || isContainingText || isXpath;
    }

    protected boolean waitUntilElementContains(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, true);
    }

    protected boolean waitUntilElementDoesNotContain(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, false);
    }

    protected boolean selectRadioForText(String text) throws Exception {

        //Locator radioHandles = getElementWithinDefaultTime("[type=radio]:left-of(:text(\"" + text + "\"))");
        //radioHandles = (radioHandles.count() == 0) ? getElementWithinDefaultTime("xpath=//label[contains(.,'" + text + "')]") : radioHandles;
        Locator radioHandles = getTargetInputElement(text, "radio");
        if (radioHandles.count() == 0)
            throw new Exception("Could not find a radio button with text " + text);
        try {
            radioHandles.first().click();
        } catch (TimeoutError ex) {
            radioHandles.first().click(new Locator.ClickOptions().setForce(true));
        }
        return true;
    }

    protected boolean selectUnselectcheckboxeswithtext(String stringToFind, boolean shouldBeSelected) throws Exception {
        List<String> textsToActOn = Arrays.asList(stringToFind.split(",,"));
        Locator finalLocatorToClick = null;
        for (String eachText : textsToActOn) {
            finalLocatorToClick = getTargetInputElement(eachText, "checkbox");
            if (finalLocatorToClick == null || finalLocatorToClick.count() == 0)
                throw new Exception("Could not find checkbox corresponding to : " + stringToFind);

            if ((finalLocatorToClick.isChecked() && !shouldBeSelected) || (!finalLocatorToClick.isChecked() && shouldBeSelected))
                finalLocatorToClick.click();
        }
        return true;
    }

    protected boolean uploadFile(String filePath) throws Exception {
        Locator fileButtons = getElementWithinDefaultTime("input[type='file']");
        if (fileButtons.count() == 0)
            throw new Exception("Could not find any button of type file.");
        fileButtons.first().setInputFiles(Paths.get(filePath));
        return true;
    }

    private String getDirection(String direction) {
        if (direction.toLowerCase().contains("below"))
            return "below";
        if (direction.toLowerCase().contains("above"))
            return "above";
        if (direction.toLowerCase().contains("right"))
            return "right-of";
        if (direction.toLowerCase().contains("left"))
            return "left-of";
        return "near";
    }

    private boolean waitUntilOneOfTheElements(String locatorString, String value) {
        if (!conditionalStep) return true;
        String[] locatorsStrings = locatorString.split(",,");
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            for (String eachLocator : locatorsStrings) {
                if (isElementDisplayed(eachLocator)) {
                    if ("visible".contains(value)) {
                        return true;
                    }
                    if ("enabled".contains(value)) {
                        if (getElementWithinDefaultTime(eachLocator).isEnabled())
                            return true;
                    }
                }
            }
            waitSeconds(1);
        }
        return false;
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

    private Locator getTargetInputElement(String stringToFind, String typeOfInput) {
        waitUntilIsDisplayed(stringToFind);
        //In some pages relative locator finds incorrect number of elements.
        //Locator targetLocators = playwrightPage.locator("[type=checkbox]:near(:text(\"" + eachText + "\"))");
        //Locator checkbox = targetLocators.count() > 0 ? targetLocators.first() : null;
        Locator targetLocators = playwrightPage.locator("input[type=" + typeOfInput + "]");
        for (int i = 0; i < targetLocators.count(); i++) {
            Locator eachLocator = targetLocators.nth(i);
            while (eachLocator.textContent().trim().length() == 0)
                eachLocator = eachLocator.locator("xpath=..");
            if (eachLocator.isVisible() && eachLocator.textContent().contains(stringToFind))
                return eachLocator;
        }
        return getEmptyLocator();
    }

    private Locator getEmptyLocator() {
        return playwrightPage.locator("DoesNotExist");
    }

    private boolean clickWhichButton(String text, boolean isExact, boolean isRightClick) throws Exception {
        Locator.ClickOptions clickOptions = new Locator.ClickOptions();
        if (isRightClick)
            clickOptions.setButton(MouseButton.RIGHT);
        if (isPlaywrightLocator(text)) {
            playwrightPage.waitForSelector(text);
            lastLocatorInteractedWith = playwrightPage.locator(text).first();
            lastLocatorInteractedWith.click(clickOptions);
        } else {
            waitUntilIsDisplayed(text);
            try {
                try {
                    lastLocatorInteractedWith = getEmptyLocator();
                    lastLocatorInteractedWith = isExact ? getFirstElementWithExactText(text) : getAllElementsContainingText(text);
                } catch (Exception ex) {
                    lastLocatorInteractedWith = getElementWithinDefaultTime("[value='" + text + "']");
                }
                if (lastLocatorInteractedWith.count() > 1)
                    lastLocatorInteractedWith = lastLocatorInteractedWith.first();

                lastLocatorInteractedWith.click(clickOptions);
            } catch (TimeoutError ex) {
                //Force click it per comment in https://github.com/microsoft/playwright/issues/12298#issuecomment-1051261068
                lastLocatorInteractedWith.click(clickOptions.setForce(true));
            }
        }
        playwrightPage.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return true;
    }

    private Locator getTargetTableCell(String columnToGet, String valueToFind) throws Exception {

        //TODO: It does not work when the column header name has parantheses
        //This is to accommodate case where column name contains equal sign
        int lastIndexOfEqualSign = valueToFind.lastIndexOf("=");
        String columnToLocate = valueToFind.substring(0, lastIndexOfEqualSign);
        String valueToLocate = valueToFind.substring(lastIndexOfEqualSign + 1);

        Locator tables = playwrightPage.locator("table,[role=grid]");

        int columnIndexToFind = -1;
        int columnIndexToGet = -1;

        for (int i = 0; i < tables.count(); i++) {
            Locator table = tables.nth(i);
            Locator headers = table.locator("th,[role=columnHeader],[role=columnheader]");
            for (int j = 0; j < headers.count(); j++) {
                String headerText = headers.nth(j).textContent();
                if (headerText.trim().equals(columnToLocate)) {
                    columnIndexToFind = j;
                }
                if (headerText.trim().equals(columnToGet)) {
                    columnIndexToGet = j;
                }
                if (columnIndexToFind != -1 && columnIndexToGet != -1) {
                    table.scrollIntoViewIfNeeded();
                    Locator rows = table.locator("tbody >> tr");
                    rows = rows.count() == 0 ? table.locator("[role=row]") : rows;
                    for (int k = 0; k < rows.count(); k++) {
                        Locator eachRow = rows.nth(k);
                        Locator cells = eachRow.locator("[role=cell],[role=gridcell]");
                        cells = cells.count() == 0 ? eachRow.locator("th,td") : cells;
                        for (int l = 0; l < cells.count(); l++) {
                            if (cells.nth(columnIndexToFind).textContent().equals(valueToLocate)) {
                                return cells.nth(columnIndexToGet);
                            }
                        }
                    }
                }
            }
        }
        throw new Exception("Did not get any table with the criteria: " + valueToFind + " & existence of column" + columnToGet);
    }

    private Locator getAllElementsContainingText(String text) {
        waitUntilIsDisplayed(text);
        String locatorString = "text=" + text;
        Locator locator = getElementWithinDefaultTime(locatorString);
        if (locator.count() > 0)
            return locator;

        locatorString = "xpath=//input[contains(@placeholder,'" + text + "')]";
        Locator placeholderSelectors = getElementWithinDefaultTime(locatorString);

        return placeholderSelectors;
    }

    private Locator getFirstElementWithExactText(String text) throws Exception {
        //Cannot do exact text locator directly because some elements have newline or space in the front or in the back
        Locator locator = getAllElementsContainingText(text);
        for (int i = 0; i < locator.count(); i++) {
            if (locator.nth(i).textContent().trim().equals(text))
                return locator.nth(i);
        }
        throw new Exception("Could not find an element with text : " + text);
    }

}
