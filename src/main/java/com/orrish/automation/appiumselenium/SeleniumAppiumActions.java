package com.orrish.automation.appiumselenium;

import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.report.UIStepReporter;
import org.openqa.selenium.UnhandledAlertException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.SetUp.isScreenshotAtEachStepEnabled;
import static com.orrish.automation.entrypoint.SetUp.stepCounter;
import static com.orrish.automation.utility.GeneralUtility.getMethodStyleStepName;

public class SeleniumAppiumActions {

    protected boolean isWebStepPassed = true;
    protected boolean isMobileStepPassed = true;
    protected SeleniumPageMethods seleniumPageMethods = SeleniumPageMethods.getInstance();
    protected AppiumPageMethods appiumPageMethods = AppiumPageMethods.getInstance();

    public boolean executeOnWebAndReturnBoolean(Object... args) {
        Object valueToReturn = executeOnWebAndReturnObject(args);
        return valueToReturn != null && Boolean.parseBoolean(valueToReturn.toString());
    }

    public String executeOnWebAndReturnString(Object... args) {
        Object valueToReturn = executeOnWebAndReturnObject(args);
        if (valueToReturn == null)
            return "";
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " returned " + valueToReturn);
        return valueToReturn.toString();
    }

    public boolean executeOnMobileAndReturnBoolean(Object... args) {
        Object valueToReturn = executeOnMobileAndReturnObject(args);
        return valueToReturn != null && Boolean.parseBoolean(valueToReturn.toString());
    }

    public String executeOnMobileAndReturnString(Object... args) {
        Object valueToReturn = executeOnMobileAndReturnObject(args);
        if (valueToReturn == null)
            return "";
        ReportUtility.reportInfo(getMethodStyleStepName(args) + " returned " + valueToReturn);
        return valueToReturn.toString();
    }


    protected Object executeOnWebAndReturnObject(Object... args) {
        String methodNamePassed = args[0].toString();
        if (!conditionalStep) return null;
        if (!isWebStepPassed) {
            ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
            return null;
        }
        try {
            int numberOfParametersPassed = args.length - 1;
            Method[] methods = seleniumPageMethods.getClass().getMethods();
            boolean methodFound = false;
            for (Method method : methods) {
                if (method.getName().equals(methodNamePassed)) {
                    methodFound = true;
                    Object valueToReturn = null;
                    if (numberOfParametersPassed == 0)
                        valueToReturn = getMethod(SeleniumPageMethods.class, method).invoke(seleniumPageMethods);
                    else if (numberOfParametersPassed == 1)
                        valueToReturn = getMethod(SeleniumPageMethods.class, method).invoke(seleniumPageMethods, args[1]);
                    else if (numberOfParametersPassed == 2)
                        valueToReturn = getMethod(SeleniumPageMethods.class, method).invoke(seleniumPageMethods, args[1], args[2]);
                    else if (numberOfParametersPassed == 3)
                        valueToReturn = getMethod(SeleniumPageMethods.class, method).invoke(seleniumPageMethods, args[1], args[2], args[3]);

                    String methodReturnType = method.getReturnType().getSimpleName();
                    if (!methodReturnType.equals("boolean")) {
                        return valueToReturn;
                    }
                    //Don't fail the test for accessibility because we may want to continue the test to go on even if accessibility check fails.
                    isWebStepPassed = method.getName().equals("checkAccessibilityForPage") ? isWebStepPassed : (boolean) valueToReturn;
                    break;
                }
            }
            if (!methodFound) {
                throw new Exception(methodNamePassed + " not implemented yet.");
            }
        } catch (Exception ex) {
            isWebStepPassed = false;
            Throwable throwable = (ex instanceof InvocationTargetException) ? ex.getCause() : ex;
            if (methodNamePassed.contains("launchBrowserAndNavigateTo") || methodNamePassed.contains("inBrowserNavigateTo")) {
                ReportUtility.reportFail("Could not navigate in the browser. Is your setup correct?");
                ReportUtility.reportExceptionFail(throwable);
            } else {
                if (ex instanceof UnhandledAlertException) {
                    ReportUtility.reportInfo("Could not take screenshot because of an open alert.");
                } else {
                    UIStepReporter UIStepReporter = new UIStepReporter(++stepCounter, args, throwable);
                    UIStepReporter.reportStepResultWithScreenshotAndException(ReportUtility.REPORT_STATUS.FAIL, seleniumPageMethods.webDriver);
                }
            }
            return false;
        }

        if (isWebStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isWebStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            UIStepReporter UIStepReporter = new UIStepReporter(++stepCounter, args, null);
            UIStepReporter.reportStepResultWithScreenshotAndException(status, seleniumPageMethods.webDriver);
        }
        return isWebStepPassed;
    }

    protected Object executeOnMobileAndReturnObject(Object... args) {
        if (!conditionalStep) return null;
        String methodNamePassed = args[0].toString();
        if (!isMobileStepPassed) {
            ReportUtility.reportInfo(getMethodStyleStepName(args) + " ignored due to last failure.");
            return null;
        }
        try {
            int numberOfParametersPassed = args.length - 1;
            Method[] methods = appiumPageMethods.getClass().getMethods();
            boolean methodFound = false;
            for (Method method : methods) {
                if (method.getName().equals(methodNamePassed)) {
                    methodFound = true;
                    Object valueToReturn = null;
                    if (numberOfParametersPassed == 0)
                        valueToReturn = getMethod(AppiumPageMethods.class, method).invoke(appiumPageMethods);
                    else if (numberOfParametersPassed == 1)
                        valueToReturn = getMethod(AppiumPageMethods.class, method).invoke(appiumPageMethods, args[1]);
                    else if (numberOfParametersPassed == 2)
                        valueToReturn = getMethod(AppiumPageMethods.class, method).invoke(appiumPageMethods, args[1], args[2]);
                    else if (numberOfParametersPassed == 3)
                        valueToReturn = getMethod(AppiumPageMethods.class, method).invoke(appiumPageMethods, args[1], args[2], args[3]);

                    String methodReturnType = method.getReturnType().getSimpleName();
                    if (!methodReturnType.equals("boolean")) {
                        return valueToReturn;
                    }
                    //Don't fail the test for accessibility because we may want to continue the test to go on even if accessibility check fails.
                    isMobileStepPassed = method.getName().equals("checkAccessibilityForPage") ? isMobileStepPassed : (boolean) valueToReturn;
                    break;
                }
            }
            if (!methodFound) {
                throw new Exception(methodNamePassed + " not implemented yet.");
            }
        } catch (Exception ex) {
            isMobileStepPassed = false;
            Throwable throwable = (ex instanceof InvocationTargetException) ? ex.getCause() : ex;
            if (methodNamePassed.contains("launchAppOnDevice")) {
                ReportUtility.reportFail("Could not launch app. Is your setup correct?");
                ReportUtility.reportExceptionFail(throwable);
            } else {
                UIStepReporter UIStepReporter = new UIStepReporter(++stepCounter, args, throwable);
                UIStepReporter.reportStepResultWithScreenshotAndException(ReportUtility.REPORT_STATUS.FAIL, appiumPageMethods.appiumDriver);
            }
            return false;
        }
        if (isMobileStepPassed && !isScreenshotAtEachStepEnabled)
            ReportUtility.reportPass(getMethodStyleStepName(args) + " performed successfully.");
        else {
            ReportUtility.REPORT_STATUS status = isMobileStepPassed ? ReportUtility.REPORT_STATUS.PASS : ReportUtility.REPORT_STATUS.FAIL;
            UIStepReporter UIStepReporter = new UIStepReporter(++stepCounter, args, null);
            UIStepReporter.reportStepResultWithScreenshotAndException(status, appiumPageMethods.appiumDriver);
        }
        return isMobileStepPassed;
    }

    private Method getMethod(Class clazz, Method method) throws NoSuchMethodException {
        int count = method.getParameters().length;
        if (count == 1)
            return clazz.getMethod(method.getName(), String.class);
        if (count == 2)
            return clazz.getMethod(method.getName(), String.class, String.class);
        if (count == 3)
            return clazz.getMethod(method.getName(), String.class, String.class, String.class);
        if (count == 4)
            return clazz.getMethod(method.getName(), String.class, String.class, String.class, String.class);
        if (count == 5)
            return clazz.getMethod(method.getName(), String.class, String.class, String.class, String.class, String.class);
        return method;
    }

}
