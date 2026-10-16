package com.personal.directwhatsapp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public final class CountryDetectionPolicyTest {
    private static final List<String> SUPPORTED = Arrays.asList("PK", "US", "GB", "AE");

    private final CountryDetectionPolicy.RegionSupport support =
            region -> SUPPORTED.contains(region);

    @Test
    public void prefersSupportedSimRegion() {
        assertEquals("PK", CountryDetectionPolicy.chooseRegion("pk", "AE", "US", support));
    }

    @Test
    public void usesNetworkWhenSimRegionIsMissingOrUnsupported() {
        assertEquals("AE", CountryDetectionPolicy.chooseRegion("", "ae", "US", support));
        assertEquals("AE", CountryDetectionPolicy.chooseRegion("XX", "AE", "US", support));
    }

    @Test
    public void usesDeviceRegionWhenSimAndNetworkAreUnavailable() {
        assertEquals("GB", CountryDetectionPolicy.chooseRegion(null, "", "gb", support));
    }

    @Test
    public void fallsBackToPakistanWhenNoCandidateIsSupported() {
        assertEquals("PK", CountryDetectionPolicy.chooseRegion(null, "XX", "ZZ", support));
    }

    @Test
    public void normalizesSupportedCandidatesToUppercase() {
        assertEquals("US", CountryDetectionPolicy.chooseRegion("us", null, "PK", support));
    }
}
