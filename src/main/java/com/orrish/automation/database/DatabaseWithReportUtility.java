package com.orrish.automation.database;

import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.orrish.automation.entrypoint.SetUp.mongoDbConnectionString;

public class DatabaseWithReportUtility {

    public boolean isDbQueryResultIn(String query, List expectedValue) {
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

    public String runDBQueryAndGetCell(String query) {
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

    public String runCommand(String command) {
        return String.valueOf(runQueryOrCommand(command, true).get(0));
    }

    public List runQueryOrCommand(String queryOrCommandToRun, boolean isCommand) {
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

    public List<String> getAllDocumentsFromMongoDBWithCriteria(String collectionName, String criteria) {
        if (SetUp.mongoDbConnectionString == null)
            return null;
        try {
            List<String> listOfDocuments = MongoDbActions.getInstance().getAllDocumentsFromMongoDBWithCriteria(collectionName, criteria);
            String reportWhere = (criteria == null) ? "" : " with condition " + criteria;
            ReportUtility.reportInfo("Found " + listOfDocuments.size() + " documents in collection " + collectionName + reportWhere + ".");
            return listOfDocuments;
        } catch (Exception ex) {
            ReportUtility.reportExceptionFail(ex);
            return new ArrayList<>();
        }
    }

    public String getFirstDocumentFromMongoDBWithCriteria(String collectionName, String criteria) {
        List<String> documents = getAllDocumentsFromMongoDBWithCriteria(collectionName, criteria);
        if (documents.size() > 1)
            ReportUtility.reportInfo("Returning only the first document out of " + documents.size() + " documents.");
        return (documents == null || documents.size() == 0) ? "" : documents.get(0);
    }

    public int updateMongoDBForCollectionSetForCriteria(String collectionName, String setValue, String criteria) {
        return (mongoDbConnectionString == null) ? 0 : MongoDbActions.getInstance().updateMongoDBForCollectionSetForCriteria(collectionName, setValue, criteria);
    }

    public int deleteInMongoDBForCollectionWithCriteria(String collectionName, String criteria) {
        return (mongoDbConnectionString == null) ? 0 : MongoDbActions.getInstance().deleteInMongoDBForCollectionWithCriteria(collectionName, criteria);
    }

    public boolean insertMongoDBDocumentInCollection(String document, String collectionName) {
        boolean valueToReturn = false;
        try {
            valueToReturn = MongoDbActions.getInstance().insertDocumentInCollection(document, collectionName);
        } catch (Throwable ex) {
            ReportUtility.reportExceptionFail(ex);
        }
        if (valueToReturn) {
            ReportUtility.reportPass("Inserted document in MongoDB.");
        } else {
            ReportUtility.reportFail("Could not insert document in MongoDB");
        }
        return valueToReturn;
    }

    public boolean createCollectionInMongoDB(String collectionName) {
        return actionOnMongoDBCollection(collectionName, "create");
    }

    public boolean dropCollectionInMongoDB(String collectionName) {
        return actionOnMongoDBCollection(collectionName, "drop");
    }

    private boolean actionOnMongoDBCollection(String collectionName, String action) {
        boolean valueToReturn = false;
        try {
            if (action.contains("create"))
                valueToReturn = MongoDbActions.getInstance().createCollection(collectionName);
            else if (action.contains("drop"))
                valueToReturn = MongoDbActions.getInstance().dropCollection(collectionName);
        } catch (Exception ex) {
            ReportUtility.reportExceptionFail(ex);
        }
        if (valueToReturn) {
            ReportUtility.reportPass(action + "ed collection " + collectionName + " in MongoDB.");
        } else {
            ReportUtility.reportFail("Could not " + action + " collection " + collectionName + " in MongoDB");
        }
        return valueToReturn;
    }

}
