package net.oneandone.concierge.configuration;

import com.google.common.collect.ImmutableList;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.demo.resolver.PostResolver;
import net.oneandone.concierge.demo.resolver.UserProfileExtensionResolver;
import net.oneandone.concierge.demo.resolver.UserResolver;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.testng.Assert.*;

public class ApiGatewayConfigurationTest {

    @Test
    public void testValidConfiguration() throws Exception {
        final List<String> groupResolverClassNames = ImmutableList.<String>builder()
                .add(UserResolver.class.getCanonicalName())
                .add(PostResolver.class.getCanonicalName())
                .build();

        final List<String> extensionResolverClassNames = ImmutableList.<String>builder()
                .add(UserProfileExtensionResolver.class.getCanonicalName())
                .build();

        final Resolvers resolvers = new Resolvers(groupResolverClassNames, extensionResolverClassNames);

        final ApiGatewayConfiguration configuration = ApiGatewayConfiguration.getConfiguration(ClassLoader.getSystemResource("server.json"));
        final Field resolversField = ApiGatewayConfiguration.class.getDeclaredField("resolvers");
        resolversField.setAccessible(true);
        resolversField.set(configuration, resolvers);

        assertNotNull(configuration.getResolvers());

        final List<GroupResolver> groupResolvers = configuration.getResolvers().getGroupResolvers();
        assertNotNull(groupResolvers);
        assertEquals(groupResolvers.size(), 2);
        boolean contains = false;
        for (String className : groupResolverClassNames) {
            for (GroupResolver resolver : groupResolvers) {
                if (resolver.getClass().getCanonicalName().equals(className)) {
                    contains = true;
                }
            }
            assertTrue(contains);
            contains = false;
        }

        final List<ExtensionResolver> extensionResolvers = configuration.getResolvers().getExtensionResolvers();
        assertNotNull(extensionResolvers);
        assertEquals(extensionResolvers.size(), 1);
        for (String className : extensionResolverClassNames) {
            for (ExtensionResolver resolver : extensionResolvers) {
                if (resolver.getClass().getCanonicalName().equals(className)) {
                    contains = true;
                }
            }
            assertTrue(contains);
            contains = false;
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testInvalidConfiguration() throws Exception {
        final List<String> groupResolverClassNames = ImmutableList.<String>builder()
                .add("foobar")
                .build();

        final List<String> extensionResolverClassNames = ImmutableList.<String>builder().build();

        new Resolvers(groupResolverClassNames, extensionResolverClassNames);
    }

}