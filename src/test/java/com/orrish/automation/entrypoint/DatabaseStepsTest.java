package com.orrish.automation.entrypoint;

import com.orrish.automation.database.DatabaseWithReportUtility;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DatabaseStepsTest {

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
    public void runDBCommandTest() {

        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.databaseWithReportUtility = mock(DatabaseWithReportUtility.class);

        List returnValue = new ArrayList<String>();
        returnValue.add("1 row(s) affected.");
        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(returnValue);

        assertEquals(databaseSteps.runDBCommand(""), "1 row(s) affected.");
    }

    @Test
    public void waitUntilDBReturns() {

        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.databaseWithReportUtility = mock(DatabaseWithReportUtility.class);

        List returnValue = new ArrayList<String>();
        returnValue.add("test");
        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(returnValue);

        assertEquals(databaseSteps.waitTillDBQueryReturnsValueWaitingUpToSeconds("", 0), "test");
    }

    @Test
    public void runDBQueryAndGetColumnTest() {
        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.databaseWithReportUtility = mock(DatabaseWithReportUtility.class);

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleRecords());

        List<String> values = databaseSteps.runDBQueryAndGetColumn("");
        assertEquals(values.size(), 0);

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleColumns());
        values = databaseSteps.runDBQueryAndGetColumn("");
        assertEquals(values.size(), 0);

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleRecordsSingleColumn());
        values = databaseSteps.runDBQueryAndGetColumn("");
        assertEquals(values.size(), 3);
    }

    @Test
    public void dbQueryAndGetRecordsTest() {

        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.databaseWithReportUtility = mock(DatabaseWithReportUtility.class);

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleRecords());

        List<String> values = databaseSteps.runDBQueryAndGetRecords("");
        assertEquals(values.size(), 2);

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleColumns());
        values = databaseSteps.runDBQueryAndGetRecords("");
        assertEquals(values.size(), 1);

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleRecordsSingleColumn());
        values = databaseSteps.runDBQueryAndGetRecords("");
        assertEquals(values.size(), 3);
    }

    @Test
    public void dbQueryResultContainsTest() {

        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.databaseWithReportUtility = mock(DatabaseWithReportUtility.class);

        List<String> valueToReturn = new ArrayList<>();
        valueToReturn.add("orrish");
        valueToReturn.add("core");
        valueToReturn.add("test");
        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultSingleCell());
        when(databaseSteps.databaseWithReportUtility.isDbQueryResultIn(anyString(), anyList())).thenCallRealMethod();
        when(databaseSteps.databaseWithReportUtility.runDBQueryAndGetCell(anyString())).thenCallRealMethod();

        assertTrue(databaseSteps.doesDBQueryReturnOneOf("sample query", valueToReturn));

        valueToReturn.remove("test");

        assertFalse(databaseSteps.doesDBQueryReturnOneOf("sample query", valueToReturn));

        valueToReturn.clear();
        valueToReturn.add("donotverify");
        assertTrue(databaseSteps.doesDBQueryReturnOneOf("sample query", valueToReturn));

        assertTrue(databaseSteps.doesDBQueryResultContain("", "test"));
    }

    @Test
    public void databaseCheckDisabledTest() {

        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.databaseWithReportUtility = mock(DatabaseWithReportUtility.class);

        setUp.databaseCheck("false");
        assertTrue(databaseSteps.doesDBQueryReturn("", ""));
        verify(databaseSteps.databaseWithReportUtility,never()).runQueryOrCommand(anyString(),anyBoolean());

        setUp.databaseCheck("true");
        assertTrue(databaseSteps.doesDBQueryResultContain("", "donotverify"));
        verify(databaseSteps.databaseWithReportUtility,never()).runQueryOrCommand(anyString(),anyBoolean());

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultSingleCell());
        when(databaseSteps.databaseWithReportUtility.runDBQueryAndGetCell(anyString())).thenCallRealMethod();
        assertTrue(databaseSteps.doesDBQueryResultContain("", "test"));
        verify(databaseSteps.databaseWithReportUtility, times(1)).runQueryOrCommand(anyString(), anyBoolean());

    }

    @Test
    public void databaseCheckCell() {

        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.databaseWithReportUtility = mock(DatabaseWithReportUtility.class);

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultSingleCell());
        when(databaseSteps.databaseWithReportUtility.runDBQueryAndGetCell(anyString())).thenCallRealMethod();
        String value = databaseSteps.runDBQueryAndGetCell("SELECT NAME FROM TABLE WHERE id=1");
        assertEquals(value, "test");

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(new ArrayList());
        value = databaseSteps.runDBQueryAndGetCell("SELECT NAME FROM TABLE WHERE id=1");
        assertEquals(value, "No Data");

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleRecords());
        value = databaseSteps.runDBQueryAndGetCell("SELECT NAME FROM TABLE WHERE id=1");
        assertEquals(value, "Database returned 2 records. Only 1 is expected.");

        when(databaseSteps.databaseWithReportUtility.runQueryOrCommand(anyString(), anyBoolean())).thenReturn(getMockDBResultMultipleColumns());
        value = databaseSteps.runDBQueryAndGetCell("SELECT NAME FROM TABLE WHERE id=1");
        assertEquals(value, "Database returned 2 columns. Only 1 is expected.");
    }

    private List<Map<String, Object>> getMockDBResultSingleCell() {
        List<Map<String, Object>> valueToReturn = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("NAME", "test");
        valueToReturn.add(map);
        return valueToReturn;
    }

    private List<Map<String, Object>> getMockDBResultMultipleRecords() {
        List<Map<String, Object>> valueToReturn = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("NAME", "test1");
        map1.put("TITLE", "method1");
        valueToReturn.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("NAME", "test2");
        map2.put("TITLE", "method2");
        valueToReturn.add(map2);
        return valueToReturn;
    }

    private List<Map<String, Object>> getMockDBResultMultipleColumns() {
        List<Map<String, Object>> valueToReturn = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("NAME", "test");
        map.put("TITLE", "method");
        valueToReturn.add(map);
        return valueToReturn;
    }

    private List<Map<String, Object>> getMockDBResultMultipleRecordsSingleColumn() {
        List<Map<String, Object>> valueToReturn = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("NAME", "test1");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("NAME", "test2");
        Map<String, Object> map3 = new HashMap<>();
        map3.put("NAME", "test3");
        valueToReturn.add(map1);
        valueToReturn.add(map2);
        valueToReturn.add(map3);
        return valueToReturn;
    }

}
