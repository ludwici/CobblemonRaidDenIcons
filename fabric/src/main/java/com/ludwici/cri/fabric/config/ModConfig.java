package com.ludwici.cri.fabric.config;

import com.ludwici.cri.config.ConfigConstants;
import com.ludwici.cri.config.MapIconStyle;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import static com.ludwici.cri.CobblemonRaidDenIcons.MODID;

@Config(name = MODID)
public final class ModConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(min = ConfigConstants.MIN_ICON_SIZE, max = ConfigConstants.MAX_ICON_SIZE)
    public int iconSize = ConfigConstants.DEFAULT_ICON_SIZE;

    @ConfigEntry.BoundedDiscrete(min = ConfigConstants.MIN_ICON_SIZE, max = ConfigConstants.MAX_TERA_ICON_SIZE)
    public int teraIconSize = ConfigConstants.DEFAULT_TERA_ICON_SIZE;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public MapIconStyle dioramaIconStyle = MapIconStyle.DEFAULT;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public MapIconStyle ftbIconStyle = MapIconStyle.DEFAULT;

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public void validatePostLoad() {
        iconSize = Math.clamp(iconSize, ConfigConstants.MIN_ICON_SIZE, ConfigConstants.MAX_ICON_SIZE);
        teraIconSize = Math.clamp(teraIconSize, ConfigConstants.MIN_ICON_SIZE, ConfigConstants.MAX_TERA_ICON_SIZE);
        if (dioramaIconStyle == null) {
            dioramaIconStyle = MapIconStyle.DEFAULT;
        }
        if (ftbIconStyle == null) {
            ftbIconStyle = MapIconStyle.DEFAULT;
        }
    }
}
