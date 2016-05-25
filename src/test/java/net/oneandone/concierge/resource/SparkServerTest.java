package net.oneandone.concierge.resource;

import net.oneandone.concierge.JsonHelper;
import net.oneandone.concierge.configuration.Resolvers;
import net.oneandone.concierge.demo.resolver.PostResolver;
import net.oneandone.concierge.demo.resolver.UserProfileExtensionResolver;
import net.oneandone.concierge.demo.resolver.UserResolver;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SparkServerTest {

    private static SparkServer apiResource;

    @Mock
    private Request request;
    @Mock
    private Response response;
    @Mock
    private QueryParamsMap queryMap;

    @BeforeClass
    public static void setUpApiResource() {
        final List<String> extensionResolvers = new ArrayList<>();
        final List<String> groupResolvers = new ArrayList<>();

        groupResolvers.add(UserResolver.class.getCanonicalName());
        groupResolvers.add(PostResolver.class.getCanonicalName());

        extensionResolvers.add(UserProfileExtensionResolver.class.getCanonicalName());

        apiResource = new SparkServer(new Resolvers(groupResolvers, extensionResolvers), 8080);
    }

    @BeforeMethod(alwaysRun=true)
    public void injectDoubles() {
        MockitoAnnotations.initMocks(this); //This could be pulled up into a shared base class
    }

    @Test
    public void testRootResponse() throws Exception {
        when(request.params()).thenReturn(Collections.emptyMap());
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("/");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testRootResponse"));
    }


    @Test
    public void testGroupResponse() throws Exception {
        Mockito.when(request.params()).thenReturn(Collections.emptyMap());
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("/users");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testGroupResponse"));
        Mockito.verify(response).header("Accept-Ranges", "users");
        Mockito.verify(response).header("Content-Range", "users 0-2/4");
        Mockito.verify(response).header("Last-Modified", "1934-10-30T07:13:50Z");
    }

    @Test
    public void testNestedGroupResponse() throws Exception {
        Mockito.when(request.params()).thenReturn(Collections.emptyMap());
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users/johann.bitionaire/posts");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testNestedGroupResponse"));
        Mockito.verify(response).header("Last-Modified", "1961-06-02T05:12:12Z");
    }

    @Test
    public void testSingleRootElementResponse() throws Exception {
        Mockito.when(request.params()).thenReturn(Collections.emptyMap());
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users/andreas.piranha87");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testSingleRootElementResponse"));
        Mockito.verify(response).header("Last-Modified", "1928-02-17T01:32:30Z");
    }

    @Test
    public void testSingleNestedElementResponse() throws Exception {
        Mockito.when(request.params()).thenReturn(Collections.emptyMap());
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users/tobias.netdevfighter/posts/go-hard-or-go-home");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testSingleNestedElementResponse"));
        Mockito.verify(response).header("Last-Modified", "2018-11-18T10:11:25Z");
    }

    @Test
    public void testExtensionResponse() throws Exception {
        Mockito.when(request.params()).thenReturn(Collections.emptyMap());
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users/johann.bitionaire/profile");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testExtensionResponse"));
        Mockito.verify(response).header("Last-Modified", "1961-06-02T05:12:12Z");
    }

    @Test
    public void testGroupResponseWithExtensions() throws Exception {
        final Map<String, String> queryParameters = new HashMap<>(1);
        queryParameters.put("show", "profile");
        Mockito.when(request.params()).thenReturn(queryParameters);
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testGroupResponseWithExtensions"));
        Mockito.verify(response).header("Last-Modified", "1934-10-30T07:13:50Z");
        Mockito.verify(response).header("Accept-Ranges", "users");
        Mockito.verify(response).header("Content-Range", "users 0-2/4");
    }

    @Test
    public void testGroupResponseWithExtensionsAndSubgroups() throws Exception {
        QueryParamsMap queryMap = mock(QueryParamsMap.class);
        final Map<String, String[]> queryParameters = new HashMap<>(3);
        String[] arrayVal = {"posts", "profile"};
        queryParameters.put("show", arrayVal);
        String[] arrayVal2 = {"1"};
        queryParameters.put("page", arrayVal2);
        String[] arrayVal3 = {"10"};
        queryParameters.put("per_page", arrayVal3);
        Mockito.when(queryMap.toMap()).thenReturn(queryParameters);
        Mockito.when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testGroupResponseWithExtensionsAndSubgroups"));
        Mockito.verify(response).header("Last-Modified", "1992-07-19T03:04:12Z");
        Mockito.verify(response).header("Accept-Ranges", "users");
        Mockito.verify(response).header("Content-Range", "users 0-3/4");
    }

    @Test
    public void testElementResponseWithExtensions() throws Exception {
        final Map<String, String> queryParameters = new HashMap<>(1);
        queryParameters.put("show", "profile");
        Mockito.when(request.params()).thenReturn(queryParameters);
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users/johann.bitionaire");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testElementResponseWithExtensions"));
        Mockito.verify(response).header("Last-Modified", "1961-06-02T05:12:12Z");
    }

    @Test
    public void testPageCount() throws Exception {
        final Map<String, String> queryParameters = new HashMap<>(2);
        queryParameters.put("page", "2");
        queryParameters.put("per_page", "3");
        Mockito.when(request.params()).thenReturn(queryParameters);
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        when(request.uri()).thenReturn("users");
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).body(JsonHelper.getJsonStringFor(SparkServerTest.class, "testPageCount"));
        Mockito.verify(response).header("Last-Modified", "1994-02-04T15:09:45Z");
        Mockito.verify(response).header("Accept-Ranges", "users");
        Mockito.verify(response).header("Content-Range", "users 3-3/4");
    }

    @Test
    public void testResponseOfNonExistingResource() {
        Mockito.when(request.params()).thenReturn(Collections.emptyMap());
        when(request.uri()).thenReturn("users/rick.grimes");
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testGettingUnknownGroup() {
        Mockito.when(request.params()).thenReturn(Collections.emptyMap());
        when(request.uri()).thenReturn("users/daniel.germandrummer92/instruments");
        when(queryMap.toMap()).thenReturn(Collections.emptyMap());
        when(request.queryMap()).thenReturn(queryMap);
        apiResource.getGetResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testOptionsForRootGroup() {
        when(request.uri()).thenReturn("users");
        apiResource.getOptionsResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).header("Accept", "GET, OPTIONS");
    }

    @Test
    public void testOptionsForElement() {
        when(request.uri()).thenReturn("users/tobias.netdevfighter");
        apiResource.getOptionsResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).header("Accept", "GET, OPTIONS");
    }

    @Test
    public void testOptionsForSubgroup() {
        when(request.uri()).thenReturn("users/andreas.piranha87/posts");
        apiResource.getOptionsResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).header("Accept", "GET, OPTIONS");
    }

    @Test
    public void testOptionsForExtension() {
        when(request.uri()).thenReturn("users/johann.bitionaire/profile");
        apiResource.getOptionsResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_OK);
        Mockito.verify(response).header("Accept", "GET, OPTIONS");
    }

    @Test
    public void testOptionsForNonExisting() {
        when(request.uri()).thenReturn("users/daniel.germandrummer92/instruments");
        apiResource.getOptionsResponse(request, response);

        Mockito.verify(response).status(HttpServletResponse.SC_NOT_FOUND);
    }
}