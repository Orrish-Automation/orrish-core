package com.orrish.automation.utility.report;

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.orrish.automation.entrypoint.GeneralSteps.createFileWithContent;
import static io.restassured.path.json.JsonPath.from;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ReportPortalUtility {

    protected static String reportPortalBaseUrl;
    protected static String reportPortalProject;
    protected static String reportPortalApiToken;
    protected static REPORT_PORTAL_TEST_STATUS overallTestResult = REPORT_PORTAL_TEST_STATUS.passed;
    protected String topLevelSuiteItem;
    protected String currentSuiteItem;
    protected String testItem;
    protected String stepItem;
    String launchId;

    private ReportPortalUtility() {
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    //Bill Pugh
    private static class InstanceHelper {
        private static final ReportPortalUtility reportPortalUtility = new ReportPortalUtility();
    }

    protected static ReportPortalUtility getInstance() {
        return InstanceHelper.reportPortalUtility;
    }

    private String escapeString(String message) {
        if (message.startsWith("\""))
            message = message.replaceFirst("\"", "");
        if (message.endsWith("\""))
            message = message.substring(0, message.length() - 1);
        message = message
                .replace("\"", "\\\"")
                .replace("\\\\", "\\");
        return message;
    }

    protected boolean isReportPortalEnabled() {
        return (reportPortalBaseUrl != null && reportPortalProject != null && reportPortalApiToken != null
                && reportPortalBaseUrl.trim().startsWith("http") && reportPortalProject.trim().length() > 0 && reportPortalApiToken.trim().length() > 0);
    }

    protected void resetSuiteLevel() {
        //If you created a sub suite, this will reset it to the top level suite.
        if (currentSuiteItem != null && topLevelSuiteItem != null && !currentSuiteItem.equals(topLevelSuiteItem)) {
            reportPortalFinishSuite();
            currentSuiteItem = topLevelSuiteItem;
        }
    }

    protected void startLaunch(String launchName) {
        executorService.submit(() -> {
            String responseBody = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "launch", "{\n" +
                    "  \"name\": \"" + launchName + "\",\n" +
                    "  \"startTime\": \"" + getCurrentTime() + "\"" +
                    "}");
            launchId = getFromBody(responseBody);
        });
    }

    protected void reportPortalStartSuite(String suiteName) {
        executorService.submit(() -> {
            resetSuiteLevel();
            String responseBodyString = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item",
                    "{\n" +
                            "  \"name\": \"" + suiteName + "\",\n" +
                            "  \"startTime\": \"" + getCurrentTime() + "\",\n" +
                            "  \"type\": \"suite\",\n" +
                            "  \"launchUuid\": \"" + launchId + "\"\n" +
                            "}");
            currentSuiteItem = getFromBody(responseBodyString);
        });
    }

    protected void reportPortalStartTest(String testName) {
        executorService.submit(() -> {
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
        });
    }

    //Optionally, you can create test step
    protected void reportPortalStartTestStep(String stepName) {
        executorService.submit(() -> {
            String responseBody = sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + testItem, "{\n" +
                    "  \"name\": \"" + stepName + "\",\n" +
                    "  \"startTime\": \"" + getCurrentTime() + "\",\n" +
                    "  \"type\": \"step\",\n" +
                    "  \"launchUuid\": \"" + launchId + "\"\n" +
                    "}");
            stepItem = getFromBody(responseBody);
        });
    }

    //Optionally, if you created test step, remember to finish the step.
    protected void reportPortalFinishTestStep(REPORT_PORTAL_TEST_STATUS status) {
        executorService.submit(() -> {
            sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + stepItem,
                    "{\n" +
                            "  \"endTime\": \"" + getCurrentTime() + "\",\n" +
                            "  \"status\": \"" + status + "\",\n" +
                            "  \"launchUuid\": \"" + launchId + "\"\n" +
                            "}");
        });
    }

    //Status is optional if you created test steps earlier
    protected void reportPortalFinishTest(REPORT_PORTAL_TEST_STATUS status) {
        executorService.submit(() -> {
            sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + testItem,
                    "{\n" +
                            "  \"endTime\": \"" + getCurrentTime() + "\",\n" +
                            "  \"status\": \"" + status + "\",\n" +
                            "  \"launchUuid\": \"" + launchId + "\"\n" +
                            "}");
            overallTestResult = ReportPortalUtility.REPORT_PORTAL_TEST_STATUS.passed;
        });
    }

    //Log steps here
    protected void reportPortalFinishSuite() {
        executorService.submit(() -> {
            sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "item/" + currentSuiteItem,
                    "{\n" +
                            "  \"endTime\": \"" + getCurrentTime() + "\",\n" +
                            "  \"launchUuid\": \"" + launchId + "\"\n" +
                            "}");
        });
    }

    protected void reportPortalFinishLaunch() {
        executorService.submit(() -> {
            sendPUTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "launch/" + launchId + "/finish",
                    "{\n" +
                            "  \"endTime\": \"" + getCurrentTime() + "\"\n" +
                            "}");
        });
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void reportPortalLogStep(REPORT_PORTAL_LOG_TYPE level, String message) {
        executorService.submit(() -> {
            sendPOSTToReportPortalServer(reportPortalBaseUrl + reportPortalProject + "log",
                    "{\n" +
                            "  \"launchUuid\": \"" + launchId + "\",\n" +
                            "  \"itemUuid\": \"" + testItem + "\",\n" +
                            "  \"time\": \"" + getCurrentTime() + "\",\n" +
                            "  \"message\": \"" + escapeString(message) + "\",\n" +
                            "  \"level\": \"" + level + "\"\n" +
                            "}");
        });
    }

    private String getCurrentTime() {
        String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new java.util.Date());
    }

    protected void reportPortalLogStepWithScreenshot(REPORT_PORTAL_LOG_TYPE level, String screenShotPath, String message) {
        executorService.submit(() -> {
            String[] parts = screenShotPath.split("/");
            String fileName = parts[parts.length - 1];

            String url = reportPortalBaseUrl + reportPortalProject + "log";
            String currentTime = getCurrentTime();
            String body = "[{ \"launchUuid\": \"" + launchId + "\"," +
                    "  \"itemUuid\": \"" + testItem + "\"," +
                    "  \"time\": \"" + currentTime + "\"," +
                    "  \"message\": \"" + escapeString(message) + "\"," +
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
        });
    }

    protected void reportPortalLogStepWithJSON(REPORT_PORTAL_LOG_TYPE level, String message, String jsonBody) {
        executorService.submit(() -> {
            File file = createFileWithContent("jsonBody.txt", jsonBody);
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
        });
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

    protected enum REPORT_PORTAL_LOG_TYPE {
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE
    }

    protected enum REPORT_PORTAL_TEST_STATUS {
        passed,
        failed,
        stopped,
        skipped,
        interrupted,
        cancelled
    }

}
