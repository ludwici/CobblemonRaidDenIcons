package com.ludwici.cri;

import dev.architectury.platform.Platform;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;

public final class ModCompat {
    public static final boolean JOURNEYMAP_LOADED;
    public static final boolean XAERO_WORLD_MAP_LOADED;
    public static final boolean DIORAMA_LOADED;
    public static final boolean FTB_CHUNKS_LOADED;
    public static final boolean MEGA_SHOWDOWN_LOADED;

    static {
        JOURNEYMAP_LOADED = Platform.isModLoaded("journeymap");
        XAERO_WORLD_MAP_LOADED = Platform.isModLoaded("xaeroworldmap");
        DIORAMA_LOADED = Platform.isModLoaded("diorama");
        FTB_CHUNKS_LOADED = Platform.isModLoaded("ftbchunks");
        MEGA_SHOWDOWN_LOADED = Platform.isModLoaded("mega_showdown");
    }

    private ModCompat() {}

    public static void init() {}
}
