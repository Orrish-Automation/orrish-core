package com.orrish.automation.model;

import java.util.HashMap;
import java.util.Map;

public class VerificationResultModel {
    private final boolean result;
    private String resultStringToBePrinted;
    private Map<Integer, VerificationResultModel> multiVerificationResult = new HashMap<>();

    public VerificationResultModel(boolean result, String resultStringToBePrintedPassed) {
        this.result = result;
        this.resultStringToBePrinted = resultStringToBePrintedPassed;
    }

    public VerificationResultModel(boolean result, Map multiVerificationResult) {
        this.result = result;
        this.multiVerificationResult = multiVerificationResult;
    }

    public boolean getOverallResult() {
        return this.result;
    }

    public String getVerificationResultString() {
        return this.resultStringToBePrinted;
    }

    public Map getMultiStepResult() {
        return this.multiVerificationResult;
    }
}
