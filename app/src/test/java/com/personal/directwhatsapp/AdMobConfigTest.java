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
    public void productionBannerUsesConfiguredAdUnit() {
        assertEquals("ca-app-pub-2486732565949048/1291165743",
                AdMobConfig.BANNER_AD_UNIT_ID);
    }
}
