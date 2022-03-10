package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class DatabaseIntegrationTests {

    @BeforeMethod
    public void setUp() {
        SetUp setUp = new SetUp();
        setUp.databaseCheck("true");
        setUp.databaseConnectionString("jdbc:postgresql://postgres:5432/postgres");
        setUp.databaseUsername("postgres");
        setUp.databasePassword("postgres");
    }

    @AfterMethod
    public void tearDown() {
        new TearDown();
    }

    @Test
    public void dbQueryTest() {
        DatabaseSteps databaseSteps = new DatabaseSteps();
        databaseSteps.runDBCommand("CREATE TABLE student(id SERIAL PRIMARY KEY, firstName VARCHAR(40) NOT NULL, lastName VARCHAR(40))");
        databaseSteps.runDBCommand("INSERT INTO student(firstName,lastName) VALUES ('firstFirst','firstLast'),('secondFirst','secondLast')");
        String value = databaseSteps.runDBQueryAndGetCell("Select firstName from student where firstName='firstFirst'");
        assertEquals("firstFirst", value);
    }

}
