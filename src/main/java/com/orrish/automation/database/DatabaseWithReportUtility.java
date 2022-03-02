package com.orrish.automation.database;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.orrish.automation.entrypoint.SetUp.mongoDbConnectionString;

public class DatabaseWithReportUtility {

    public static boolean isDbQueryResultIn(String query, List expectedValue) {
        if (expectedValue.get(0).toString().trim().toLowerCase().contentEquals("donotverify")) {
            ReportUtility.reportInfo("DB query not run. It is marked not to be verified.");
            return true;
        }
        String valueFromDb = runDBQueryAndGetCell(query);
        if (expectedValue.contains(valueFromDb)) {
            ReportUtility.reportPass("Database value: " + valueFromDb + System.lineSeparator() + "Expected: " + expectedValue);
            return true;
        } else {
            ReportUtility.reportFail("Database value: " + valueFromDb + System.lineSeparator() + "Expected:  " + expectedValue);
            return false;
        }
    }

    public static String runDBQueryAndGetCell(String query) {
        List<Map<String, Object>> values = runQueryOrCommand(query, false);
        if (values == null || values.size() == 0)
            return "No Data";
        if (values.size() > 1)
            return "Database returned " + values.size() + " records. Only 1 is expected.";
        if (!(values.get(0) instanceof java.util.Map))
            return String.valueOf(values.get(0));
        if (values.get(0).keySet().size() > 1)
            return "Database returned " + values.get(0).keySet().size() + " columns. Only 1 is expected.";
        String key = values.get(0).keySet().stream().findFirst().get();
        return String.valueOf(values.get(0).get(key));
    }

    public static String runCommand(String command) {
        return String.valueOf(runQueryOrCommand(command, true).get(0));
    }

    public static List runQueryOrCommand(String queryOrCommandToRun, boolean isCommand) {
        List valueToReturn = new ArrayList<>();
        SetUp.setUpDatabase();
        try {
            if (isCommand) {
                int count = DatabaseActions.getInstance().runCommand(queryOrCommandToRun);
                ReportUtility.reportInfo("Command : " + queryOrCommandToRun + " returned " + count);
                valueToReturn.add(count + " row(s) affected.");
            } else {
                valueToReturn = DatabaseActions.getInstance().runQuery(queryOrCommandToRun);
                if (SetUp.printDatabaseQueryInReport) {
                    ReportUtility.reportInfo("Query : " + queryOrCommandToRun + " returned value " + valueToReturn);
                }
            }
        } catch (Exception ex) {
            ReportUtility.reportInfo("Query/Command : " + queryOrCommandToRun + " threw exception : " + ex.getMessage());
            valueToReturn.add(ex.getMessage());
        }
        return valueToReturn;
    }

    public static List<String> getAllDocumentsFromMongoDBWithCriteria(String collectionName, String criteria) {
        if (SetUp.mongoDbConnectionString == null)
            return null;
        try {
            List<String> listOfDocuments = MongoDbActions.getInstance().getAllDocumentsFromMongoDBWithCriteria(collectionName, criteria);
            String reportWhere = (criteria == null) ? "" : " with the condition " + criteria;
            ReportUtility.reportInfo("Found " + listOfDocuments.size() + " documents in collection " + collectionName + reportWhere + ". Returned only the first document.");
            return listOfDocuments;
        } catch (Exception ex) {
            ReportUtility.reportExceptionFail(ex);
            return null;
        }
    }

    public static String getFirstDocumentFromMongoDBWithCriteria(String collectionName, String criteria) {
        List<String> documents = getAllDocumentsFromMongoDBWithCriteria(collectionName, criteria);
        return (documents == null || documents.size() == 0) ? null : documents.get(0);
    }

    public static int updateMongoDBForCollectionSetForCriteria(String collectionName, String setValue, String criteria) {
        return (mongoDbConnectionString == null) ? 0 : MongoDbActions.getInstance().updateMongoDBForCollectionSetForCriteria(collectionName, setValue, criteria);
    }

    public static int deleteInMongoDBForCollectionWithCriteria(String collectionName, String criteria) {
        return (mongoDbConnectionString == null) ? 0 : MongoDbActions.getInstance().deleteInMongoDBForCollectionWithCriteria(collectionName, criteria);
    }
}
