package com.orrish.automation.api;

import com.orrish.automation.entrypoint.APISteps;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import java.io.File;
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
        return (apiSteps.apiRequestHeaders == null || apiSteps.apiRequestHeaders.size() == 0)
                ? SetUp.defaultApiRequestHeaders
                : apiSteps.apiRequestHeaders;
    }

    public Map getApiRequestCookies() {
        return apiSteps.apiRequestCookies;
    }

    public boolean resetRequestHeaderAndEndpoint() {
        apiSteps.apiServerUrl = null;
        apiSteps.apiRequestHeaders.clear();
        apiSteps.apiRequestCookies.clear();
        apiSteps.apiRequestFormParams = null;
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
                if (SetUp.useRelaxedHTTPSValidation) {
                    RestAssured.useRelaxedHTTPSValidation();
                }
                RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder().addHeaders(headers).addCookies(getApiRequestCookies());
                requestSpecBuilder = urlToSend.equals(baseUrl) ? requestSpecBuilder.setBaseUri(baseUrl) : requestSpecBuilder;
                if (headers.size() > 0)
                    ReportUtility.reportInfo(shouldReport, "Headers included in the request : " + headers);
                if (getApiRequestCookies().size() > 0)
                    ReportUtility.reportInfo(shouldReport, "Cookies included in the request : " + getApiRequestCookies());
                //Json request
                if (requestBody != null) {
                    requestSpecBuilder.setBody(requestBody);
                }
                String contentTypeHeader = headers.get("Content-Type");
                if (requestBody != null && contentTypeHeader != null) {
                    if (contentTypeHeader.contains("application/json")) {
                        ReportUtility.reportJsonAsInfo(shouldReport, "Request body : ", requestBody);
                    } else {
                        ReportUtility.reportMarkupAsInfo(shouldReport, "Request body : " + System.lineSeparator() + requestBody);
                    }
                }
                //Form data request
                if (contentTypeHeader != null && (apiSteps.apiRequestFormParams != null || apiSteps.apiRequestMultipartValues != null)) {
                    if (contentTypeHeader.contains("x-www-form-urlencoded")) {
                        requestSpecBuilder.setUrlEncodingEnabled(true).addFormParams(apiSteps.apiRequestFormParams);
                        ReportUtility.reportJsonAsInfo(shouldReport, "Request form data values are : ", apiSteps.apiRequestFormParams.entrySet().toString());
                    } else if (contentTypeHeader.contains("multipart")) {
                        Set<Map.Entry<String, Object>> entries = apiSteps.apiRequestMultipartValues.entrySet();
                        for (Map.Entry eachEntry : entries) {
                            if (eachEntry.getKey().toString().contains("file")) {
                                Map<String, String> mapValues = (Map<String, String>) eachEntry.getValue();
                                if (mapValues.get("name") != null) {
                                    requestSpecBuilder.addMultiPart(mapValues.get("name"), new File(mapValues.get("path")));
                                } else {
                                    requestSpecBuilder.addMultiPart(new File(mapValues.get("path")));
                                }
                            } else {
                                requestSpecBuilder.addMultiPart(eachEntry.getKey().toString(), eachEntry.getValue().toString());
                            }
                        }
                        ReportUtility.reportJsonAsInfo(shouldReport, "Request form data values are : ", apiSteps.apiRequestMultipartValues.entrySet().toString());
                    }
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
            else if (type.contentEquals("HEAD"))
                apiSteps.apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.head() : requestSpecification.head(urlToSend);
            resetRequestHeaderAndEndpoint();
            if (apiSteps.apiResponse == null || apiSteps.apiResponse.getBody() == null) {
                ReportUtility.reportFail(shouldReport, "Did not get a valid apiResponse or valid apiResponse body.");
                return false;
            }
            String responseString = apiSteps.apiResponse.getBody().asString();
            Header header = apiSteps.apiResponse.getHeaders().get("Content-Type");
            if (shouldReport && header != null) {
                String responseContentTypeHeader = header.getValue();
                if (responseContentTypeHeader.contains("text/xml")) {
                    ReportUtility.reportMarkupAsInfo(true, "Response body :" + System.lineSeparator() + responseString);
                } else if (responseContentTypeHeader.contains("application/json")) {
                    ReportUtility.reportJsonAsInfo(true, "Response body :", responseString);
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
