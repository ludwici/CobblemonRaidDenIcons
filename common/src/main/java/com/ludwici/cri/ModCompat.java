package com.ludwici.cri;

import dev.architectury.platform.Platform;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;

public final class ModCompat {
    public static final boolean JOURNEYMAP_LOADED;
    public static final boolean XAERO_WORLD_MAP_LOADED;
    public static final boolean MEGA_SHOWDOWN_LOADED;

    static {
        JOURNEYMAP_LOADED = Platform.isModLoaded("journeymap");
        XAERO_WORLD_MAP_LOADED = Platform.isModLoaded("xaeroworldmap");
        MEGA_SHOWDOWN_LOADED = Platform.isModLoaded("mega_showdown");
    }

    private ModCompat() {
    }

    public static void init() {
        LOGGER.info(
                "Mod compatibility: JourneyMap={}, XaeroWorldMap={}, MegaShowdown={}",
                JOURNEYMAP_LOADED,
                XAERO_WORLD_MAP_LOADED,
                MEGA_SHOWDOWN_LOADED
        );
    }
}
