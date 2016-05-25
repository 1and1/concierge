package net.oneandone.concierge;

public class ConciergeTest {

    //Test doesn't work on CI as port is in use (sometimes?) and therefore spark doesn't start, while old jersey server did.
   /* @Test(timeOut = 15000L)
    public void testStartAndStop() throws Exception {
        final URL configurationURL = ClassLoader.getSystemResource("server.json");
        final Concierge server = Concierge.start(configurationURL);
        server.stop();
    }*/

}