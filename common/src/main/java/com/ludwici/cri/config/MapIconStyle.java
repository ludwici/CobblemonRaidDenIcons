package com.ludwici.cri.config;

import net.minecraft.network.chat.Component;

public enum MapIconStyle {
    DEFAULT,
    TYPE_ICONS;

    @Override
    public String toString() {
        return Component.translatable("text.autoconfig.cri.enum.iconStyle." + name()).getString();
    }
}
