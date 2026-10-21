package com.personal.directwhatsapp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public final class CountryDetectionPolicyTest {
    private static final List<String> SUPPORTED =
            Arrays.asList("PK", "US", "GB", "AE", "IN");

    private final CountryDetectionPolicy.RegionSupport support =
            region -> SUPPORTED.contains(region);

    @Test
    public void prefersSupportedSimRegionOverNetworkAndDeviceRegion() {
        assertEquals("GB", CountryDetectionPolicy.chooseRegion("gb", "PK", "US", support));
    }

    @Test
    public void usesNetworkWhenSimRegionIsMissingOrUnsupported() {
        assertEquals("AE", CountryDetectionPolicy.chooseRegion("", "ae", "US", support));
        assertEquals("AE", CountryDetectionPolicy.chooseRegion("XX", "AE", "US", support));
    }

    @Test
    public void usesDeviceRegionWhenSimAndNetworkAreUnavailable() {
        assertEquals("US", CountryDetectionPolicy.chooseRegion(null, "", "us", support));
    }

    @Test
    public void fallsBackToPakistanWhenNoCandidateIsSupported() {
        assertEquals("PK", CountryDetectionPolicy.chooseRegion(null, "XX", "ZZ", support));
        assertEquals("PK", CountryDetectionPolicy.chooseRegion(null, null, null, support));
    }

    @Test
    public void normalizesSupportedCandidatesToUppercase() {
        assertEquals("IN", CountryDetectionPolicy.chooseRegion("in", null, "PK", support));
    }

    @Test
    public void rejectsCandidatesThatAreNotTwoLetterSupportedRegions() {
        assertEquals("AE", CountryDetectionPolicy.chooseRegion("USA", "ae", "PK", support));
        assertEquals("PK", CountryDetectionPolicy.chooseRegion("U", "XX", "ZZ", support));
    }
}
