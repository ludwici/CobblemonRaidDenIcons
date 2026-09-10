package com.ludwici.cri.neoforge;

import com.ludwici.cri.config.ConfigConstants;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class NeoConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.IntValue ICON_SIZE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ICON_SIZE = builder.comment("Raid icon size for Xaero's map/minimap")
                .translation("text.autoconfig.cri.option.iconSize")
                .defineInRange("iconSize", ConfigConstants.DEFAULT_ICON_SIZE,
                        ConfigConstants.MIN_ICON_SIZE, ConfigConstants.MAX_ICON_SIZE);
        SPEC = builder.build();
    }

    private NeoConfig() {}
    public static int iconSize() { return ICON_SIZE.get(); }
}
