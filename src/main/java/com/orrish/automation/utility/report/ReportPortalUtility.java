package com.orrish.automation.utility.report;

import com.orrish.automation.utility.GeneralUtility;
import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.text.SimpleDateFormat;

import static io.restassured.path.json.JsonPath.from;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ReportPortalUtility {

    public static String reportPortalBaseUrl;
    public static String reportPortalProject;
    public static String reportPortalApiToken;
    public static REPORT_PORTAL_TEST_STATUS overallTestResult = REPORT_PORTAL_TEST_STATUS.passed;
    private static ReportPortalUtility reportPortalUtility;
    protected String topLevelSuiteItem;
    protected String currentSuiteItem;
    protected String testItem;
    protected String stepItem;
    String launchId;
    private ReportPortalUtility() {
    }

    public static ReportPortalUtility getInstance() {
        if (reportPortalUtility == null)
            reportPortalUtility = new ReportPortalUtility();
        return reportPortalUtility;
    }

    private static String escapeString(String message) {
        if (message.startsWith("\""))
            message = message.replaceFirst("\"", "");
        if (message.endsWith("\""))
            message = message.substring(0, message.length() - 1);
        message = message
                .replace("\"", "\\\"")
                .replace("\\\\", "\\");
        return message;
    }

    public boolean isReportPortalEnabled() {
        return (reportPortalBaseUrl != null && reportPortalProject != null && reportPortalApiToken != null
                && reportPortalBaseUrl.trim().startsWith("http") && reportPortalProject.trim().length() > 0 && reportPortalApiToken.trim().length() > 0);
    }

    public void resetSuiteLevel() {
        //If you created a sub suite, this will reset it to the top level suite.
        if (!currentSuiteItem.equals(topLevelSuiteItem)) {
            reportPortalFinishSuite();
            currentSuiteItem = topLevelSuiteItem;
        }
    }

    public void startLaunch(String launchName) {
        String responseBody = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "launch", "{\n" +
                "  \"name\": \"" + launchName + "\",\n" +
                "  \"startTime\": \"" + getCurrentTime() + "\"" +
                "}");
        launchId = getFromBody(responseBody);
    }

    public void reportPortalStartSuite(String suiteName) {
        resetSuiteLevel();
        String responseBodyString = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item",
                "{\n" +
                        "  \"name\": \"" + suiteName + "\",\n" +
                        "  \"startTime\": \"" + getCurrentTime() + "\",\n" +
                        "  \"type\": \"suite\",\n" +
                        "  \"launchUuid\": \"" + launchId + "\"\n" +
                        "}");
        currentSuiteItem = getFromBody(responseBodyString);
    }

    public void reportPortalStartTest(String testName) {
        if (currentSuiteItem == null) {
            reportPortalStartSuite("Suite Run");
            topLevelSuiteItem = currentSuiteItem;
        }
        String responseBody = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + currentSuiteItem, "{\n" +
                "  \"name\": \"" + testName + "\",\n" +
                "  \"startTime\": \"" + getCurrentTime() + "\",\n" +
                "  \"type\": \"test\",\n" +
                "  \"launchUuid\": \"" + launchId + "\"\n" +
                "}");
        testItem = getFromBody(responseBody);
    }

    //Optionally, you can create test step
    public String reportPortalStartTestStep(String stepName) {
        String responseBody = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + testItem, "{\n" +
                "  \"name\": \"" + stepName + "\",\n" +
                "  \"startTime\": \"" + getCurrentTime() + "\",\n" +
                "  \"type\": \"step\",\n" +
                "  \"launchUuid\": \"" + launchId + "\"\n" +
                "}");
        stepItem = getFromBody(responseBody);
        return stepItem;
    }

    //Optionally, if you created test step, remember to finish the step.
    public String reportPortalFinishTestStep(REPORT_PORTAL_TEST_STATUS status) {
        String responseBody = sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + stepItem,
                "{\n" +
                        "  \"endTime\": \"" + getCurrentTime() + "\",\n" +
                        "  \"status\": \"" + status + "\",\n" +
                        "  \"launchUuid\": \"" + launchId + "\"\n" +
                        "}");
        return getFromBody(responseBody);
    }

    //Status is optional if you created test steps earlier
    public void reportPortalFinishTest(REPORT_PORTAL_TEST_STATUS status) {
        String responseBody = sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + testItem,
                "{\n" +
                        "  \"endTime\": \"" + getCurrentTime() + "\",\n" +
                        "  \"status\": \"" + status + "\",\n" +
                        "  \"launchUuid\": \"" + launchId + "\"\n" +
                        "}");
        ReportPortalUtility.overallTestResult = ReportPortalUtility.REPORT_PORTAL_TEST_STATUS.passed;
        getFromBody(responseBody);
    }

    //Log steps here

    public void reportPortalFinishSuite() {
        String responseBody = sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + currentSuiteItem,
                "{\n" +
                        "  \"endTime\": \"" + getCurrentTime() + "\",\n" +
                        "  \"launchUuid\": \"" + launchId + "\"\n" +
                        "}");
        getFromBody(responseBody);
    }

    public void reportPortalFinishLaunch() {
        String responseBody = sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "launch/" + launchId + "/finish",
                "{\n" +
                        "  \"endTime\": \"" + getCurrentTime() + "\"\n" +
                        "}");
        getFromBody(responseBody);
    }

    public void reportPortalLogStep(REPORT_PORTAL_LOG_TYPE level, String message) {
        message = escapeString(message);
        String responseBody = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "log",
                "{\n" +
                        "  \"launchUuid\": \"" + launchId + "\",\n" +
                        "  \"itemUuid\": \"" + testItem + "\",\n" +
                        "  \"time\": \"" + getCurrentTime() + "\",\n" +
                        "  \"message\": \"" + message + "\",\n" +
                        "  \"level\": \"" + level + "\"\n" +
                        "}");
        getFromBody(responseBody);
    }

    private String getCurrentTime() {
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new java.util.Date());
    }

    public void reportPortalLogStepWithScreenshot(REPORT_PORTAL_LOG_TYPE level, String screenShotPath, String message) {

        String[] parts = screenShotPath.split("/");
        String fileName = parts[parts.length - 1];

        String url = reportPortalBaseUrl + reportPortalProject + "log";
        message = escapeString(message);
        String currentTime = getCurrentTime();
        String body = "[{ \"launchUuid\": \"" + launchId + "\"," +
                "  \"itemUuid\": \"" + testItem + "\"," +
                "  \"time\": \"" + currentTime + "\"," +
                "  \"message\": \"" + message + "\"," +
                "  \"level\": \"" + level.name().toLowerCase() + "\"," +
                "  \"file\":{\"name\":\"" + fileName + "\"}" +
                "}]";
        MultiPartSpecification multiPartSpecificationText = new MultiPartSpecBuilder(body)
                .with()
                .controlName("json_request_part")
                .mimeType(ContentType.JSON.toString())
                .charset(UTF_8)
                .build();
        MultiPartSpecification multiPartSpecificationScreenshot = new MultiPartSpecBuilder(new File(screenShotPath))
                .with()
                .fileName(fileName)
                .controlName("file")
                .mimeType("image/png")
                .build();
        RequestSpecification requestSpecification = RestAssured.given()
                .header("Authorization", "Bearer " + reportPortalApiToken)
                .contentType("multipart/form-data")
                .multiPart(multiPartSpecificationText)
                .multiPart(multiPartSpecificationScreenshot);
        try {
            Response response = requestSpecification.post(url);
            String responseBody = response.getBody().asString();
            getFromBody(responseBody);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void reportPortalLogStepWithJSON(REPORT_PORTAL_LOG_TYPE level, String message, String jsonBody) {

        File file = GeneralUtility.createFile("jsonBody.txt", jsonBody);
        String url = reportPortalBaseUrl + reportPortalProject + "log";
        String currentTime = getCurrentTime();
        String body = "[{ \"launchUuid\": \"" + launchId + "\"," +
                "  \"itemUuid\": \"" + testItem + "\"," +
                "  \"time\": \"" + currentTime + "\"," +
                "  \"message\": \"" + message + "\"," +
                "  \"level\": \"" + level.name().toLowerCase() + "\"," +
                "  \"file\":{\"name\":\"" + file.getName() + "\"}" +
                "}]";
        MultiPartSpecification multiPartSpecificationText = new MultiPartSpecBuilder(body)
                .with()
                .controlName("json_request_part")
                .mimeType(ContentType.JSON.toString())
                .charset(UTF_8)
                .build();
        MultiPartSpecification multiPartSpecificationJson = new MultiPartSpecBuilder(file)
                .with()
                .fileName(file.getName())
                .controlName("file")
                .mimeType(ContentType.JSON.toString())
                .build();
        RequestSpecification requestSpecification = RestAssured.given()
                .header("Authorization", "Bearer " + reportPortalApiToken)
                .contentType("multipart/form-data")
                .multiPart(multiPartSpecificationText)
                .multiPart(multiPartSpecificationJson);
        try {
            Response response = requestSpecification.post(url);
            String responseBody = response.getBody().asString();
            getFromBody(responseBody);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (file.exists())
                file.delete();
        }
    }

    private String sendPOSTToReportPortalServer(String url, String body) {
        return sendRequestToServer(body, url, "post");
    }

    private String sendPUTToReportPortalServer(String url, String body) {
        return sendRequestToServer(body, url, "put");
    }

    private String sendRequestToServer(String body, String url, String type) {
        RequestSpecification requestSpecification = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + reportPortalApiToken)
                .body(body);
        Response response = type.toLowerCase().contains("put") ? requestSpecification.put(url) : requestSpecification.post(url);
        return response.getBody().asString();
    }

    private String getFromBody(String responseBodyString) {
        try {
            return from(responseBodyString).get("id").toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public enum REPORT_PORTAL_LOG_TYPE {
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

    public enum REPORT_PORTAL_TEST_STATUS {
        passed,
        failed,
        stopped,
        skipped,
        interrupted,
        cancelled
    }
}
