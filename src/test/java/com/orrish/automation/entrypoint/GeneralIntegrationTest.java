package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;

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

        GeneralSteps generalSteps = new GeneralSteps();

        String value = "test";
        assertFalse(generalSteps.setConditionalValueForNextStep(value + "=invalid"));
        assertTrue(generalSteps.downloadFromUrlAndSaveAs("invalidUrl", ""));
        assertTrue(generalSteps.setConditionalValueForNextStep(value + "=test"));
        assertTrue(generalSteps.setConditionalValueForNextStep("true"));
        assertFalse(generalSteps.downloadFromUrlAndSaveAs("invalidUrl", ""));
        assertTrue(generalSteps.resetConditionalValueForNextStep());

        assertTrue(generalSteps.echo("hello").equals("hello"));
        assertEquals(generalSteps.getValidStringBetweenAnd(null, "abc"), "abc");
        assertEquals(generalSteps.getValidStringBetweenAnd(null, "null"), "null");
        assertEquals(generalSteps.getValidStringBetweenAnd("donotmodify", "valid"), "valid");
        assertEquals(generalSteps.concatenateAnd("a", "b"), "ab");
        assertEquals(generalSteps.subtractFrom(2, 5), 3);
        assertTrue(generalSteps.isOnlyDigits("564"));
        assertFalse(generalSteps.isOnlyDigits("ab76"));
        String[] ints = new String[]{"1", "2", "3", "4"};
        assertEquals(generalSteps.getSumOfIntegerValuesInList(Arrays.asList(ints)), "10");
        String[] decimals = new String[]{"1.1", "2.2", "3.3", "4.4"};
        assertEquals(generalSteps.getSumOfDecimalValuesInList(Arrays.asList(decimals)), "11.00");

        assertEquals(generalSteps.splitWithDelimiter("abc-def", "-"), new String[]{"abc", "def"});
        assertEquals(generalSteps.splitWithDelimiterAndReturnLastString("abc-def", "-"), "def");
        assertEquals(generalSteps.getIndexFromArray(1, new String[]{"a", "b"}), "a");

        assertTrue(generalSteps.doesContain("hello", "he"));
        assertTrue(generalSteps.doesContainByIgnoringCase("hello", "He"));
        assertTrue(generalSteps.doesStartWith("hello", "he"));
        assertTrue(generalSteps.doesMatchPattern("345", "[0-9]+"));

        assertTrue(generalSteps.isEqualWithoutValidation("123", "123"));
        assertTrue(generalSteps.isListEqual(Arrays.asList(new String[]{"1", "2"}), Arrays.asList(new String[]{"2", "1"})));
        assertTrue(generalSteps.areAllValuesInListOneOf(Arrays.asList(new String[]{"1", "2", "2", "1"}), Arrays.asList(new String[]{"2", "1"})));
        assertTrue(generalSteps.isOneOf("1", Arrays.asList(new String[]{"1", "2"})));
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("1", "one");
        assertTrue(generalSteps.isValueInIs("1", hashMap, "one"));
        assertTrue(generalSteps.isValueInIsNot("1", hashMap, "1"));

        assertTrue(generalSteps.executeShell("date"));
        assertTrue(generalSteps.executeShellWithoutReporting("date"));
        assertTrue(generalSteps.executeShellWithoutWait("date"));

        String envValue = generalSteps.getFromSystemEnvironmentVariables("HOME");
        String alphaNumericString = generalSteps.getCharacterRandomAlphaNumericString(2);

    }
}
