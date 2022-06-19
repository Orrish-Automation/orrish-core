package com.orrish.automation.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.SelectOption;
import com.orrish.automation.entrypoint.SetUp;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;
import static com.orrish.automation.entrypoint.SetUp.defaultWaitTime;

public class ElementActions {

    protected static Playwright playwright;
    protected static Page playwrightPage;
    public boolean isPlaywrightStepPassed = true;
    protected Locator lastLocatorInteractedWith;

    protected List<String> htmlTags = Arrays.asList(new String[]{"a", "input", "img", "button", "div", "span", "p"});

    public boolean checkTextIsPresentInWebpage(String text) {
        Locator locator = getElementWithinDefaultTime("text=" + text);
        for (int i = 0; i < locator.count(); i++) {
            if (locator.nth(i).textContent().contains(text))
                return true;
        }
        return false;
    }

    public boolean hoverOn(String text) throws Exception {
        getFirstElementWithExactText(text).hover();
        return true;
    }

    public boolean scrollTo(String locatorText) {
        waitUntilIsDisplayed(locatorText);
        getAllElementsContainingText(locatorText).first().scrollIntoViewIfNeeded();
        return true;
    }

    public boolean click(String value) {
        return click(value, true);
    }

    public boolean clickWithPartialText(String value) {
        return click(value, false);
    }

    public boolean clickNumber(String text, String whichCount) {
        return clickWhichElement(text, false, false, Integer.parseInt(whichCount));
    }

    public boolean selectCheckboxForText(String text) throws Exception {
        return selectUnselectcheckboxeswithText(text, true);
    }

    public boolean unselectCheckboxForText(String text) throws Exception {
        return selectUnselectcheckboxeswithText(text, false);
    }

    private boolean click(String text, boolean isExact) {
        return clickWhichElement(text, isExact, false, 1);
    }

    public boolean rightClick(String text) {
        return clickWhichElement(text, false, true, 1);
    }

    public boolean clearText() {
        lastLocatorInteractedWith.fill("");
        return true;
    }

