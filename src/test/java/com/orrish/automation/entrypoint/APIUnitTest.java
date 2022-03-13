package com.orrish.automation.entrypoint;

import com.orrish.automation.api.APIActions;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class APIUnitTest {
    SetUp setUp;

    @BeforeMethod
    public void beforeMethod() {
        setUp = new SetUp();
        setUp.reportEnabled(false);
    }

    @AfterMethod
    public void afterMethod() {
        new TearDown();
    }

    @Test
    public void sampleTest() {

    }

    @Test
    public void testJsonTemplateExactTextReplace() {
        setUp.jsonRequestTemplate("[{\"id\":1,\"name\":\"Leanne Graham\",\"username\":\"Bret\",\"email\":\"Sincere@april.biz\",\"address\":{\"street\":\"Kulas Light\",\"suite\":\"Apt. 556\",\"city\":\"Gwenborough\",\"zipcode\":\"92998-3874\",\"geo\":{\"lat\":\"-37.3159\",\"lng\":\"81.1496\"}},\"phone\":\"1-770-736-8031 x56442\",\"website\":\"hildegard.org\",\"company\":{\"name\":\"Romaguera-Crona\",\"catchPhrase\":\"Multi-layered client-server neural-net\",\"bs\":\"harness real-time e-markets\"}},{\"id\":2,\"name\":\"orrish\",\"username\":\"Antonette\",\"email\":\"Shanna@melissa.tv\",\"address\":{\"street\":\"Victor Plains\",\"suite\":\"Suite 879\",\"city\":\"Wisokyburgh\",\"zipcode\":\"90566-7771\",\"geo\":{\"lat\":\"-43.9509\",\"lng\":\"-34.4618\"}},\"phone\":\"010-692-6593 x09125\",\"website\":\"anastasia.net\",\"company\":{\"name\":\"Deckow-Crist\",\"catchPhrase\":\"Proactive didactic contingency\",\"bs\":\"synergize scalable supply-chains\"}}]");
        APISteps apiSteps = new APISteps();
        String value = apiSteps.replaceStringInJsonTemplateWith("hildegard.org", "orrish");
        assertEquals(value, "[{\"id\":1,\"name\":\"Leanne Graham\",\"username\":\"Bret\",\"email\":\"Sincere@april.biz\",\"address\":{\"street\":\"Kulas Light\",\"suite\":\"Apt. 556\",\"city\":\"Gwenborough\",\"zipcode\":\"92998-3874\",\"geo\":{\"lat\":\"-37.3159\",\"lng\":\"81.1496\"}},\"phone\":\"1-770-736-8031 x56442\",\"website\":\"orrish\",\"company\":{\"name\":\"Romaguera-Crona\",\"catchPhrase\":\"Multi-layered client-server neural-net\",\"bs\":\"harness real-time e-markets\"}},{\"id\":2,\"name\":\"orrish\",\"username\":\"Antonette\",\"email\":\"Shanna@melissa.tv\",\"address\":{\"street\":\"Victor Plains\",\"suite\":\"Suite 879\",\"city\":\"Wisokyburgh\",\"zipcode\":\"90566-7771\",\"geo\":{\"lat\":\"-43.9509\",\"lng\":\"-34.4618\"}},\"phone\":\"010-692-6593 x09125\",\"website\":\"anastasia.net\",\"company\":{\"name\":\"Deckow-Crist\",\"catchPhrase\":\"Proactive didactic contingency\",\"bs\":\"synergize scalable supply-chains\"}}]");
    }

    @Test
    public void testJsonTemplateDataReplace() {
        setUp.jsonRequestTemplate(getSampleUsersData());
        APISteps apiSteps = new APISteps();
        String value = apiSteps.replaceDataInJsonTemplateWith("##name=orrish");
        assertEquals(value, "[{\"id\":1,\"name\":\"orrish\",\"username\":\"Bret\",\"email\":\"Sincere@april.biz\",\"address\":{\"street\":\"Kulas Light\",\"suite\":\"Apt. 556\",\"city\":\"Gwenborough\",\"zipcode\":\"92998-3874\",\"geo\":{\"lat\":\"-37.3159\",\"lng\":\"81.1496\"}},\"phone\":\"1-770-736-8031 x56442\",\"website\":\"hildegard.org\",\"company\":{\"name\":\"Romaguera-Crona\",\"catchPhrase\":\"Multi-layered client-server neural-net\",\"bs\":\"harness real-time e-markets\"}},{\"id\":2,\"name\":\"orrish\",\"username\":\"Antonette\",\"email\":\"Shanna@melissa.tv\",\"address\":{\"street\":\"Victor Plains\",\"suite\":\"Suite 879\",\"city\":\"Wisokyburgh\",\"zipcode\":\"90566-7771\",\"geo\":{\"lat\":\"-43.9509\",\"lng\":\"-34.4618\"}},\"phone\":\"010-692-6593 x09125\",\"website\":\"anastasia.net\",\"company\":{\"name\":\"Deckow-Crist\",\"catchPhrase\":\"Proactive didactic contingency\",\"bs\":\"synergize scalable supply-chains\"}}]");

        value = apiSteps.replaceDataInJsonTemplateWith("#2name=orrish");
        assertEquals(value, "[{\"id\":1,\"name\":\"Leanne Graham\",\"username\":\"Bret\",\"email\":\"Sincere@april.biz\",\"address\":{\"street\":\"Kulas Light\",\"suite\":\"Apt. 556\",\"city\":\"Gwenborough\",\"zipcode\":\"92998-3874\",\"geo\":{\"lat\":\"-37.3159\",\"lng\":\"81.1496\"}},\"phone\":\"1-770-736-8031 x56442\",\"website\":\"hildegard.org\",\"company\":{\"name\":\"Romaguera-Crona\",\"catchPhrase\":\"Multi-layered client-server neural-net\",\"bs\":\"harness real-time e-markets\"}},{\"id\":2,\"name\":\"orrish\",\"username\":\"Antonette\",\"email\":\"Shanna@melissa.tv\",\"address\":{\"street\":\"Victor Plains\",\"suite\":\"Suite 879\",\"city\":\"Wisokyburgh\",\"zipcode\":\"90566-7771\",\"geo\":{\"lat\":\"-43.9509\",\"lng\":\"-34.4618\"}},\"phone\":\"010-692-6593 x09125\",\"website\":\"anastasia.net\",\"company\":{\"name\":\"Deckow-Crist\",\"catchPhrase\":\"Proactive didactic contingency\",\"bs\":\"synergize scalable supply-chains\"}}]");

        value = apiSteps.replaceDataInJsonTemplateWith("#1name=doNotPass");
        assertEquals(value, "[{\"id\":1,\"username\":\"Bret\",\"email\":\"Sincere@april.biz\",\"address\":{\"street\":\"Kulas Light\",\"suite\":\"Apt. 556\",\"city\":\"Gwenborough\",\"zipcode\":\"92998-3874\",\"geo\":{\"lat\":\"-37.3159\",\"lng\":\"81.1496\"}},\"phone\":\"1-770-736-8031 x56442\",\"website\":\"hildegard.org\",\"company\":{\"name\":\"Romaguera-Crona\",\"catchPhrase\":\"Multi-layered client-server neural-net\",\"bs\":\"harness real-time e-markets\"}},{\"id\":2,\"name\":\"Ervin Howell\",\"username\":\"Antonette\",\"email\":\"Shanna@melissa.tv\",\"address\":{\"street\":\"Victor Plains\",\"suite\":\"Suite 879\",\"city\":\"Wisokyburgh\",\"zipcode\":\"90566-7771\",\"geo\":{\"lat\":\"-43.9509\",\"lng\":\"-34.4618\"}},\"phone\":\"010-692-6593 x09125\",\"website\":\"anastasia.net\",\"company\":{\"name\":\"Deckow-Crist\",\"catchPhrase\":\"Proactive didactic contingency\",\"bs\":\"synergize scalable supply-chains\"}}]");
    }

    @Test
    public void endpointAndHeaderTest() {
        APISteps apiSteps = new APISteps();

        assertTrue(apiSteps.setServerEndpoint("http://test"));
        assertEquals("http://test", apiSteps.apiServerUrl);

        assertTrue(apiSteps.setRequestHeaders("key1=value1,key2=doNotPass"));
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("key1", "value1");
        assertEquals(expectedHeaders, apiSteps.apiRequestHeaders);

        expectedHeaders.clear();
        setUp.defaultRequestHeaders("setUpHeaderKey=setUpHeaderValue");
        assertTrue(apiSteps.setRequestHeaders("key1=value1"));
        expectedHeaders.put("setUpHeaderKey", "setUpHeaderValue");
        expectedHeaders.put("key1", "value1");
        assertEquals(expectedHeaders, apiSteps.apiRequestHeaders);

        expectedHeaders.clear();
        expectedHeaders.put("key1", "value1");
        assertTrue(apiSteps.setFormValues("key1:value1"));
        assertEquals(expectedHeaders, apiSteps.apiRequestFormValues);
    }

    @Test
    public void getCall() {

        APISteps apiSteps = new APISteps();
        apiSteps.apiActions = new APIActions(apiSteps);
        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        when(apiSteps.apiActions.requestSpecification.get()).thenReturn(new ResponseBuilder().setStatusCode(200).setBody("{\"name\":\"Leanne Graha\"}").build());

        assertTrue(apiSteps.callGETForEndpoint("https://jsonplaceholder.typicode.com/users/1"));

        assertTrue(apiSteps.isHTTPStatusCode(200));
        assertTrue(apiSteps.getExistenceOfNodeInResponse("name"));
        String name = apiSteps.getFromResponse("name");
        assertEquals(name, "Leanne Graha");

        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        Cookie cookie = new Cookie.Builder("cookie", "cookieValue").setComment("comment").build();
        when(apiSteps.apiActions.requestSpecification.get()).thenReturn(new ResponseBuilder().setStatusCode(400).setBody("{\"error\":\"Some error\"}").setCookies(new Cookies(cookie)).build());
        apiSteps.setServerEndpoint("https://jsonplaceholder.typicode.com/users/1");
        assertTrue(apiSteps.callGETWithRequest("https://jsonplaceholder.typicode.com/users/1"));
        assertTrue(apiSteps.isHTTPStatusCode(400));
        assertTrue(apiSteps.getExistenceOfNodeInResponse("error"));
        name = apiSteps.getFromResponse("error");
        assertEquals(name, "Some error");
        assertEquals(apiSteps.getResponseBody(), "{\"error\":\"Some error\"}");
        assertEquals(apiSteps.getFromResponseCookie("cookie"), "cookieValue");

    }

    @Test
    public void callPOST() {
        APISteps apiSteps = new APISteps();
        apiSteps.apiActions = new APIActions(apiSteps);
        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        when(apiSteps.apiActions.requestSpecification.post()).thenReturn(new ResponseBuilder().setStatusCode(200).setBody("{\"name\":\"Leanne Graha\"}").build());

        assertFalse(apiSteps.callPOST());
        assertFalse(apiSteps.isHTTPStatusCode(200));
        assertEquals(apiSteps.getFromResponse("name"), "Last API response was null.");

        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        when(apiSteps.apiActions.requestSpecification.post()).thenReturn(new ResponseBuilder().setStatusCode(200).setBody("{\"name\":\"Some name\"}").build());
        apiSteps.setServerEndpoint("https://jsonplaceholder.typicode.com/users/1");
        assertTrue(apiSteps.callPOSTWithRequest("{}"));
        assertTrue(apiSteps.isHTTPStatusCode(200));
        assertTrue(apiSteps.getExistenceOfNodeInResponse("name"));
        String name = apiSteps.getFromResponse("name");
        assertEquals(name, "Some name");

    }

    @Test
    void callDelete() {
        APISteps apiSteps = new APISteps();
        apiSteps.apiActions = new APIActions(apiSteps);
        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        when(apiSteps.apiActions.requestSpecification.delete()).thenReturn(new ResponseBuilder().setStatusCode(200).setBody("{\"name\":\"Leanne Graha\"}").build());

        assertFalse(apiSteps.callDELETEWithRequest("{}"));

        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        when(apiSteps.apiActions.requestSpecification.delete()).thenReturn(new ResponseBuilder().setStatusCode(200).setBody("{\"name\":\"Some name\"}").build());
        apiSteps.setServerEndpoint("https://jsonplaceholder.typicode.com/users/1");
        assertTrue(apiSteps.callDELETEWithRequest("{}"));
        assertTrue(apiSteps.isHTTPStatusCode(200));
        assertTrue(apiSteps.getExistenceOfNodeInResponse("name"));
        String name = apiSteps.getFromResponse("name");
        assertEquals(name, "Some name");
        assertEquals(apiSteps.getFromJsonString("name", apiSteps.getResponseBody()), "Some name");
    }

    @Test
    public void callPut() {

        APISteps apiSteps = new APISteps();
        apiSteps.apiActions = new APIActions(apiSteps);
        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        when(apiSteps.apiActions.requestSpecification.put()).thenReturn(new ResponseBuilder().setStatusCode(200).setBody("{\"name\":\"Leanne Graha\"}").build());

        assertFalse(apiSteps.callPUTWithRequest("{}"));

        apiSteps.apiActions.requestSpecification = mock(RequestSpecification.class);
        when(apiSteps.apiActions.requestSpecification.put()).thenReturn(new ResponseBuilder().setStatusCode(200).setBody("{\"name\":\"Some name\"}").build());
        apiSteps.setServerEndpoint("https://jsonplaceholder.typicode.com/users/1");
        assertTrue(apiSteps.callPUTWithRequest("{}"));
        assertTrue(apiSteps.isHTTPStatusCode(200));
        assertTrue(apiSteps.getExistenceOfNodeInResponse("name"));
        String name = apiSteps.getFromResponse("name");
        assertEquals(name, "Some name");

    }

    private String getSampleUsersData() {
        return "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Leanne Graham\",\n" +
                "    \"username\": \"Bret\",\n" +
                "    \"email\": \"Sincere@april.biz\",\n" +
                "    \"address\": {\n" +
                "      \"street\": \"Kulas Light\",\n" +
                "      \"suite\": \"Apt. 556\",\n" +
                "      \"city\": \"Gwenborough\",\n" +
                "      \"zipcode\": \"92998-3874\",\n" +
                "      \"geo\": {\n" +
                "        \"lat\": \"-37.3159\",\n" +
                "        \"lng\": \"81.1496\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"phone\": \"1-770-736-8031 x56442\",\n" +
                "    \"website\": \"hildegard.org\",\n" +
                "    \"company\": {\n" +
                "      \"name\": \"Romaguera-Crona\",\n" +
                "      \"catchPhrase\": \"Multi-layered client-server neural-net\",\n" +
                "      \"bs\": \"harness real-time e-markets\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Ervin Howell\",\n" +
                "    \"username\": \"Antonette\",\n" +
                "    \"email\": \"Shanna@melissa.tv\",\n" +
                "    \"address\": {\n" +
                "      \"street\": \"Victor Plains\",\n" +
                "      \"suite\": \"Suite 879\",\n" +
                "      \"city\": \"Wisokyburgh\",\n" +
                "      \"zipcode\": \"90566-7771\",\n" +
                "      \"geo\": {\n" +
                "        \"lat\": \"-43.9509\",\n" +
                "        \"lng\": \"-34.4618\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"phone\": \"010-692-6593 x09125\",\n" +
                "    \"website\": \"anastasia.net\",\n" +
                "    \"company\": {\n" +
                "      \"name\": \"Deckow-Crist\",\n" +
                "      \"catchPhrase\": \"Proactive didactic contingency\",\n" +
                "      \"bs\": \"synergize scalable supply-chains\"\n" +
                "    }\n" +
                "  }]";
    }

}
