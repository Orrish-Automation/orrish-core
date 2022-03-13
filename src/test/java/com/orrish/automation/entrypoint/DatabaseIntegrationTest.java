package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class DatabaseIntegrationTest {

    SetUp setUp = new SetUp();
    DatabaseSteps databaseSteps = new DatabaseSteps();

    @BeforeClass
    public void suiteSetUp() {

        setUp.databaseCheck("true");
        setUp.databaseConnectionString("jdbc:postgresql://postgres:5432/postgres");
        setUp.databaseUsername("postgres");
        setUp.databasePassword("postgres");

        databaseSteps.runDBCommand("CREATE TABLE student(id SERIAL PRIMARY KEY, firstName VARCHAR(40) NOT NULL, lastName VARCHAR(40))");
        databaseSteps.runDBCommand("INSERT INTO student(firstName,lastName) VALUES ('firstFirst','firstLast'),('secondFirst','secondLast')");

    }

    @AfterMethod
    public void tearDown() {
        new TearDown();
    }

    @AfterClass
    public void suiteTearDown() {
        databaseSteps.runDBCommand("DROP TABLE student;");
    }

    @Test
    public void dbQueryTest() {
        assertTrue(databaseSteps.doesDBQueryReturn("Select firstName from student where firstName='firstFirst'", "firstFirst"));
        assertTrue(databaseSteps.doesDBQueryResultContain("Select firstName from student where firstName='firstFirst'", "first"));
        assertTrue(databaseSteps.doesDBQueryReturnOneOf("Select firstName from student where firstName='secondFirst'", Arrays.asList(new String[]{"firstFirst", "secondFirst"})));
        assertFalse(databaseSteps.doesDBQueryReturnOneOf("Select lastName from student where lastName='secondLast'", Arrays.asList(new String[]{"firstFirst", "secondFirst"})));
    }

    @Test
    public void noDBAccessWhenDisabled() {
        setUp.databaseCheck("false");
        assertEquals(0, databaseSteps.runDBQueryAndGetColumn("Select firstName from student where firstName='firstFirst'").size());
        assertEquals("", databaseSteps.runDBCommand("Select firstName from student where firstName='firstFirst'"));
        setUp.databaseCheck("true");
    }

    @Test
    public void dbQueryTests() {

        String stringValue = databaseSteps.runDBQueryAndGetCell("Select firstName from student where firstName='firstFirst'");
        assertEquals("firstFirst", stringValue);

        List<String> stringList = databaseSteps.runDBQueryAndGetColumn("Select firstName from student");
        assertEquals(2, stringList.size());
        assertTrue(Arrays.equals(stringList.toArray(), new String[]{"firstFirst", "secondFirst"}));

        List mapList = databaseSteps.runDBQueryAndGetRecords("Select * from student");
        assertEquals(2, mapList.size());
        assertTrue(mapList.get(0) instanceof HashMap);
        assertEquals(3, ((Map) mapList.get(0)).size());

    }

    @Test
    public void dbQueryReturnAfterWait() {
        assertEquals("{firstname=firstFirst}", databaseSteps.waitTillDBQueryReturnsValueWaitingUpToSeconds("Select firstName from student where firstName='firstFirst'", 5));
        assertEquals(null, databaseSteps.waitTillDBQueryReturnsValueWaitingUpToSeconds("Select firstName from student where firstName='nonExistent'", 2));
    }

}
