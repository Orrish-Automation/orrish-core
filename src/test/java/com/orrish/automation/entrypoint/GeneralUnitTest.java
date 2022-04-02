package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GeneralUnitTest {

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
    public void generalStepsTest() {

        GeneralSteps generalSteps = new GeneralSteps();
        assertTrue(generalSteps.echo("hello").equals("hello"));

        assertTrue(generalSteps.isEqualWithoutValidation("123", "123"));
        assertTrue(generalSteps.isListEqual(Arrays.asList(new String[]{"1", "2"}), Arrays.asList(new String[]{"2", "1"})));
        assertTrue(generalSteps.areAllValuesInListOneOf(Arrays.asList(new String[]{"1", "2", "2", "1"}), Arrays.asList(new String[]{"2", "1"})));
        assertTrue(generalSteps.isOneOf("1", Arrays.asList(new String[]{"1", "2"})));
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("1", "one");
        assertTrue(generalSteps.isValueInIs("1", hashMap, "one"));
        assertTrue(generalSteps.isValueInIsNot("1", hashMap, "1"));
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
        assertTrue(generalSteps.doesStartWith("hello", "he"));
        assertTrue(generalSteps.doesMatchPattern("345", "[0-9]+"));
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

        String[] decimals = new String[]{"1.1", "2.2", "3.3", "4.4"};
        assertEquals(generalSteps.getSumOfDecimalValuesInList(Arrays.asList(decimals)), "11.00");

    }

    @Test
    public void sumIntegerTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        List<String> input = new ArrayList<>();
        input.add("12");
        input.add("-2");

        assertEquals(generalSteps.getSumOfIntegerValuesInList(input), "10");

        String[] ints = new String[]{"1", "2", "3", "4"};
        assertEquals(generalSteps.getSumOfIntegerValuesInList(Arrays.asList(ints)), "10");

    }

    @Test
    public void onlyDigitsTest() {
        GeneralSteps generalSteps = new GeneralSteps();
        assertFalse(generalSteps.isOnlyDigits("ab76"));
        assertFalse(generalSteps.isOnlyDigits("abc"));
        assertTrue(generalSteps.isOnlyDigits("123"));
        assertFalse(generalSteps.isOnlyDigits("12.3"));
        assertFalse(generalSteps.isOnlyDigits("123   "));
        assertFalse(generalSteps.isOnlyDigits("-123"));
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
        assertEquals(generalSteps.concatenateAnd("a", "b"), "ab");
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
        assertEquals(generalSteps.getValidStringBetweenAnd(null, "null"), "null");
    }

    @Test
    public void replaceString() {
        GeneralSteps generalSteps = new GeneralSteps();
        String value = generalSteps.replaceStringWithIn("hello", "hi", "hello orrish core");
        assertEquals(value, "hi orrish core");
    }

    @Test
    public void testDateValues() {
        GeneralSteps generalSteps = new GeneralSteps();
        Map<String, Integer> values = generalSteps.secondsConvertedToHHmmss(5000);
        int value = values.get("hours");
        assertEquals(value, 1);
        value = values.get("minutes");
        assertEquals(value, 23);
        value = values.get("seconds");
        assertEquals(value, 20);

        int date = Integer.parseInt(generalSteps.getCurrentTimeInTheFormat("dd"));
        int futureDate = Integer.parseInt(generalSteps.getTimeInTheFormatPlusDaysFromToday("dd", 1));
        if (futureDate == 1) {
            assertTrue(generalSteps.isOneOf(String.valueOf(date), Arrays.asList(new String[]{"28", "29", "30", "31"})));
        } else {
            assertEquals(futureDate - date, 1);
        }

        date = Integer.parseInt(generalSteps.getCurrentGMTTimeInTheFormat("dd"));
        futureDate = Integer.parseInt(generalSteps.getGMTTimeInTheFormatPlusDaysFromToday("dd", 1));
        int pastDate = Integer.parseInt(generalSteps.getGMTTimeInTheFormatMinusDaysFromToday("dd", 1));
        List lastDayOfTheMonth = Arrays.asList(new String[]{"28", "29", "30", "31"});
        if (futureDate == 1) {
            assertTrue(generalSteps.isOneOf(String.valueOf(date), lastDayOfTheMonth));
            assertEquals(date - pastDate, 1);
        } else {
            assertEquals(futureDate - date, 1);
        }
        if (date == 1) {
            assertTrue(generalSteps.isOneOf(String.valueOf(pastDate), lastDayOfTheMonth));
        }

    }

}
