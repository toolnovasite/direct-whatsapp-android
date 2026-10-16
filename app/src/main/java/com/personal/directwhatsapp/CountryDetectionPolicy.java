package com.personal.directwhatsapp;

import java.util.Locale;

/**
 * Selects a default calling region without requesting additional device permissions.
 * The caller supplies region candidates obtained from Android and validates them
 * against the phone-number library's supported regions.
 */
final class CountryDetectionPolicy {
    interface RegionSupport {
        boolean isSupported(String region);
    }

    private CountryDetectionPolicy() { }

    static String chooseRegion(String simRegion, String networkRegion, String deviceRegion,
            RegionSupport support) {
        String region = supportedRegion(simRegion, support);
        if (region != null) return region;

        region = supportedRegion(networkRegion, support);
        if (region != null) return region;

        region = supportedRegion(deviceRegion, support);
        if (region != null) return region;

        return "PK";
    }

    private static String supportedRegion(String candidate, RegionSupport support) {
        if (candidate == null) return null;
        String normalized = candidate.toUpperCase(Locale.ROOT);
        return normalized.length() == 2 && support.isSupported(normalized) ? normalized : null;
    }
}
