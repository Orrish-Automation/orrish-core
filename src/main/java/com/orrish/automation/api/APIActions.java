package com.orrish.automation.api;

import com.orrish.automation.entrypoint.APISteps;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class APIActions {

    APISteps apiSteps;
    public RequestSpecification requestSpecification;

    public APIActions(APISteps apiSteps) {
        this.apiSteps = apiSteps;
    }

    public Map getApiRequestHeaders() {
        Map<String, String> headerToReturn = new HashMap<>();
        if (SetUp.defaultApiRequestHeaders != null)
            headerToReturn.putAll(SetUp.defaultApiRequestHeaders);
        if (apiSteps.apiRequestHeaders != null) {
            headerToReturn.putAll(apiSteps.apiRequestHeaders);
        }
        if (!headerToReturn.containsKey("Content-Type")) {
            headerToReturn.put("Content-Type", "application/json");
        }
        return headerToReturn;
    }

    public boolean resetHeaderAndEndpoint() {
        apiSteps.apiServerUrl = null;
        apiSteps.apiRequestHeaders = null;
        apiSteps.apiRequestFormValues = null;
        requestSpecification = null;
        return true;
    }

    public boolean callWithRequest(String type, String requestBody, boolean shouldReport) {
        try {
            ReportUtility.reportInfo(shouldReport, " === Preparing API call ===");
            Map<String, String> headers = getApiRequestHeaders();
            String urlToSend = apiSteps.apiServerUrl.trim();
            String baseUrl = urlToSend.split("\\?")[0];
            if (requestSpecification == null) {
                if (!urlToSend.startsWith("https")) {
                    RestAssured.useRelaxedHTTPSValidation();
                }
                RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder().addHeaders(headers);
                requestSpecBuilder = urlToSend.equals(baseUrl) ? requestSpecBuilder.setBaseUri(baseUrl) : requestSpecBuilder;
                ReportUtility.reportInfo(shouldReport, "Headers included in the request : " + headers);
                //Json request
                if (requestBody != null) {
                    requestSpecBuilder.setBody(requestBody);
                }
                String contentTypeHeader = headers.get("Content-Type");
                if (requestBody != null) {
                    if (contentTypeHeader != null && contentTypeHeader.contains("application/json")) {
                        ReportUtility.reportJsonAsInfo(shouldReport, "Request body : ", requestBody);
                    } else {
                        ReportUtility.reportMarkupAsInfo(shouldReport, "Request body : " + System.lineSeparator() + requestBody);
                    }
                }
                //Form data request
                if (contentTypeHeader != null && contentTypeHeader.contains("form-data")) {
                    Set<Map.Entry<String, String>> entries = apiSteps.apiRequestFormValues.entrySet();
                    for (Map.Entry eachEntry : entries) {
                        requestSpecBuilder.addMultiPart(eachEntry.getKey().toString(), eachEntry.getValue().toString());
                    }
                    ReportUtility.reportJsonAsInfo(shouldReport, "Request form data values are : ", entries.toString());
                } else if (contentTypeHeader != null && contentTypeHeader.contains("application/x-www-form-urlencoded")) {
                    Set<Map.Entry<String, String>> entries = apiSteps.apiRequestFormValues.entrySet();
                    for (Map.Entry eachEntry : entries) {
                        requestSpecBuilder.addFormParam(eachEntry.getKey().toString(), eachEntry.getValue().toString());
                    }
                    ReportUtility.reportJsonAsInfo(shouldReport, "Request form data values are : ", entries.toString());
                }
                requestSpecification = given().spec(requestSpecBuilder.build()).when();
            }
            type = type.trim().toUpperCase();
            ReportUtility.reportInfo(shouldReport, "Sending " + type + " request to " + urlToSend);
            if (type.contentEquals("POST"))
                apiSteps.apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.post() : requestSpecification.post(urlToSend);
            else if (type.contentEquals("PUT"))
                apiSteps.apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.put() : requestSpecification.put(urlToSend);
            else if (type.contentEquals("GET"))
                apiSteps.apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.get() : requestSpecification.get(urlToSend);
            else if (type.contentEquals("DELETE"))
                apiSteps.apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.delete() : requestSpecification.delete(urlToSend);
            resetHeaderAndEndpoint();
            if (apiSteps.apiResponse == null || apiSteps.apiResponse.getBody() == null) {
                ReportUtility.reportFail(shouldReport, "Did not get a valid apiResponse or valid apiResponse body.");
                return false;
            }
            String responseString = apiSteps.apiResponse.getBody().asString();
            Header header = apiSteps.apiResponse.getHeaders().get("Content-Type");
            String responseContentTypeHeader = (header == null) ? null : header.getName();
            if (shouldReport) {
                if (responseContentTypeHeader == null || responseContentTypeHeader.contains("text/xml")) {
                    ReportUtility.reportMarkupAsInfo(shouldReport, "Response body :" + System.lineSeparator() + responseString);
                } else {
                    ReportUtility.reportJsonAsInfo(shouldReport, "Response body :", responseString);
                }
            }
            return true;
        } catch (Exception ex) {
            ReportUtility.reportFail(shouldReport, "Call to the server failed. " + System.lineSeparator() + ex.getMessage());
            return false;
        } finally {
            ReportUtility.reportInfo(shouldReport, " === The API request is complete ===");
        }
    }
}
