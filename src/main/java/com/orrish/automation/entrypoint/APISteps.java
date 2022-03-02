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
import com.orrish.automation.api.APIActions;
import com.orrish.automation.utility.report.ReportUtility;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.*;

import static com.orrish.automation.entrypoint.GeneralSteps.conditionalStep;
import static com.orrish.automation.entrypoint.SetUp.jsonRequestTemplate;
import static com.orrish.automation.utility.GeneralUtility.getMapFromString;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class APISteps {

    public Response apiResponse;
    public String apiServerUrl = null;
    public Map<String, String> apiRequestFormValues;
    public Map<String, String> apiRequestHeaders = new HashMap<>();

    public APIActions apiActions;

    public boolean setServerEndpoint(String serverEndpoint) {
        if (!conditionalStep) return true;
        this.apiServerUrl = serverEndpoint;
        return true;
    }

    public boolean setRequestHeaders(String headers) {
        if (!conditionalStep) return true;
        this.apiRequestHeaders = getMapFromString(headers, "=");
        this.apiRequestHeaders.entrySet().removeIf(e -> e.getValue().trim().equalsIgnoreCase("doNotPass"));
        if (SetUp.defaultApiRequestHeaders != null)
            this.apiRequestHeaders.putAll(SetUp.defaultApiRequestHeaders);
        return true;
    }

    public boolean setFormValues(String values) {
        if (!conditionalStep) return true;
        apiRequestFormValues = getMapFromString(values, ":");
        return true;
    }

    //This is to replace data in the json request template defined in setup.
    //## to replace all values
    //#4 will replace 4th occurrence
    //doNotPass will not pass this node
    //null will pass null
    public String replaceDataInJsonTemplateWith(String keyValueAsString) {
        if (!conditionalStep) return "";
        Map mapFromString = getMapFromString(keyValueAsString, "=");
        return getModifiedAPIRequest(mapFromString, jsonRequestTemplate);
    }

    //This is a plain Java replace functionality to manipulate data for data driven testing
    public String replaceStringInJsonTemplateWith(String valueToFind, String valueToReplace) {
        return GeneralSteps.replaceStringWithIn(valueToFind, valueToReplace, jsonRequestTemplate);
    }

    private String getModifiedAPIRequest(Map<String, Object> keyValues, String requestAsString) {
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
        if (!conditionalStep) return true;
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
        if (!conditionalStep) return true;
        setServerEndpoint(serverEndpoint);
        return callWithRequest("DELETE", null);
    }

    public boolean callDELETEWithRequest(String requestBody) {
        return callWithRequest("DELETE", requestBody);
    }

    public boolean callWithRequestWithoutReporting(String type, String requestBody) {
        return getApiActions().callWithRequest(type.toUpperCase().trim(), requestBody, false);
    }

    private APIActions getApiActions() {
        return apiActions = (apiActions == null) ? new APIActions(this) : apiActions;
    }

    public boolean callWithRequest(String type, String requestBody) {
        if (!conditionalStep) return true;
        boolean value = getApiActions().callWithRequest(type, requestBody, true);
        ReportUtility.setReportPortalOverallTestResult(value);
        return value;
    }

    public String getResponseBody() {
        if (!conditionalStep) return "";
        return apiResponse.getBody().asString();
    }

    public boolean verifyHTTPStatusCodeIs(int expectedStatusCode) {
        if (!conditionalStep) return true;
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
        if (!conditionalStep) return "";
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
        if (!conditionalStep) return "";
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
        if (!conditionalStep) return "";
        return apiResponse.getCookie(cookieName);
    }

    public String getFromResponseWithoutReporting(String jsonPath) {
        if (!conditionalStep) return "";
        if (apiResponse == null) {
            return "Last API response was null.";
        }
        String responseBodyString = apiResponse.getBody().asString();
        Header contentTypeHeader = apiResponse.headers().get("Content-Type");
        Object actualValue = (contentTypeHeader != null && contentTypeHeader.getName().contains("xml"))
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
        if (!conditionalStep) return true;
        return validateAgainstSchema(apiResponse, schema);
    }

    public boolean doesMatchSchema(String json, String schema) {
        if (!conditionalStep) return true;
        Response response = new ResponseBuilder().setStatusCode(200).setBody(json).build();
        return validateAgainstSchema(response, schema);
    }

    private boolean validateAgainstSchema(Response response, String schema) {
        try {
            response.then().assertThat().body(matchesJsonSchema(schema));
            ReportUtility.reportPass("Body conforms to the schema provided.");
            return true;
        } catch (Throwable ex) {
            ReportUtility.report(ReportUtility.REPORT_STATUS.FAIL, "Body does not match the expected schema.");
            ReportUtility.reportExceptionDebug(ex);
            return false;
        }
    }

    public boolean isResponseJsonEqualTo(String expectedResponseString) {
        return GeneralSteps.verifyJsons(apiResponse, expectedResponseString);
    }

    public boolean verifyValues(String responseToVerify) {
        return GeneralSteps.verifyValues(responseToVerify);
    }

    public boolean verifyResponseFor(String responseToVerify) {
        return GeneralSteps.verifyResponseFor(apiResponse, responseToVerify);
    }

    public boolean verifyForInJsonString(String responseToVerify, String json) {
        return GeneralSteps.verifyResponseStringFor(json, responseToVerify);
    }

    public boolean verifyObjectNodeCount(String node, String count) {
        return GeneralSteps.verifyObjectNodeCount(apiResponse, node, count.trim());
    }

    public List getAllValuesOfInList(String exactNode, String parentNode) {
        if (!conditionalStep) return new ArrayList();
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

    public boolean doesResponseHave(String jsonPath) {
        if (!conditionalStep) return true;
        try {
            getFromResponseWithoutReporting(jsonPath);
            return true;
        } catch (Exception ex) {
            ReportUtility.reportInfo(jsonPath + " node not found.");
            return false;
        }
    }

    public String getFromResponseHeader(String headerName) {
        if (!conditionalStep) return "";
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

}
