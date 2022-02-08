package com.orrish.automation.entrypoint;

import com.orrish.automation.utility.report.ReportUtility;

public class SuiteSetUp {

    public SuiteSetUp() {
    }

    public boolean reportPortalUrl(String url) {
        ReportUtility.setReportPortalUrl(url);
        return true;
    }

    public boolean reportPortalProject(String projectName) {
        ReportUtility.setReportPortalProject(projectName);
        return true;
    }

    public boolean reportPortalApiToken(String apiKey) {
        ReportUtility.setReportPortalApiToken(apiKey);
        return true;
    }

    public boolean launchReportPortalWithName(String name) {
        return ReportUtility.launchReportPortalReport(name);
    }
}
