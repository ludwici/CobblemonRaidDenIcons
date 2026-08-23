package com.ludwici.cri;

import net.fabricmc.loader.api.FabricLoader;
import static com.ludwici.cri.CobblemonRaidIcons.LOGGER;

public final class ModCompat {
    public static final boolean JOURNEYMAP_LOADED;
    public static final boolean XAERO_WORLD_MAP_LOADED;
    public static final boolean MEGA_SHOWDOWN_LOADED;

    static {
        FabricLoader loader = FabricLoader.getInstance();

        JOURNEYMAP_LOADED = loader.isModLoaded("journeymap");
        XAERO_WORLD_MAP_LOADED = loader.isModLoaded("xaeroworldmap");
        MEGA_SHOWDOWN_LOADED = loader.isModLoaded("mega_showdown");
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
