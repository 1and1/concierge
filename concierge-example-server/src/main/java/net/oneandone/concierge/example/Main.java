package net.oneandone.concierge.example;

import net.oneandone.concierge.Concierge;

import java.net.URL;

public class Main {

    public static void main(final String... args) throws Exception {
        final URL configurationURL = ClassLoader.getSystemResource("server.yml");
        Concierge.start(configurationURL);
    }

}
