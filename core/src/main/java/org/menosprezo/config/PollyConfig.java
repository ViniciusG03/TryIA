package org.menosprezo.config;

import software.amazon.awssdk.regions.Region;

public class PollyConfig {
    public static final String ACESS_KEY = System.getenv("ACESS_KEY");
    public static final String SECRET_KEY = System.getenv("SECRET_KEY");
    public static final Region REGION = Region.US_EAST_1;
}
