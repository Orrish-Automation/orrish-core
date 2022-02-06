package com.orrish.automation.entrypoint;

import com.orrish.automation.database.DatabaseService;
import com.orrish.automation.utility.report.ReportUtility;

import java.util.HashMap;
import java.util.Map;

import static com.orrish.automation.utility.GeneralUtility.getMapFromString;

public class SetUp {

    //API
    public static Map<String, String> defaultApiRequestHeaders = new HashMap<>();
    public static String jsonRequestTemplate;//Template defined which can be replaced with different data in the test case.

    //Database
    public static boolean databaseCheck = true;
    public static boolean printDatabaseQueryInReport = true;
    public static String databaseConnectionString;
    public static String databaseUserName;
    public static String databasePassword;

    public static String mongoDbConnectionString;
    public static String mongoDatabaseName;

    //Mobile and Web
    public static boolean isScreenshotAtEachStepEnabled;
    public static int stepCounter = 0;
    public static Map<String, String> executionCapabilities = new HashMap<>();
    public static int screenshotDelayInSeconds = 0;

    //Web only - common
    public static String browser = "CHROME";
    public static int browserWidth = 0;
    public static int browserHeight = 0;
    //Web only - Selenium
    public static String seleniumGridURL;
    public static String browserVersion;
    //Web only - Playwright
    public static boolean isPlaywrightHeadless;

    //Mobile - Common to iOS and Android
    public static String appiumServerURL;
    public static String automationName;
    public static String platformName;
    public static String platformVersion;
    public static String deviceName;
    public static int defaultWaitTime = 10;

    //Mobile - iOS specific
    public static String app;
    public static String xcodeOrgId;
    public static String xcodeSigningId;
    public static String udid;
    //Mobile - Android specific
    public static String APP_PACKAGE;
    public static String APP_ACTIVITY;

    //General
    public static boolean showPageInfoOnFailure = false;

    public static boolean isVideoRecordingEnabled() {
        return executionCapabilities.containsKey("enableVideo") && executionCapabilities.get("enableVideo").toLowerCase().contains("true");
    }

    public boolean extentReportLocation(String location) {
        ReportUtility.setExtentReportLocation(location);
        return true;
    }

    public boolean showPageInfoOnFailure(boolean value) {
        showPageInfoOnFailure = value;
        return true;
    }

    public String echo(String value) {
        return value;
    }

    public boolean setSuiteName(String suiteName) {
        return ReportSteps.setSuiteName(suiteName);
    }

    public boolean defaultRequestHeaders(String headers) {
        defaultApiRequestHeaders = getMapFromString(headers, "=");
        return true;
    }

    public boolean jsonRequestTemplate(String templatePassed) {
        jsonRequestTemplate = templatePassed;
        return true;
    }

    public boolean printDatabaseQueryInReport(boolean shouldReport) {
        printDatabaseQueryInReport = shouldReport;
        return true;
    }

    //Database
    public boolean databaseCheck(String databaseCheckPassed) {
        databaseCheckPassed = databaseCheckPassed.toLowerCase();
        databaseCheck = databaseCheckPassed.contains("true") || databaseCheckPassed.contains("yes");
        return true;
    }

    public boolean mongodbConnectionString(String connectionStringPassed) {
        mongoDbConnectionString = connectionStringPassed;
        return true;
    }

    public boolean mongodbDatabaseName(String mongodbName) {
        mongoDatabaseName = mongodbName;
        return true;
    }

    public boolean databaseConnectionString(String dbConnectionString) {
        databaseConnectionString = dbConnectionString;
        return true;
    }

    public boolean databaseUsername(String dbUsername) {
        databaseUserName = dbUsername;
        return true;
    }

    public boolean databasePassword(String dbPassword) {
        databasePassword = dbPassword;
        return true;
    }

    //Common to web and mobile
    public boolean executionCapabilities(String values) {
        executionCapabilities.putAll(getMapFromString(values, "="));
        return true;
    }

    public boolean takeScreenshotAtEachStep(boolean takeScreenshotAtEachStep) {
        isScreenshotAtEachStepEnabled = takeScreenshotAtEachStep;
        return true;
    }

    public boolean defaultWaitTimeInSeconds(int waitTime) {
        defaultWaitTime = waitTime;
        return true;
    }

    public boolean screenshotDelayInSeconds(int valuePassed) {
        screenshotDelayInSeconds = valuePassed;
        return true;
    }

    //Web only
    public boolean seleniumGridURL(String url) {
        seleniumGridURL = url;
        return true;
    }

    public boolean browser(String browserPassed) {
        browser = browserPassed;
        return true;
    }

    public boolean browserVersion(String versionPassed) {
        browserVersion = versionPassed;
        return true;
    }

    public boolean browserDimension(String valuePassed) {
        valuePassed = valuePassed.toUpperCase();
        try {
            if (valuePassed.contains("X")) {
                browserWidth = Integer.parseInt(valuePassed.split("X")[0]);
                browserHeight = Integer.parseInt(valuePassed.split("X")[1]);
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean playwrightHeadless(boolean isHeadless) {
        isPlaywrightHeadless = isHeadless;
        return true;
    }

    //Mobile only
    public boolean appiumServerURL(String url) {
        appiumServerURL = url;
        return true;
    }

    public boolean platformName(String platform) {
        platformName = platform;
        return true;
    }

    public boolean platformVersion(String platformVersionPassed) {
        platformVersion = platformVersionPassed;
        return true;
    }

    public boolean deviceName(String deviceNamePassed) {
        deviceName = deviceNamePassed;
        return true;
    }

    public boolean automationName(String automationNamePassed) {
        automationName = automationNamePassed;
        return true;
    }

    public boolean app(String appPassed) {
        app = appPassed;
        return true;
    }

    public boolean appPackage(String packageName) {
        APP_PACKAGE = packageName;
        return true;
    }

    public boolean appActivity(String appActivity) {
        APP_ACTIVITY = appActivity;
        return true;
    }

    public boolean udid(String udidPassed) {
        udid = udidPassed;
        return true;
    }

    public boolean xcodeOrgId(String xcodeOrgIdPassed) {
        xcodeOrgId = xcodeOrgIdPassed;
        return true;
    }

    public boolean xcodeSigningId(String xcodeSigningIdPassed) {
        xcodeSigningId = xcodeSigningIdPassed;
        return true;
    }

    public static void setUpDatabase() {
        if (databaseCheck) {
            DatabaseService databaseService = DatabaseService.getInstance();
            if (!databaseService.getConnectionString().contains(databaseConnectionString)) {
                databaseService.reassignDataSource();
            }
        }
    }

}
