package com.dispenserbreeding.platform;

public final class Services {
    private static PlatformHelper platformHelper;

    private Services() {
    }

    public static void bind(PlatformHelper helper) {
        if (platformHelper != null) {
            throw new IllegalStateException("PlatformHelper already bound");
        }
        platformHelper = helper;
    }

    public static PlatformHelper platform() {
        if (platformHelper == null) {
            throw new IllegalStateException("PlatformHelper not bound");
        }
        return platformHelper;
    }
}
