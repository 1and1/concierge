package net.oneandone.concierge;

import org.testng.annotations.Test;

import java.net.URL;

public class ConciergeTest {

    @Test(timeOut = 15000L)
    public void testStartAndStop() throws Exception {
        final URL configurationURL = ClassLoader.getSystemResource("server.yml");
        final Concierge server = Concierge.start(configurationURL);
        server.stop();
    }

}