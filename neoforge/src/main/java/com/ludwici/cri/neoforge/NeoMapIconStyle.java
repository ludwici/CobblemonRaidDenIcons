package com.ludwici.cri.neoforge;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

public enum NeoMapIconStyle implements TranslatableEnum {
    DEFAULT,
    TYPE_ICONS;

    @Override
    public Component getTranslatedName() {
        return Component.translatable("text.autoconfig.cri.enum.iconStyle." + name());
    }
}
