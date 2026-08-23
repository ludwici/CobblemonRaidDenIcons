package com.ludwici.cri;

import net.neoforged.fml.ModList;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;

public final class ModCompat {
    public static final boolean JOURNEYMAP_LOADED;
    public static final boolean XAERO_WORLD_MAP_LOADED;
    public static final boolean MEGA_SHOWDOWN_LOADED;

    static {
        ModList modList = ModList.get();

        JOURNEYMAP_LOADED = modList.isLoaded("journeymap");
        XAERO_WORLD_MAP_LOADED = modList.isLoaded("xaeroworldmap");
        MEGA_SHOWDOWN_LOADED = modList.isLoaded("mega_showdown");
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
