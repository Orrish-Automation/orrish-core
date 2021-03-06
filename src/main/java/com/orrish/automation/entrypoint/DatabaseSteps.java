package com.orrish.automation.entrypoint;

import com.orrish.automation.database.DatabaseWithReportUtility;
import com.orrish.automation.utility.report.ReportUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.orrish.automation.entrypoint.GeneralSteps.waitSeconds;

public class DatabaseSteps {

    protected DatabaseWithReportUtility databaseWithReportUtility;
    GeneralSteps generalSteps = new GeneralSteps();

    public DatabaseSteps() {
        databaseWithReportUtility = new DatabaseWithReportUtility();
    }

    private boolean shouldDBStepBeExecuted() {
        if (SetUp.databaseCheck && GeneralSteps.conditionalStep) {
            return true;
        }
        ReportUtility.reportInfo("Database check was disabled in SetUp page. So, it was not checked.");
        return false;
    }

    public boolean doesDBQueryReturn(String query, String expectedValue) {
        return !shouldDBStepBeExecuted() || doesDBQueryReturnOneOf(query, Collections.singletonList(expectedValue));
    }

    public boolean doesDBQueryReturnOneOf(String query, List expectedValue) {
        return !shouldDBStepBeExecuted() || databaseWithReportUtility.isDbQueryResultIn(query, expectedValue);
    }

    public boolean doesDBQueryResultContain(String query, String valueToCompare) {
        if (shouldDBStepBeExecuted()) {
            if (valueToCompare.trim().toLowerCase().contentEquals("donotverify")) {
                ReportUtility.reportInfo("Not running query as it is marked to be not verified.");
                return true;
            }
            String valueFromDatabase = String.valueOf(databaseWithReportUtility.runDBQueryAndGetCell(query));
            return generalSteps.doesContain(valueFromDatabase, valueToCompare);
        }
        return true;
    }

    public String runDBQueryAndGetCell(String queryToRun) {
        if (shouldDBStepBeExecuted()) {
            return databaseWithReportUtility.runDBQueryAndGetCell(queryToRun);
        }
        return "";
    }

    public List<String> runDBQueryAndGetColumn(String queryToRun) {
        List<String> valueToReturn = new ArrayList<>();
        if (shouldDBStepBeExecuted()) {
            List<Map<String, Object>> valueFromDb = runQueryOrCommand(queryToRun, false);
            if (valueFromDb.size() == 0)
                return valueToReturn;
            if (valueFromDb.get(0).size() != 1) {
                ReportSteps.writeInReport("You have specified a query which returns more than one column. Please modify query.");
                return valueToReturn;
            }
            valueFromDb.forEach(e -> {
                String key = e.entrySet().iterator().next().getKey();
                Object valueObject = e.get(key);
                String valueString = (valueObject == null) ? "" : String.valueOf(valueObject);
                valueToReturn.add(valueString);
            });
        }
        return valueToReturn;
    }

    public List runDBQueryAndGetRecords(String queryToRun) {
        if (shouldDBStepBeExecuted()) {
            List valueToReturn = runQueryOrCommand(queryToRun, false);
            return valueToReturn;
        }
        return new ArrayList();
    }

    public String waitTillDBQueryReturnsValueWaitingUpToSeconds(String queryToRun, int seconds) {
        if (shouldDBStepBeExecuted()) {
            do {
                List value = runQueryOrCommand(queryToRun, false);
                if (value != null && value.size() > 0) {
                    String dataToBeReturned = String.valueOf(runQueryOrCommand(queryToRun, false).get(0));
                    if (!dataToBeReturned.equals("null")) {
                        return dataToBeReturned;
                    }
                }
                //wait a second and try again
                waitSeconds(1);
            } while (--seconds > 0);
        }
        return null;
    }

    public String runDBCommand(String commandToRun) {
        return shouldDBStepBeExecuted()
                ? String.valueOf(runQueryOrCommand(commandToRun, true).get(0))
                : "";
    }

    private List runQueryOrCommand(String query, boolean isCommand) {
        return shouldDBStepBeExecuted()
                ? databaseWithReportUtility.runQueryOrCommand(query, isCommand)
                : new ArrayList();
    }

    public boolean createCollectionInMongoDB(String collectionName) {
        return databaseWithReportUtility.createCollectionInMongoDB(collectionName);
    }

    public boolean dropCollectionInMongoDB(String collectionName) {
        return databaseWithReportUtility.dropCollectionInMongoDB(collectionName);
    }

    public boolean insertMongoDBDocumentInCollection(String document, String collectionName) {
        return databaseWithReportUtility.insertMongoDBDocumentInCollection(document, collectionName);
    }

    public String getFirstDocumentFromMongoDBForCollectionWithCriteria(String collectionName, String criteria) {
        if (shouldDBStepBeExecuted()) {
            String dataToBeReturned = databaseWithReportUtility.getFirstDocumentFromMongoDBWithCriteria(collectionName, criteria);
            if (dataToBeReturned != null)
                ReportUtility.reportJsonAsInfo("Got document from mongodb for collection " + collectionName, dataToBeReturned);
            return dataToBeReturned;
        }
        return null;
    }

    public String getFirstDocumentFromMongoDBForCollection(String collectionName) {
        if (shouldDBStepBeExecuted()) {
            String dataToBeReturned = databaseWithReportUtility.getFirstDocumentFromMongoDBWithCriteria(collectionName, null);
            if (dataToBeReturned != null)
                ReportUtility.reportJsonAsInfo("Got first document from mongodb for collection " + collectionName, dataToBeReturned);
            return dataToBeReturned;
        }
        return null;
    }

    public int updateMongoDBForCollectionSetForCriteria(String collectionName, String setValue, String criteria) {
        return (shouldDBStepBeExecuted())
                ? databaseWithReportUtility.updateMongoDBForCollectionSetForCriteria(collectionName, setValue, criteria)
                : 0;
    }

    public int deleteInMongoDBForCollectionWithCriteria(String collectionName, String criteria) {
        return shouldDBStepBeExecuted()
                ? databaseWithReportUtility.deleteInMongoDBForCollectionWithCriteria(collectionName, criteria)
                : 0;
    }
}
