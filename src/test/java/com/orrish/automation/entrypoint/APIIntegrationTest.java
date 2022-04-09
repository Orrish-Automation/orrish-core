package com.orrish.automation.entrypoint;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.testng.Assert.*;

public class APIIntegrationTest {

    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.reportEnabled(true);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    @Test
    public void allAPISteps() {

        APISteps apiSteps = new APISteps();
        GeneralSteps generalSteps = new GeneralSteps();

        assertTrue(apiSteps.setServerEndpoint("https://jsonplaceholder.typicode.com/users/2"));
        assertTrue(apiSteps.callGETWithRequest(getSampleRequestBody()));
        assertTrue(apiSteps.verifyResponseFor("name=Ervin Howell,website=anastasia.net,aaa=doNotVerify"));
        String responseBody = apiSteps.getResponseBody();
        assertTrue(apiSteps.isResponseJsonEqualTo(getExpectedResponseBody()));
        String nameFromGetResponse = apiSteps.getFromJsonString("name", responseBody);
        assertTrue(generalSteps.isEqual(nameFromGetResponse, "Ervin Howell"));

        assertTrue(apiSteps.callGETForEndpoint("https://postman-echo.com/get?a=1"));
        assertTrue(apiSteps.isHTTPStatusCode(200));
        String cookie = apiSteps.getFromResponseCookie("sails.sid");
        assertTrue(generalSteps.verifyIs(cookie, "not null"));
        String header = apiSteps.getFromResponseHeader("Content-Type");
        assertTrue(generalSteps.doesContain(header, "application/json"));

        assertTrue(apiSteps.setServerEndpoint("https://postman-echo.com/post"));
        assertTrue(apiSteps.setRequestHeaders("Content-Type=application/json,Accept=application/json"));
        assertTrue(apiSteps.callPOSTWithRequest(getSampleRequestBody()));
        assertTrue(apiSteps.verifyObjectNodeCount("data.contacts", "2"));
        String value = apiSteps.getFromResponse("data.user");
        assertTrue(generalSteps.verifyValues(value + "=test"));
        value = apiSteps.getFromResponseWithoutReporting("data.user");
        assertTrue(apiSteps.verifyForInJsonString("user=" + value, getSampleRequestBody()));

        assertTrue(apiSteps.setServerEndpoint("https://postman-echo.com/put"));
        assertTrue(apiSteps.setRequestHeaders("Content-Type=application/json,Accept=application/json"));
        assertTrue(apiSteps.callPUTWithRequest(getSampleRequestBody()));
        assertTrue(apiSteps.getExistenceOfNodeInResponse("data.user"));

        assertTrue(apiSteps.setServerEndpoint("https://postman-echo.com/delete"));
        assertTrue(apiSteps.setRequestHeaders("Content-Type=application/json,Accept=application/json"));
        assertTrue(apiSteps.callDELETEWithRequest(getSampleRequestBody()));

        assertTrue(apiSteps.setRequestHeaders("Content-Type=application/json,Accept=application/json"));
        assertTrue(apiSteps.callDELETEForEndpoint("https://postman-echo.com/delete"));
        assertFalse((apiSteps.getExistenceOfNodeInResponse("notPresent")));

        assertTrue(apiSteps.setServerEndpoint("https://postman-echo.com/delete"));
        assertTrue(apiSteps.callWithRequestWithoutReporting("DELETE", "{}"));

        assertTrue(apiSteps.setServerEndpoint("https://postman-echo.com/post"));
        assertTrue(apiSteps.setRequestHeaders("Content-Type=application/x-www-form-urlencoded;charset=UTF-8"));
        assertTrue(apiSteps.setFormParams("a=b"));
        assertTrue(apiSteps.callPOST());

        assertTrue(setUp.useRelaxedHTTPSValidation(true));
        assertTrue(apiSteps.setServerEndpoint("https://postman-echo.com/post"));
        assertTrue(apiSteps.setRequestHeaders("Content-Type=multipart/form-data"));
        HashMap<String, String> map = new HashMap();
        map.put("someKey", "someValue");
        map.put("file", "name=filename&path=filename.txt");
        assertTrue(apiSteps.setMultipartValues(map));
        assertTrue(apiSteps.callPOST());

        assertTrue(apiSteps.doesMatchSchema("[{\"id\":1,\"step\":\"|Set suite name|Some name|\",\"help\":\"\"}]", getJsonSchema()));
    }

