package com.orrish.automation.entrypoint;

import com.orrish.automation.utility.report.ReportUtility;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedList;

import static com.orrish.automation.entrypoint.ReportSteps.*;
import static com.orrish.automation.entrypoint.SetUp.*;

public class TearDown {

    public TearDown() {
        try {
            stepCounter = 0;
            if (webDriver != null && webDriver.getSessionId() != null) {
                try {
                    webDriver.quit();
                    webDriver = null;
                } catch (Exception ex) {
                    ReportUtility.reportExceptionDebug(ex);
                }
            }
            try {
                if (playwrightPage != null && !playwrightPage.isClosed()) {
                    playwrightPage.close();
                    savePlaywrightVideoIfEnabled();
                }
                if (playwright != null) {
                    playwright.close();
                }
            } catch (Exception ex) {
                ReportUtility.reportExceptionDebug(ex);
            }
            if (appiumDriver != null && appiumDriver.getSessionId() != null) {
                try {
                    appiumDriver.quit();
                    appiumDriver = null;
                } catch (Exception ex) {
                    ReportUtility.reportExceptionDebug(ex);
                }
            }

            testList.forEach(individualTest -> {
                String testNameFromModel = individualTest.getModel().getName().replaceAll("[^A-Za-z0-9 ]", "");
                String result = individualTest.getModel().getStatus().toString();
                if (testResults.get(result) == null) {
                    LinkedList<String> newList = new LinkedList<>();
                    newList.addFirst(testNameFromModel);
                    testResults.put(result, newList);
                } else
                    testResults.get(result).addLast(testNameFromModel);
            });
            testList.clear();
            childNode = currentTest = null;
            suiteCreated = false;
        } finally {
            ReportUtility.updateReport();
        }
    }

    private void savePlaywrightVideoIfEnabled() {
        if (SetUp.isVideoRecordingEnabled()) {
            String browserVersion = (SetUp.browserVersion != null && SetUp.browserVersion.trim().length() > 0) ? "_" + SetUp.browserVersion : "";
            String parentFolder = String.valueOf(playwrightPage.video().path().getParent());
            String extension = FilenameUtils.getExtension(playwrightPage.video().path().getFileName().toString());
            String testName = getCurrentTestName().replace(" ", "");
            String videoName = testName + "_" + SetUp.browser + browserVersion + "." + extension;
            playwrightPage.video().saveAs(Paths.get(parentFolder + File.separator + videoName));
            playwrightPage.video().delete();
        }
    }

}
