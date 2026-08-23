package com.ludwici.cri.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.IntValue ICON_SIZE;

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ICON_SIZE = builder
                .translation("text.autoconfig.cri.option.iconSize")
                .defineInRange(
                        "iconSize",
                        ConfigConstants.DEFAULT_ICON_SIZE,
                        ConfigConstants.MIN_ICON_SIZE,
                        ConfigConstants.MAX_ICON_SIZE
                );

        SPEC = builder.build();
    }

    private Config() {
    }

    public static int iconSize() {
        return ICON_SIZE.get();
    }
}