    @Test
    public void replaceRequestTemplate() {
        setUp.jsonRequestTemplate(getSampleRequestBody());
        APISteps apiSteps = new APISteps();
        String modifiedRequestValue = apiSteps.replaceDataInJsonTemplateWith("user=hello,boolean=false,number=123,stringArray=123");
        assertEquals(modifiedRequestValue.replaceAll("\\s+", ""), getSampleRequestBody().replaceFirst("test", "hello").replaceAll("\\s+", ""));
        modifiedRequestValue = apiSteps.replaceDataInJsonTemplateWith("user=null");
        assertEquals(modifiedRequestValue.replaceAll("\\s+", ""), getSampleRequestBody().replaceFirst("\"test\"", "null").replaceAll("\\s+", ""));
        modifiedRequestValue = apiSteps.replaceDataInJsonTemplateWith("contacts.##street=Xyz Street");
        assertEquals(modifiedRequestValue.replaceAll("\\s+", ""), getSampleRequestBody().replaceAll("Abc Street", "Xyz Street").replaceAll("\\s+", ""));
        modifiedRequestValue = apiSteps.replaceDataInJsonTemplateWith("contacts.#1street=Xyz Street");
        assertEquals(modifiedRequestValue.replaceAll("\\s+", ""), getSampleRequestBody().replaceFirst("Abc Street", "Xyz Street").replaceAll("\\s+", ""));
        modifiedRequestValue = apiSteps.replaceDataInJsonTemplateWith("user=doNotPass");
        assertEquals(modifiedRequestValue.replaceAll("\\s+", ""), getSampleRequestBody().replaceFirst("\"user\":\"test\",", "").replaceAll("\\s+", ""));
        modifiedRequestValue = apiSteps.replaceStringInJsonTemplateWith("Abc", "Xyz");
        modifiedRequestValue = modifiedRequestValue.replaceAll("\\s+", "");
        String expected = getSampleRequestBody().replaceAll("Abc", "Xyz").replaceAll("\\s+", "");
        assertEquals(modifiedRequestValue, expected);
    }

    private static String getSampleRequestBody() {
        return "\n" +
                "{\n" +
                "    \"user\":\"test\",\n" +
                "    \"boolean\":false,\n" +
                "    \"number\":123,\n" +
                "    \"numberArray\":[123],\n" +
                "    \"booleanArray\":[false],\n" +
                "    \"stringArray\":[\"123\"],\n" +
                "    \"contacts\": [\n" +
                "      {\n" +
                "          \"type\":\"home\",\n" +
                "        \"street\": \"Abc Street\",\n" +
                "        \"zip\":12345,\n" +
                "        \"ord\": {\n" +
                "            \"lat\": 33.1234,\n" +
                "            \"long\": -22.1234\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "          \"type\":\"work\",\n" +
                "        \"street\": \"Abc Street\",\n" +
                "        \"zip\":12345,\n" +
                "        \"ord\": {\n" +
                "            \"lat\": 33.1234,\n" +
                "            \"long\": -22.1234\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "}";
    }

    private String getExpectedResponseBody() {
        return "{\n" +
                "  \"id\" : 2,\n" +
                "  \"name\" : \"Ervin Howell\",\n" +
                "  \"username\" : \"Antonette\",\n" +
                "  \"email\" : \"Shanna@melissa.tv\",\n" +
                "  \"address\" : {\n" +
                "    \"street\" : \"Victor Plains\",\n" +
                "    \"suite\" : \"Suite 879\",\n" +
                "    \"city\" : \"Wisokyburgh\",\n" +
                "    \"zipcode\" : \"90566-7771\",\n" +
                "    \"geo\" : {\n" +
                "      \"lat\" : \"-43.9509\",\n" +
                "      \"lng\" : \"-34.4618\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"phone\" : \"010-692-6593 x09125\",\n" +
                "  \"website\" : \"anastasia.net\",\n" +
                "  \"company\" : {\n" +
                "    \"name\" : \"Deckow-Crist\",\n" +
                "    \"catchPhrase\" : \"Proactive didactic contingency\",\n" +
                "    \"bs\" : \"synergize scalable supply-chains\"\n" +
                "  }\n" +
                "}";
    }

    private String getJsonSchema() {
        return "{\n" +
                "\"definitions\": {},\n" +
                "\"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                "\"$id\": \"https://example.com/object1646173832.json\",\n" +
                "\"title\": \"Root\",\n" +
                "\"type\": \"array\",\n" +
                "\"default\": [],\n" +
                "\"items\":{\n" +
                "\"$id\": \"#root/items\",\n" +
                "\"title\": \"Items\",\n" +
                "\"type\": \"object\",\n" +
                "\"required\": [\n" +
                "\"id\",\n" +
                "\"step\",\n" +
                "\"help\"\n" +
                "],\n" +
                "\"properties\": {\n" +
                "\"id\": {\n" +
                "\"$id\": \"#root/items/id\",\n" +
                "\"title\": \"Id\",\n" +
                "\"type\": \"integer\",\n" +
                "\"examples\": [\n" +
                "1\n" +
                "],\n" +
                "\"default\": 0\n" +
                "},\n" +
                "\"step\": {\n" +
                "\"$id\": \"#root/items/step\",\n" +
                "\"title\": \"Step\",\n" +
                "\"type\": \"string\",\n" +
                "\"default\": \"\",\n" +
                "\"examples\": [\n" +
                "\"|Launch app on device|\"\n" +
                "],\n" +
                "\"pattern\": \"^.*$\"\n" +
                "},\n" +
                "\"help\": {\n" +
                "\"$id\": \"#root/items/help\",\n" +
                "\"title\": \"Help\",\n" +
                "\"type\": \"string\",\n" +
                "\"default\": \"\",\n" +
                "\"examples\": [\n" +
                "\"\"\n" +
                "],\n" +
                "\"pattern\": \"^.*$\"\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "}";
    }

}
