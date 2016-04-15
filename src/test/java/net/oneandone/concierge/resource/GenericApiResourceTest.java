package net.oneandone.concierge.resource;

import net.oneandone.concierge.JsonHelper;
import net.oneandone.concierge.configuration.Resolvers;
import net.oneandone.concierge.demo.resolver.PostResolver;
import net.oneandone.concierge.demo.resolver.UserProfileExtensionResolver;
import net.oneandone.concierge.demo.resolver.UserResolver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test(singleThreaded = true)
public class GenericApiResourceTest {

    private static GenericApiResource apiResource;

    @BeforeClass
    public static void setUpApiResource() {
        final List<String> extensionResolvers = new ArrayList<>();
        final List<String> groupResolvers = new ArrayList<>();

        groupResolvers.add(UserResolver.class.getCanonicalName());
        groupResolvers.add(PostResolver.class.getCanonicalName());

        extensionResolvers.add(UserProfileExtensionResolver.class.getCanonicalName());

        apiResource = new GenericApiResource(new Resolvers(groupResolvers, extensionResolvers));
    }

    @Test
    public void testRootResponse() throws Exception {
        final Response response = apiResource.getResource("", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testRootResponse"));
    }

    @Test
    public void testGroupResponse() throws Exception {
        final Response response = apiResource.getResource("users", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testGroupResponse"));

        assertEquals(response.getHeaderString("Accept-Ranges"), "users");
        assertEquals(response.getHeaderString("Content-Range"), "users 0-2/4");
        assertEquals(response.getHeaderString("Last-Modified"), "1934-10-30T07:13:50Z");
    }

    @Test
    public void testNestedGroupResponse() throws Exception {
        final Response response = apiResource.getResource("users/johann.bitionaire/posts", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testNestedGroupResponse"));
        assertEquals(response.getHeaderString("Last-Modified"), "1961-06-02T05:12:12Z");
    }

    @Test
    public void testSingleRootElementResponse() throws Exception {
        final Response response = apiResource.getResource("users/andreas.piranha87", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testSingleRootElementResponse"));
        assertEquals(response.getHeaderString("Last-Modified"), "1928-02-17T01:32:30Z");
    }

    @Test
    public void testSingleNestedElementResponse() throws Exception {
        final Response response = apiResource.getResource("users/tobias.netdevfighter/posts/go-hard-or-go-home", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testSingleNestedElementResponse"));
        assertEquals(response.getHeaderString("Last-Modified"), "2018-11-18T10:11:25Z");
    }

    @Test
    public void testExtensionResponse() throws Exception {
        final Response response = apiResource.getResource("users/johann.bitionaire/profile", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testExtensionResponse"));
        assertEquals(response.getHeaderString("Last-Modified"), "1961-06-02T05:12:12Z");
    }

    @Test
    public void testGroupResponseWithExtensions() throws Exception {
        final List<String> extensions = new ArrayList<>();
        extensions.add("profile");

        final Response response = apiResource.getResource("users", null, null, extensions);

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testGroupResponseWithExtensions"));

        assertEquals(response.getHeaderString("Accept-Ranges"), "users");
        assertEquals(response.getHeaderString("Content-Range"), "users 0-2/4");
        assertEquals(response.getHeaderString("Last-Modified"), "1934-10-30T07:13:50Z");
    }

    @Test
    public void testGroupResponseWithExtensionsAndSubgroups() throws Exception {
        final List<String> extensions = new ArrayList<>();
        extensions.add("profile");
        extensions.add("posts");

        final Response response = apiResource.getResource("users", 1, 10, extensions);

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testGroupResponseWithExtensionsAndSubgroups"));

        assertEquals(response.getHeaderString("Accept-Ranges"), "users");
        assertEquals(response.getHeaderString("Content-Range"), "users 0-3/4");
        assertEquals(response.getHeaderString("Last-Modified"), "1992-07-19T03:04:12Z");
    }

    @Test
    public void testElementResponseWithExtensions() throws Exception {
        final List<String> extensions = new ArrayList<>();
        extensions.add("profile");

        final Response response = apiResource.getResource("users/johann.bitionaire", null, null, extensions);

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testElementResponseWithExtensions"));
        assertEquals(response.getHeaderString("Last-Modified"), "1961-06-02T05:12:12Z");
    }

    @Test
    public void testPageCount() throws Exception {
        final Response response = apiResource.getResource("users", 2, 3, Collections.emptyList());

        assertEquals(response.getStatus(), 200);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity(), JsonHelper.getJsonStringFor(GenericApiResourceTest.class, "testPageCount"));

        assertEquals(response.getHeaderString("Accept-Ranges"), "users");
        assertEquals(response.getHeaderString("Content-Range"), "users 3-3/4");
        assertEquals(response.getHeaderString("Last-Modified"), "1994-02-04T15:09:45Z");
    }

    @Test
    public void testResponseOfNonExistingResource() {
        final Response response = apiResource.getResource("users/rick.grimes", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 404);
    }

    @Test
    public void testGettingUnknownGroup() {
        final Response response = apiResource.getResource("users/daniel.germandrummer92/instruments", null, null, Collections.emptyList());

        assertEquals(response.getStatus(), 404);
    }

    @Test
    public void testOptionsForRootGroup() {
        final Response response = apiResource.getOptions("users");

        assertEquals(response.getStatus(), 200);
        assertEquals(response.getHeaderString("Accept"), "GET, OPTIONS");
    }

    @Test
    public void testOptionsForElement() {
        final Response response = apiResource.getOptions("users/tobias.netdevfighter");

        assertEquals(response.getStatus(), 200);
        assertEquals(response.getHeaderString("Accept"), "GET, OPTIONS");
    }

    @Test
    public void testOptionsForSubgroup() {
        final Response response = apiResource.getOptions("users/andreas.piranha87/posts");

        assertEquals(response.getStatus(), 200);
        assertEquals(response.getHeaderString("Accept"), "GET, OPTIONS");
    }

    @Test
    public void testOptionsForExtension() {
        final Response response = apiResource.getOptions("users/johann.bitionaire/profile");

        assertEquals(response.getStatus(), 200);
        assertEquals(response.getHeaderString("Accept"), "GET, OPTIONS");
    }

    @Test
    public void testOptionsForNonExisting() {
        final Response response = apiResource.getOptions("users/daniel.germandrummer92/instruments");

        assertEquals(response.getStatus(), 404);
    }
}