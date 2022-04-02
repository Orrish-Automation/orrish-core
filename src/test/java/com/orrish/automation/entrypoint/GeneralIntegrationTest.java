package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class GeneralIntegrationTest {

    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.reportEnabled(true);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    @Test
    public void generalSteps() {

        ReportSteps.setSuiteName("suite");
        ReportSteps.setChildNode("child");
        ReportSteps.setTestName("suite::child");
        GeneralSteps generalSteps = new GeneralSteps();

        String value = "test";
        assertFalse(generalSteps.setConditionalValueForNextStep(value + "=invalid"));
        assertTrue(generalSteps.downloadFromUrlAndSaveAs("invalidUrl", ""));
        assertTrue(generalSteps.setConditionalValueForNextStep(value + "=test"));
        assertTrue(generalSteps.setConditionalValueForNextStep("true"));
        assertFalse(generalSteps.downloadFromUrlAndSaveAs("invalidUrl", ""));
        assertTrue(generalSteps.resetConditionalValueForNextStep());

        assertTrue(generalSteps.executeShell("date"));
        assertTrue(generalSteps.executeShellWithoutReporting("date"));
        assertTrue(generalSteps.executeShellWithoutWait("date"));

        String envValue = generalSteps.getFromSystemEnvironmentVariables("HOME");
        String alphaNumericString = generalSteps.getCharacterRandomAlphaNumericString(2);
        ReportSteps.updateReport();

    }

    @Test
    public void fileHandling() {
        GeneralSteps generalSteps = new GeneralSteps();
        assertNotNull(generalSteps.createFileWithContent("file.txt", "hello"));
        assertNotNull(generalSteps.appendFileWithContent("file.txt", "\norrish"));
        assertEquals(generalSteps.readFile("file.txt"), "hello\norrish");
        assertTrue(generalSteps.findLineWithTextInFile("hello", "file.txt"));
        assertTrue(generalSteps.deleteLineWithTextInFile("hello", "file.txt"));
        assertFalse(generalSteps.findLineWithTextInFile("hello", "file.txt"));
    }

}
