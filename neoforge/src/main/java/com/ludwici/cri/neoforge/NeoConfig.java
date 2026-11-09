package com.ludwici.cri.neoforge;

import com.ludwici.cri.config.ConfigConstants;
import com.ludwici.cri.config.MapIconStyle;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class NeoConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.IntValue ICON_SIZE;
    private static final ModConfigSpec.IntValue TERA_ICON_SIZE;
    private static final ModConfigSpec.EnumValue<NeoMapIconStyle> DIORAMA_ICON_STYLE;
    private static final ModConfigSpec.EnumValue<NeoMapIconStyle> FTB_ICON_STYLE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ICON_SIZE = builder.comment("Raid icon size")
                .translation("text.autoconfig.cri.option.iconSize")
                .defineInRange("iconSize", ConfigConstants.DEFAULT_ICON_SIZE, ConfigConstants.MIN_ICON_SIZE, ConfigConstants.MAX_ICON_SIZE);
        TERA_ICON_SIZE = builder.comment("Tera raid icon size")
                .translation("text.autoconfig.cri.option.teraIconSize")
                .defineInRange("teraIconSize", ConfigConstants.DEFAULT_TERA_ICON_SIZE, ConfigConstants.MIN_ICON_SIZE, ConfigConstants.MAX_TERA_ICON_SIZE);
        DIORAMA_ICON_STYLE = builder.comment("Diorama raid icon style")
                .translation("text.autoconfig.cri.option.dioramaIconStyle")
                .defineEnum("dioramaIconStyle", NeoMapIconStyle.DEFAULT);
        FTB_ICON_STYLE = builder.comment("FTB Chunks raid icon style")
                .translation("text.autoconfig.cri.option.ftbIconStyle")
                .defineEnum("ftbIconStyle", NeoMapIconStyle.DEFAULT);
        SPEC = builder.build();
    }

    private NeoConfig() {}
    public static int iconSize() {
        return ICON_SIZE.get();
    }

    public static int teraIconSize() {
        return TERA_ICON_SIZE.get();
    }

    public static MapIconStyle dioramaIconStyle() {
        return MapIconStyle.valueOf(DIORAMA_ICON_STYLE.get().name());
    }

    public static MapIconStyle ftbIconStyle() {
        return MapIconStyle.valueOf(FTB_ICON_STYLE.get().name());
    }
}
