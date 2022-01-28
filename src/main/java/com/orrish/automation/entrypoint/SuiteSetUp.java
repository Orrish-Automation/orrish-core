package com.orrish.automation.entrypoint;

import com.orrish.automation.utility.report.ReportPortalUtility;
import com.orrish.automation.utility.report.ReportUtility;

public class SuiteSetUp {

    public SuiteSetUp() {
    }

    public boolean reportPortalUrl(String url) {
        ReportPortalUtility.reportPortalBaseUrl = url;
        return true;
    }

    public boolean reportPortalProject(String projectName) {
        ReportPortalUtility.reportPortalProject = projectName;
        return true;
    }

    public boolean reportPortalApiToken(String apiKey) {
        ReportPortalUtility.reportPortalApiToken = apiKey;
        return true;
    }

    public boolean launchReportPortalWithName(String name) {
        return ReportUtility.launchReportPortalReport(name);
    }
}
