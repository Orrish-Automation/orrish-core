package com.orrish.automation.entrypoint;

import com.orrish.automation.database.DatabaseWithReportUtility;
import com.orrish.automation.utility.GeneralUtility;
import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.verification.VerifyAndReportUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseSteps {

    private boolean shouldDBStepBeExecuted() {
        if (SetUp.databaseCheck) {
            return true;
        }
        ReportUtility.reportInfo("Database check was disabled in SetUp page. So, it was not checked.");
        return false;
    }

    public boolean doesDBQueryReturn(String query, String expectedValue) {
        return !shouldDBStepBeExecuted() || doesDBQueryReturnOneOf(query, Collections.singletonList(expectedValue));
    }

    public boolean doesDBQueryReturnOneOf(String query, List expectedValue) {
        return !shouldDBStepBeExecuted() || DatabaseWithReportUtility.runQueryAndCompareResult(query, expectedValue);
    }

    public boolean verifyDBQueryResultOfShouldHave(String query, String valueToCompare) {
        if (shouldDBStepBeExecuted()) {
            if (valueToCompare.trim().toLowerCase().contentEquals("donotverify")) {
                ReportUtility.reportInfo("Not running query as it is marked to be not verified.");
                return true;
            }
            String valueFromDatabase = String.valueOf(DatabaseWithReportUtility.runQuery(query));
            return VerifyAndReportUtility.doesContain(valueFromDatabase, valueToCompare);
        }
        return true;
    }

    public String runDatabaseQuery(String queryToRun) {
        if (shouldDBStepBeExecuted()) {
            List valueToReturn = runQueryOrCommand(queryToRun, false);
            return valueToReturn.size() == 0 ? null : String.valueOf(valueToReturn.get(0));
        }
        return "";
    }

    public String runDatabaseCommand(String commandToRun) {
        return shouldDBStepBeExecuted()
                ? String.valueOf(runQueryOrCommand(commandToRun, true).get(0))
                : "";
    }

    private List runQueryOrCommand(String query, boolean isCommand) {
        List valueToReturn = new ArrayList();
        valueToReturn.add("");
        return shouldDBStepBeExecuted()
                ? DatabaseWithReportUtility.runQueryOrCommand(query, isCommand)
                : valueToReturn;
    }

    public String runQueryAndReturnListAsString(String queryToRun) {
        return String.valueOf(runQueryAndReturnList(queryToRun));
    }

    public List runQueryAndReturnList(String query) {
        return runQueryOrCommand(query, false);
    }

    public String getFirstDocumentFromMongoDBForCollectionWithCriteria(String collectionName, String criteria) {
        if (shouldDBStepBeExecuted()) {
            String dataToBeReturned = DatabaseWithReportUtility.getFirstDocumentFromMongoDBWithCriteria(collectionName, criteria);
            if (dataToBeReturned != null)
                ReportUtility.reportJsonAsInfo("Got document from mongodb for collection " + collectionName, dataToBeReturned);
            return dataToBeReturned;
        }
        return null;
    }

    public String getFirstDocumentFromMongoDBForCollection(String collectionName) {
        if (shouldDBStepBeExecuted()) {
            String dataToBeReturned = DatabaseWithReportUtility.getFirstDocumentFromMongoDBWithCriteria(collectionName, null);
            if (dataToBeReturned != null)
                ReportUtility.reportJsonAsInfo("Got first document from mongodb for collection " + collectionName, dataToBeReturned);
            return dataToBeReturned;
        }
        return null;
    }

    public String waitTillTheDbQueryReturnsValueUpToSeconds(String queryToRun, int seconds) {
        if (shouldDBStepBeExecuted()) {
            do {
                //wait a second and try again
                GeneralUtility.waitSeconds(1);
                String dataToBeReturned = String.valueOf(runQueryOrCommand(queryToRun, false).get(0));
                if (!dataToBeReturned.equals("null")) {
                    return dataToBeReturned;
                }
            } while (--seconds > 0);
        }
        return null;
    }

    public int updateMongoDBForCollectionSetForCriteria(String collectionName, String setValue, String criteria) {
        return (shouldDBStepBeExecuted())
                ? DatabaseWithReportUtility.updateMongoDBForCollectionSetForCriteria(collectionName, setValue, criteria)
                : 0;
    }

    public int deleteInMongoDBForCollectionWithCriteria(String collectionName, String criteria) {
        return shouldDBStepBeExecuted()
                ? DatabaseWithReportUtility.deleteInMongoDBForCollectionWithCriteria(collectionName, criteria)
                : 0;
    }
}
