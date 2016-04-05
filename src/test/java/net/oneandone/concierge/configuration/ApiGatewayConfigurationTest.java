package net.oneandone.concierge.configuration;

import static org.testng.Assert.*;


import com.google.common.collect.ImmutableList;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.demo.resolver.PostResolver;
import net.oneandone.concierge.demo.resolver.UserProfileExtensionResolver;
import net.oneandone.concierge.demo.resolver.UserResolver;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.List;

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

        final ApiGatewayConfiguration configuration = new ApiGatewayConfiguration();
        final Field resolversField = ApiGatewayConfiguration.class.getDeclaredField("resolvers");
        resolversField.setAccessible(true);
        resolversField.set(configuration, resolvers);

        assertNotNull(configuration.getResolvers());

        final List<GroupResolver> groupResolvers = configuration.getResolvers().getGroupResolvers();
        assertNotNull(groupResolvers);
        assertEquals(groupResolvers.size(), 2);

        final List<ExtensionResolver> extensionResolvers = configuration.getResolvers().getExtensionResolvers();
        assertNotNull(extensionResolvers);
        assertEquals(extensionResolvers.size(), 1);
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