    public boolean clickIcon(String textToClick) throws Exception {

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

    public boolean clickColumnWhere(String columnToGet, String valueToFind) throws Exception {
        getTargetTableCell(columnToGet, valueToFind).click();
        return true;
    }

    public boolean clickInColumnWhere(String textToClick, String columnToGet, String valueToFind) throws Exception {
        Locator locator = getTargetTableCell(columnToGet, valueToFind);
        String locatorText = isPlaywrightLocator(textToClick) ? textToClick : "text=" + textToClick;
        locator.locator(locatorText).click();
        return true;
    }

    public boolean typeInColumnWhere(String textToType, String columnToGet, String valueToFind) throws Exception {
        Locator locator = getTargetTableCell(columnToGet, valueToFind);
        locator.locator("input").type(textToType);
        return true;
    }

    public String getColumnWhere(String columnToGet, String valueToFind) throws Exception {
        return getTargetTableCell(columnToGet, valueToFind).textContent();
    }

    private Locator getRelativeImage(String iconTextToClick, String direction, String textToFind) {
        //svg:right-of(:text("Home"))
        String relativeLocatorString = ":" + getDirection(direction) + "(:text(\"" + textToFind + "\"))";
        Locator icons = getIconCorrespondingTo(iconTextToClick, "svg" + relativeLocatorString);
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "img" + relativeLocatorString) : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "button" + relativeLocatorString) : icons;
        icons = (icons.count() == 0) ? getIconCorrespondingTo(iconTextToClick, "a" + relativeLocatorString) : icons;
        return icons;
    }

    public boolean clickToTheOf(String textToClick, String direction, String textToFind) {
        lastLocatorInteractedWith = getRelativeTextElement(textToClick, direction, textToFind);
        lastLocatorInteractedWith.click();
        return true;
    }

    public String getTextFromToTheOf(String textToClick, String direction, String textToFind) {
        return getRelativeTextElement(textToClick, direction, textToFind).textContent();
    }

    public boolean clickIconNextTo(String iconToClick, String textToFind) throws Exception {
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

    public boolean clickWhicheverIsDisplayedIn(String locator) throws Exception {
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

    public boolean type(String value) {
        lastLocatorInteractedWith.type(value);
        return true;
    }

    public boolean pressKey(String value) {
        playwrightPage.keyboard().press(value);
        return true;
    }

    public boolean typeIn(String value, String locatorString) throws Exception {
        Locator locators = getElementWithinDefaultTime(locatorString);
        locators = locators.count() == 0 ? getElementWithinDefaultTime("xpath=//input[contains(@placeholder,'" + locatorString + "')]") : locators;
        for (int i = 0; i < locators.count(); i++) {
            Locator eachLocator = locators.nth(i);
            String tagName = eachLocator.elementHandle().getProperty("tagName").jsonValue().toString();
            if (!tagName.equalsIgnoreCase("input")) {
                for (int j = 0; eachLocator.locator("input").count() == 0 && j < 10; j++)
                    eachLocator = eachLocator.locator("xpath=..");
            }
            tagName = eachLocator.elementHandle().getProperty("tagName").jsonValue().toString();
            eachLocator = tagName.equalsIgnoreCase("input") ? eachLocator : eachLocator.locator("input");
            try {
                eachLocator.fill(value);
                return true;
            } catch (Exception ex) {
            }
        }
        throw new Exception("Could not find the locator : " + locatorString);
    }

    public boolean typeWithPartialText(String value, String locatorString) throws Exception {
        return typeIn(value, "text=" + locatorString);
    }

    public boolean typeInNumber(String text, String whichField) {
        return typeInNumber(text, Integer.parseInt(whichField));
    }

    private boolean typeInNumber(String text, int whichField) {
        playwrightPage.waitForSelector("input");
        Locator locator = playwrightPage.locator("input");
        locator.nth(whichField - 1).fill(text); //Convert to zero based index.
        return true;
    }

    public boolean selectFromDropdown(String value, String locatorText) {
        Locator locator = playwrightPage.locator("text=" + locatorText);
        locator = locator.locator("xpath=..");
        locator.selectOption(new SelectOption().setLabel(value));
        return true;
    }

    public String clickAndGetAlertText(String locatorText) throws Exception {
        final String[] message = new String[1];
        playwrightPage.onDialog(dialog -> message[0] = dialog.message());
        Locator locator = getFirstElementWithExactText(locatorText);
        locator.click();
        return message[0];
    }

    public boolean clickAndAcceptAlertIfPresent(String locatorText) {
        playwrightPage.onDialog(dialog -> dialog.accept());
        Locator locator = getAllElementsContainingText(locatorText);
        locator.first().click();
        return true;
    }

    public boolean waitUntilIsGone(String locatorText) {
        for (int i = 0; i < SetUp.defaultWaitTime; i++) {
            locatorText = isPlaywrightLocator(locatorText) ? locatorText : "text=" + locatorText;
            if (!isElementDisplayed(locatorText))
                return true;
            waitSeconds(1);
        }
        return false;
    }

    public boolean waitUntilOneOfIsDisplayed(String locatorString) {
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

    public boolean waitUntilOneOfIsEnabled(String locator) {
        return waitUntilOneOfTheElements(locator, "enabled");
    }

    public boolean waitUntilIsDisplayed(String locatorText) {
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

    public boolean isPlaywrightLocator(String locatorString) {
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

    public boolean waitUntilContains(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, true);
    }

    public boolean waitUntilDoesNotContain(String locator, String text) {
        return waitUntilElementTextCheck(locator, text, false);
    }

    public boolean selectRadioForText(String text) throws Exception {

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

    private boolean selectUnselectcheckboxeswithText(String stringToFind, boolean shouldBeSelected) throws Exception {
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

    public String getFullTextFor(String text) {
        String locatorText = isPlaywrightLocator(text) ? text : "text=" + text;
        return playwrightPage.textContent(locatorText);
    }

    public boolean uploadFile(String filePath) throws Exception {
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

    private boolean clickWhichElement(String locatorText, boolean isExact, boolean isRightClick, int whichElement) {
        lastLocatorInteractedWith = getEmptyLocator();
        Locator.ClickOptions clickOptions = new Locator.ClickOptions();
        if (isRightClick)
            clickOptions.setButton(MouseButton.RIGHT);
        if (isPlaywrightLocator(locatorText)) {
            playwrightPage.waitForSelector(locatorText);
            lastLocatorInteractedWith = playwrightPage.locator(locatorText).nth(whichElement - 1);
            lastLocatorInteractedWith.click(clickOptions);
        } else {
            waitUntilIsDisplayed(locatorText);
            try {
                lastLocatorInteractedWith = isExact ? getAllElementsWithExactText(locatorText) : getAllElementsContainingText(locatorText);
                lastLocatorInteractedWith = lastLocatorInteractedWith.nth(whichElement - 1);
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

    private Locator getAllElementsWithExactText(String text) {
        waitUntilIsDisplayed(text);
        String locatorString = "text='" + text + "'";
        return getElementWithinDefaultTime(locatorString);
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
