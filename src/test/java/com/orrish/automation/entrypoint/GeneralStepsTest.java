package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GeneralStepsTest {

    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.reportEnabled(false);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }


    @Test
    public void runSampleTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        assertTrue(generalSteps.doesStartWith("hello", "he"));

    }

    @Test
    public void stringVerify() {
        GeneralSteps generalSteps = new GeneralSteps();
    }

    @Test
    public void doesContainTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        assertTrue(generalSteps.doesContain("hello", "he"));
        assertTrue(generalSteps.doesContain("hello", "lo"));
        assertFalse(generalSteps.doesContain("hello", "hi"));
        assertTrue(generalSteps.doesContainByIgnoringCase("hello", "He"));
        assertFalse(generalSteps.doesContainByIgnoringCase("hello", "HI"));
        assertTrue(generalSteps.doesContain("hello", ""));
    }

    @Test
    public void splitWithDelimiter() {
        GeneralSteps generalSteps = new GeneralSteps();


        String[] actual = generalSteps.splitWithDelimiter("abcd-efgh-ijkl", "-");
        assertEquals(actual, new String[]{"abcd", "efgh", "ijkl"});
        assertEquals(generalSteps.getIndexFromArray(2, actual), "efgh");
        assertEquals(generalSteps.splitWithDelimiter("abcd-efgh-ijkl-", "-"), new String[]{"abcd", "efgh", "ijkl"});
        assertEquals(generalSteps.splitWithDelimiter("abcd", "-"), new String[]{"abcd"});
        assertEquals(generalSteps.splitWithDelimiterAndReturnLastString("abcd-efgh-ijkl", "-"), "ijkl");
    }


    @Test
    public void sumDecimalTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        List<String> input = new ArrayList<>();
        input.add("12.2");
        input.add("-2.5");

        assertEquals(generalSteps.getSumOfDecimalValuesInList(input), "9.70");
    }

    @Test
    public void sumIntegerTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        List<String> input = new ArrayList<>();
        input.add("12");
        input.add("-2");

        assertEquals(generalSteps.getSumOfIntegerValuesInList(input), "10");

    }

    @Test
    public void onlyDigitsTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        assertEquals(generalSteps.isOnlyDigits("abc"), false);
        assertEquals(generalSteps.isOnlyDigits("123"), true);
        assertEquals(generalSteps.isOnlyDigits("12.3"), false);
        assertEquals(generalSteps.isOnlyDigits("123   "), false);
        assertEquals(generalSteps.isOnlyDigits("-123"), false);
    }

    @Test
    public void subtractTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        assertEquals(generalSteps.subtractFrom(3, 5), 2);
        assertEquals(generalSteps.subtractFrom(-1, 5), 6);
        assertEquals(generalSteps.subtractFrom(-3, -5), -2);
    }

    @Test
    public void concatenateTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        assertEquals(generalSteps.concatenateAnd("    a   ", "   b   "), "ab");
        assertEquals(generalSteps.concatenateAnd(null, "   b   "), "nullb");
    }

    @Test
    public void getValidString() {

        GeneralSteps generalSteps = new GeneralSteps();
        assertEquals(generalSteps.getValidStringBetweenAnd(null, "2"), "2");
        assertEquals(generalSteps.getValidStringBetweenAnd(null, null), "");
        assertEquals(generalSteps.getValidStringBetweenAnd("1", null), "1");
        assertEquals(generalSteps.getValidStringBetweenAnd("donotmodify", "2"), "2");
        assertEquals(generalSteps.getValidStringBetweenAnd("1", "donotmodify"), "1");
        assertEquals(generalSteps.getValidStringBetweenAnd("1", "2"), "1");
    }

    @Test
    public void replaceString() {
        GeneralSteps generalSteps = new GeneralSteps();
        String value = generalSteps.replaceStringWithIn("hello", "hi", "hello orrish core");
        assertEquals(value, "hi orrish core");
    }

}
