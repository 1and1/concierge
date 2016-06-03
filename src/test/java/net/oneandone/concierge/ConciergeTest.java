package net.oneandone.concierge;

import net.oneandone.concierge.demo.resolver.PostResolver;
import net.oneandone.concierge.demo.resolver.UserResolver;
import org.testng.annotations.Test;

public class ConciergeTest {

    @Test(timeOut = 15000L)
    public void testStartAndStop() throws Exception {
        final Concierge server = Concierge.prepare().port(8080).start(new UserResolver(), new PostResolver());
        server.stop();
    }

}