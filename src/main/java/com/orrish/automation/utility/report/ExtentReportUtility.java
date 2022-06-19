package com.orrish.automation.utility.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Test;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Page;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.playwright.PlaywrightActions;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.orrish.automation.entrypoint.ReportSteps.getCurrentExtentTest;
import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;

public class ExtentReportUtility {

    private static ExtentReports extentReports;
    private static String reportPath = "FitNesseRoot" + File.separator + "files";
    private static String screenshotFolderPath = "";
    private static String reportFileName = "extent.html";
    private static ExtentHtmlReporter htmlReporter;

    private ExtentReportUtility() {
    }

    protected static ExtentReports getInstance() {
        if (extentReports == null) {
            String fullReportPath = reportPath.trim().length() == 0
                    ? reportFileName
                    : reportPath + File.separator + reportFileName;
            screenshotFolderPath = reportPath + File.separator + "screenshots";
            createInstance(fullReportPath);
        }
        return extentReports;
    }

    protected static void setExtentReportLocation(String path) {
        reportPath = path.contains(File.separator) ? StringUtils.substringBeforeLast(path, File.separator) : "";
        screenshotFolderPath = reportPath + File.separator + "screenshots";
        reportFileName = path.contains(File.separator) ? StringUtils.substringAfterLast(path, File.separator) : path;
    }

    private static void createInstance(String fileName) {
        if (Files.notExists(Paths.get(screenshotFolderPath))) {
            try {
                Files.createDirectories(Paths.get(screenshotFolderPath));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        htmlReporter = new ExtentHtmlReporter(fileName);
        htmlReporter.config().setDocumentTitle("Automation Report");
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName("Test Report");
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setChartVisibilityOnOpen(false);
        htmlReporter.config().setTheme(Theme.STANDARD);
        try {
            extentReports = new ExtentReports();
            extentReports.attachReporter(htmlReporter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static void report(Status type, String message) {
        getCurrentExtentTest().log(type, message);
    }

    protected static void reportInExtent(Status type, String message) {
        getCurrentExtentTest().log(type, message);
    }

    protected static void reportJsonInExtent(String message, String valueToBeJsonFormatted) {
        reportInExtent(Status.INFO, message + getFormattedJson(valueToBeJsonFormatted));
    }

    protected static void reportException(Status type, Throwable throwable) {
        getCurrentExtentTest().log(type, throwable);
    }

    protected static void reportWithMarkUp(Status type, String text) {
        Markup markupText = MarkupHelper.createCodeBlock(text);
        getCurrentExtentTest().log(type, markupText);
    }

    protected static String reportWithScreenshot(RemoteWebDriver driver, String fileName, Status type, String message) {
        if (driver == null && !PlaywrightActions.getInstance().isPlaywrightRunning())
            return fileName;
        try {
            String fullFileName = screenshotFolderPath + File.separator + fileName;
            File destinationFile = new File(fullFileName + ".png");
            if (destinationFile.exists())
                destinationFile.delete();
            if (driver == null) {
                if (PlaywrightActions.getInstance().isPlaywrightRunning()) {
                    Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions()
                            .setPath(Paths.get(fullFileName + ".png"));
                    if (SetUp.fullPageScreenshot)
                        screenshotOptions.setFullPage(true);
                    PlaywrightActions.getInstance().getPlaywrightPage().screenshot(screenshotOptions);
                } else
                    return "";
            } else {
                byte[] srcBytes = driver.getScreenshotAs(OutputType.BYTES);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(srcBytes));
                ImageIO.write(bufferedImage, "png", destinationFile);
            }
            try {
                getCurrentExtentTest().log(type, message, MediaEntityBuilder.createScreenCaptureFromPath("screenshots" + File.separator + fileName + ".png").build());
                fileName = destinationFile.getAbsolutePath();
            } catch (UncheckedIOException ex) {
                if (!ex.getMessage().contains("The system cannot find the file specified."))
                    throw ex;
            }
        } catch (Exception ex) {
            getCurrentExtentTest().info("Could not take screenshot.");
            getCurrentExtentTest().info(ex.toString());
            return fileName;
        }
        getInstance().flush();
        return fileName;
    }

    protected static void reportWithImage(String fileName, Status type, String message) {
        try {
            getCurrentExtentTest().log(type, message, MediaEntityBuilder.createScreenCaptureFromPath(fileName).build());
        } catch (Exception ex) {
            getCurrentExtentTest().fail("Could not report image.");
            getCurrentExtentTest().info(ex.toString());
        }
    }

    protected static void reportPassInExtent(Object[] stepNamesPassed, boolean shouldTakeScreenshotAtEachStep, RemoteWebDriver driver, int stepCounter) {
        String stepNameWithParameters = getMethodStyleStepName(stepNamesPassed);
        String extentMessage = stepNameWithParameters + " performed successfully.";
        if (shouldTakeScreenshotAtEachStep && (driver != null || PlaywrightActions.getInstance().isPlaywrightRunning())) {
            String testName = getCurrentExtentTest().getModel().getName().replace(" ", "");
            String screenshotName = testName + "_Step" + stepCounter;
            reportWithScreenshot(driver, screenshotName, Status.PASS, extentMessage);
        } else
            report(Status.PASS, extentMessage);
    }

    protected static void reportFailInExtent(String message) {
        report(Status.FAIL, message);
    }

    private static void flushReport() {
        getInstance().flush();
    }

    protected static Map<Status, List<String>> getTestResult() {
        Map<Status, List<String>> results = new HashMap<>();
        List<Test> tests = htmlReporter.getTestList();
        for (Test eachTest : tests) {
            if (eachTest.getNodeContext().size() > 0) {
                Iterator<Test> subNodes = eachTest.getNodeContext().getIterator();
                while (subNodes.hasNext()) {
                    eachTest = subNodes.next();
                    updateTestStatus(results, eachTest);
                }
            } else {
                updateTestStatus(results, eachTest);
            }
        }
        return results;
    }

    private static void updateTestStatus(Map<Status, List<String>> results, Test eachTest) {
        Status status = eachTest.getStatus();
        List<String> listOfTests = results.get(status) == null ? new ArrayList<>() : results.get(status);
        listOfTests.add(eachTest.getName());
        results.put(status, listOfTests);
    }

    private static String getFormattedJson(String body) {
        try {
            Object json = new ObjectMapper().readValue(body, Object.class);
            body = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
        }
        return "</br><pre>" + body + "</pre>";
    }

}
