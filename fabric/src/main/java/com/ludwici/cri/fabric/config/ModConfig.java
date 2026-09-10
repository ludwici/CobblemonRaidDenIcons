package com.ludwici.cri.fabric.config;

import com.ludwici.cri.config.ConfigConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "cri")
public final class ModConfig implements ConfigData {
    @ConfigEntry.BoundedDiscrete(min = ConfigConstants.MIN_ICON_SIZE, max = ConfigConstants.MAX_ICON_SIZE)
    public int iconSize = ConfigConstants.DEFAULT_ICON_SIZE;

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public void validatePostLoad() {
        iconSize = Math.clamp(iconSize, ConfigConstants.MIN_ICON_SIZE, ConfigConstants.MAX_ICON_SIZE);
    }
}
