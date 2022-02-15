package com.orrish.automation.entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.JsonContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.orrish.automation.utility.report.ReportUtility;
import com.orrish.automation.utility.verification.GeneralAndAPIVerifyAndReportUtility;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.*;

import static com.orrish.automation.entrypoint.GeneralSteps.getMapFromString;
import static com.orrish.automation.entrypoint.GeneralSteps.replaceStringWithIn;
import static com.orrish.automation.entrypoint.SetUp.jsonRequestTemplate;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class APISteps {

    protected Response apiResponse;
    protected String apiServerUrl = null;
    protected Map<String, String> apiRequestFormValues;
    protected Map<String, String> apiRequestHeaders = new HashMap<>();

    public boolean setServerEndpoint(String serverEndpoint) {
        this.apiServerUrl = serverEndpoint;
        return true;
    }

    public boolean setRequestHeaders(String headers) {
        this.apiRequestHeaders = getMapFromString(headers, "=");
        this.apiRequestHeaders.entrySet().removeIf(e -> e.getValue().trim().equalsIgnoreCase("doNotPass"));
        if (SetUp.defaultApiRequestHeaders != null)
            this.apiRequestHeaders.putAll(SetUp.defaultApiRequestHeaders);
        return true;
    }

    public boolean setFormValues(String values) {
        apiRequestFormValues = getMapFromString(values, ":");
        return true;
    }

    //This is to replace data in the json request template defined in setup.
    //## to replace all values
    //#4 will replace 4th occurrence
    //doNotPass will not pass this node
    //null will pass null
    public String replaceDataInJsonTemplateWith(String keyValueAsString) {
        Map mapFromString = getMapFromString(keyValueAsString, "=");
        return getModifiedAPIRequest(mapFromString, jsonRequestTemplate);
    }

    //This is a plain Java replace functionality to manipulate data for data driven testing
    public String replaceStringInJsonTemplateWith(String valueToFind, String valueToReplace) {
        return replaceStringWithIn(valueToFind, valueToReplace, jsonRequestTemplate);
    }

    public String getModifiedAPIRequest(Map<String, Object> keyValues, String requestAsString) {
        try {
            JsonNode node;
            try {
                node = new ObjectMapper().readTree(requestAsString);
            } catch (IOException e) {
                ReportUtility.reportFail("Request could not be generated : " + System.lineSeparator() + e.getMessage());
                return e.getMessage();
            }
            Set<String> keys = keyValues.keySet();
            for (String eachKey : keys) {
                Object eachMapEntry = keyValues.get(eachKey);
                if (eachKey.startsWith("##")) {
                    eachKey = eachKey.substring(2);
                    requestAsString = getJsonAfterReplacingValues(node, eachKey, eachMapEntry.toString(), -1).toString();
                } else if (eachKey.startsWith("#")) {
                    int index = Integer.parseInt(eachKey.substring(1, 2));
                    eachKey = eachKey.substring(2);
                    requestAsString = getJsonAfterReplacingValues(node, eachKey, eachMapEntry.toString(), index).toString();
                } else {
                    if (eachMapEntry != null) {
                        String valueToReplaceWith = eachMapEntry.toString().trim();
                        if (!valueToReplaceWith.equalsIgnoreCase("doNotPass")) {
                            Configuration configuration = Configuration.builder()
                                    .jsonProvider(new JacksonJsonNodeJsonProvider())
                                    .mappingProvider(new JacksonMappingProvider())
                                    .build();
                            DocumentContext documentContext = JsonPath.using(configuration).parse(node);
                            if (eachMapEntry.toString().trim().toLowerCase().contains("null")) {
                                documentContext.set(eachKey, null);
                                requestAsString = documentContext.jsonString();
                            } else
                                requestAsString = replaceWithCorrespondingType(documentContext, eachKey, eachMapEntry);
                        } else {
                            Configuration configuration = Configuration.builder()
                                    .jsonProvider(new JacksonJsonNodeJsonProvider())
                                    .mappingProvider(new JacksonMappingProvider())
                                    .build();
                            DocumentContext documentContext = JsonPath.using(configuration).parse(node);
                            documentContext.delete(eachKey);
                            requestAsString = documentContext.jsonString();
                        }
                    }
                }
            }
            return requestAsString;
        } catch (Exception ex) {
            ReportUtility.reportFail("Could not generate request. Please see report for details." + System.lineSeparator() + ex.getMessage());
            return "Could not generate request. Please see report for details.";
        }
    }

    public boolean callGETForEndpoint(String serverEndpoint) {
        setServerEndpoint(serverEndpoint);
        return callWithRequest("GET", null);
    }

    public boolean callGETWithRequest(String requestBody) {
        return callWithRequest("GET", requestBody);
    }

    public boolean callPOSTWithRequest(String requestBody) {
        if (requestBody != null) {
            requestBody = requestBody.replace("<pre>", "").replace("</pre>", "").replace("<br/>", "").trim();
            if (requestBody.startsWith("\n"))
                requestBody = requestBody.replaceFirst("\n", "").trim();
        }
        return callWithRequest("POST", requestBody);
    }

    public boolean callPOST() {
        return callWithRequest("POST", null);
    }

    public boolean callPUTWithRequest(String requestBody) {
        return callWithRequest("PUT", requestBody);
    }

    public boolean callDELETEForEndpoint(String serverEndpoint) {
        setServerEndpoint(serverEndpoint);
        return callWithRequest("DELETE", null);
    }

    public boolean callDELETEWithRequest(String requestBody) {
        return callWithRequest("DELETE", requestBody);
    }

    public boolean callWithRequestWithoutReporting(String type, String requestBody) {
        return callWithRequest(type.toUpperCase().trim(), requestBody, false);
    }

    public boolean callWithRequest(String type, String requestBody) {
        boolean value = callWithRequest(type, requestBody, true);
        ReportUtility.setReportPortalOverallTestResult(value);
        return value;
    }

    public String getResponseBody() {
        return apiResponse.getBody().asString();
    }

    public boolean verifyHTTPStatusCodeIs(int expectedStatusCode) {
        if (apiResponse == null) {
            ReportUtility.reportFail("No API response received in previous request. So, HTTP status code could not be verified.");
            return false;
        }
        int actualStatusCode = apiResponse.statusCode();
        if (actualStatusCode == expectedStatusCode) {
            ReportUtility.reportPass("Verified HTTP status code. It was " + expectedStatusCode);
            return true;
        }
        ReportUtility.reportFail("Expected status code: " + expectedStatusCode + " Actual status code:" + actualStatusCode);
        return false;
    }

    public String getFromResponse(String jsonPath) {
        String valueToReturn = jsonPath + " node not found. Ensure this jsonPath exists in last API response.";
        try {
            valueToReturn = getFromResponseWithoutReporting(jsonPath);
            ReportUtility.reportInfo(true, jsonPath + " value in API response is :" + valueToReturn);
        } catch (Exception ex) {
            ReportUtility.reportInfo(true, valueToReturn);
        }
        return valueToReturn;
    }

    public String getFromJsonString(String jsonPath, String jsonString) {
        String valueToReturn = jsonPath + " node not found. Ensure this jsonPath exists in API response.";
        try {
            Object actualValue = JsonPath.parse(jsonString).read(jsonPath);
            valueToReturn = getStringFromExtractedJsonValue(actualValue);
            ReportUtility.reportInfo(true, jsonPath + " value is :" + valueToReturn);
        } catch (Exception ex) {
            ReportUtility.reportInfo(true, valueToReturn);
        }
        return valueToReturn;
    }

    public String getFromResponseCookie(String cookieName) {
        return apiResponse.getCookie(cookieName);
    }

    public String getFromResponseWithoutReporting(String jsonPath) {
        if (apiResponse == null) {
            return "Last API response was null.";
        }
        String responseBodyString = apiResponse.getBody().asString();
        Object actualValue = (apiResponse.header("Content-Type").contains("xml"))
                ? io.restassured.path.xml.XmlPath.from(responseBodyString).get(jsonPath).toString()
                : JsonPath.parse(responseBodyString).read(jsonPath);
        return getStringFromExtractedJsonValue(actualValue);
    }

    private String getStringFromExtractedJsonValue(Object actualValue) {
        if (actualValue instanceof ArrayList) {
            int length = ((ArrayList<?>) actualValue).size();
            return (length == 1) ? ((ArrayList<?>) actualValue).get(0).toString() : "No unique result found. Total number of such nodes found are : " + length;
        }
        return actualValue.toString();
    }

    public boolean doesLastResponseMatchSchema(String schema) {
        try {
            apiResponse.then().assertThat().body(matchesJsonSchema(schema));
            ReportUtility.reportPass("Last API response conforms to the schema provided.");
            return true;
        } catch (Throwable ex) {
            ReportUtility.report(ReportUtility.REPORT_STATUS.FAIL, "Last API response does not match the expected schema.");
            ReportUtility.reportExceptionDebug(ex);
            return false;
        }
    }

    public boolean isResponseJsonEqualTo(String expectedResponseString) {
        return GeneralAndAPIVerifyAndReportUtility.verifyJsons(apiResponse, expectedResponseString);
    }

    public boolean verifyValues(String responseToVerify) {
        return GeneralAndAPIVerifyAndReportUtility.verifyValues(responseToVerify);
    }

    public boolean verifyResponseFor(String responseToVerify) {
        return GeneralAndAPIVerifyAndReportUtility.verifyResponseFor(apiResponse, responseToVerify);
    }

    public boolean verifyForInJsonString(String responseToVerify, String json) {
        return GeneralAndAPIVerifyAndReportUtility.verifyResponseStringFor(json, responseToVerify);
    }

    public boolean verifyObjectNodeCount(String node, String count) {
        return GeneralAndAPIVerifyAndReportUtility.verifyObjectNodeCount(apiResponse, node, count.trim());
    }

    public List getAllValuesOfInList(String exactNode, String parentNode) {
        if (parentNode == null || parentNode.trim().toLowerCase().contentEquals("null") || parentNode.trim().length() == 0) {
            parentNode = null;
        }
        List valueToReturn = new ArrayList();
        try {
            valueToReturn = getAllValuesOfInList(apiResponse, parentNode, exactNode);
            ReportUtility.report(ReportUtility.REPORT_STATUS.INFO, String.valueOf(valueToReturn));
        } catch (Exception ex) {
            ReportUtility.reportExceptionDebug(ex);
        }
        return valueToReturn;
    }

    private List getAllValuesOfInList(Response response, String parentNode, String jsonPath) {

        String responseBodyString = response.getBody().asString();
        Object nodeObject = JsonPath.parse(responseBodyString);
        nodeObject = (parentNode == null)
                ? nodeObject
                : JsonPath.parse(responseBodyString).read(parentNode);

        net.minidev.json.JSONArray array = (net.minidev.json.JSONArray) ((JsonContext) nodeObject).json();
        List<Object> valueToReturn = new ArrayList<>();
        array.forEach(e -> {
            Gson gson = new Gson();
            String json = gson.toJson(e);
            Object value = JsonPath.parse(json).read(jsonPath);
            valueToReturn.add(value);
        });
        return valueToReturn;
    }

    public boolean verifyInResponseIfElse(String jsonPath1, boolean condition, String jsonPath2) {
        if (condition)
            return verifyResponseFor(jsonPath1);
        return verifyResponseFor(jsonPath2);
    }

    public boolean verifyInResponseIf(String jsonPath, boolean condition) {
        if (condition)
            return verifyResponseFor(jsonPath);
        ReportUtility.reportInfo(jsonPath + " was not verified because it did not meet the condition.");
        return true;
    }

    public boolean doesResponseHave(String jsonPath) {
        try {
            getFromResponseWithoutReporting(jsonPath);
            return true;
        } catch (Exception ex) {
            ReportUtility.reportInfo(jsonPath + " node not found.");
            return false;
        }
    }

    public String getFromResponseHeader(String headerName) {
        if (apiResponse == null) {
            ReportUtility.reportInfo("Last API response was null.");
            return "Last API response was null.";
        }
        try {
            return apiResponse.header(headerName);
        } catch (Exception ex) {
            ReportUtility.reportInfo("Header " + headerName + " not found.");
            return "Header " + headerName + " not found.";
        }
    }

    private String replaceWithCorrespondingType(DocumentContext documentContext, String eachKey, Object eachMapValue) throws
            Exception {
        String nodeType;
        try {
            nodeType = documentContext.read(eachKey).getClass().getSimpleName();
        } catch (PathNotFoundException ex) {
            throw new Exception("Path " + eachKey + " not found in the json template.");
        }
        if (nodeType.contains("IntNode"))
            documentContext.set(eachKey, Integer.parseInt(eachMapValue.toString()));
        else if (nodeType.contains("BooleanNode"))
            documentContext.set(eachKey, Boolean.parseBoolean(eachMapValue.toString()));
        else if (nodeType.contains("DoubleNode"))
            documentContext.set(eachKey, Double.parseDouble(eachMapValue.toString()));
        else if (nodeType.contains("ArrayNode")) {
            JsonNodeType nodeSubType = ((ArrayNode) documentContext.read(eachKey)).get(0).getNodeType();
            if (nodeSubType == JsonNodeType.STRING)
                documentContext.add(eachKey, eachMapValue.toString());
            if (nodeSubType == JsonNodeType.BOOLEAN)
                documentContext.add(eachKey, Boolean.getBoolean(eachMapValue.toString()));
            if (nodeSubType == JsonNodeType.NUMBER)
                documentContext.add(eachKey, Integer.getInteger(eachMapValue.toString()));
        } else
            documentContext.set(eachKey, eachMapValue);
        return documentContext.jsonString();
    }

    private JsonNode getJsonAfterReplacingValues(JsonNode jsonNode, String keyToReplace, String replacingValue, int index) throws Exception {
        if (jsonNode.isObject())
            throw new Exception("You cannot use ## or #1 etc. for an object. It works on a list of objects.");
        int i = 0;
        for (JsonNode eachNode : jsonNode) {
            if (index == -1) {
                replaceValuesInNode(keyToReplace, replacingValue, (ObjectNode) eachNode);
            } else if (index == (i + 1)) {
                replaceValuesInNode(keyToReplace, replacingValue, (ObjectNode) eachNode);
                return jsonNode;
            }
            ++i;
        }
        return jsonNode;
    }

    private void replaceValuesInNode(String keyToReplace, String replaceValue, ObjectNode eachJsonNode) throws Exception {
        JsonNode value = eachJsonNode.get(keyToReplace);
        if (value == null) {
            throw new Exception(keyToReplace + " node does not exist in the json template.");
        }
        if (replaceValue.contains("doNotPass")) {
            eachJsonNode.remove(keyToReplace);
        } else {
            if (value.isNull()) {
                eachJsonNode.put(keyToReplace, NullNode.getInstance());
            } else if (value.isInt()) {
                eachJsonNode.put(keyToReplace, Integer.parseInt(replaceValue));
            } else if (value.isDouble()) {
                eachJsonNode.put(keyToReplace, Double.parseDouble(replaceValue));
            } else if (value.isBoolean()) {
                eachJsonNode.put(keyToReplace, Boolean.parseBoolean(replaceValue));
            } else if (value.isTextual()) {
                eachJsonNode.put(keyToReplace, replaceValue);
            }
        }
    }

    private boolean callWithRequest(String type, String requestBody, boolean shouldReport) {
        try {
            RestAssured.useRelaxedHTTPSValidation();
            ReportUtility.reportInfo(shouldReport, " === Preparing API call ===");
            Map<String, String> headers = getApiRequestHeaders();
            RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder().addHeaders(headers);
            String urlToSend = this.apiServerUrl.trim();
            String baseUrl = urlToSend.split("\\?")[0];
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
                Set<Map.Entry<String, String>> entries = apiRequestFormValues.entrySet();
                for (Map.Entry eachEntry : entries) {
                    requestSpecBuilder.addMultiPart(eachEntry.getKey().toString(), eachEntry.getValue().toString());
                }
                ReportUtility.reportJsonAsInfo(shouldReport, "Request form data values are : ", entries.toString());
            } else if (contentTypeHeader != null && contentTypeHeader.contains("application/x-www-form-urlencoded")) {
                Set<Map.Entry<String, String>> entries = apiRequestFormValues.entrySet();
                for (Map.Entry eachEntry : entries) {
                    requestSpecBuilder.addFormParam(eachEntry.getKey().toString(), eachEntry.getValue().toString());
                }
                ReportUtility.reportJsonAsInfo(shouldReport, "Request form data values are : ", entries.toString());
            }
            RequestSpecification requestSpecification = given().spec(requestSpecBuilder.build()).when();
            type = type.trim().toUpperCase();
            ReportUtility.reportInfo(shouldReport, "Sending " + type + " request to " + urlToSend);
            if (type.contentEquals("POST"))
                apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.post() : requestSpecification.post(urlToSend);
            else if (type.contentEquals("PUT"))
                apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.put() : requestSpecification.put(urlToSend);
            else if (type.contentEquals("GET"))
                apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.get() : requestSpecification.get(urlToSend);
            else if (type.contentEquals("DELETE"))
                apiResponse = baseUrl.equals(urlToSend) ? requestSpecification.delete() : requestSpecification.delete(urlToSend);
            resetHeaderAndEndpoint();
            if (apiResponse == null || apiResponse.getBody() == null) {
                ReportUtility.reportFail(shouldReport, "Did not get a valid apiResponse or valid apiResponse body.");
                return false;
            }
            String responseString = apiResponse.getBody().asString();
            String responseContentTypeHeader = apiResponse.getHeader("Content-Type");
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

    private boolean resetHeaderAndEndpoint() {
        this.apiServerUrl = null;
        this.apiRequestHeaders = null;
        this.apiRequestFormValues = null;
        return true;
    }

    private Map getApiRequestHeaders() {
        Map<String, String> headerToReturn = new HashMap<>();
        if (SetUp.defaultApiRequestHeaders != null)
            headerToReturn.putAll(SetUp.defaultApiRequestHeaders);
        if (this.apiRequestHeaders != null) {
            headerToReturn.putAll(this.apiRequestHeaders);
        }
        if (!headerToReturn.containsKey("Content-Type")) {
            headerToReturn.put("Content-Type", "application/json");
        }
        return headerToReturn;
    }

}
