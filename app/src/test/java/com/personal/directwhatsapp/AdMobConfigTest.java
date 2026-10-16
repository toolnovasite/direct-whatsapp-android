package com.personal.directwhatsapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class AdMobConfigTest {
    @Test
    public void appIdHasAdMobAppIdFormat() {
        assertTrue(AdMobConfig.APP_ID.matches("ca-app-pub-[0-9]{16}~[0-9]+"));
    }

    @Test
    public void developmentUsesGoogleDemoBannerAdUnit() {
        assertEquals("ca-app-pub-3940256099942544/9214589741",
                AdMobConfig.TEST_BANNER_AD_UNIT_ID);
    }
}
