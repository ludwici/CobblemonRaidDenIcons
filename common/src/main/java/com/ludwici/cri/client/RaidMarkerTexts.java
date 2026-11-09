package com.ludwici.cri.client;

import com.necro.raid.dens.common.data.raid.RaidType;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;

public final class RaidMarkerTexts {
    private RaidMarkerTexts() {}

    public static String displayName(Component pokemonName, boolean adventure) {
        if (adventure) {
            return Component.translatable("cri.marker.adventure", pokemonName).getString();
        }
        return pokemonName.getString();
    }

    public static String title(Component pokemonName, boolean adventure, String stars) {
        return Component.translatable("cri.marker.title", displayName(pokemonName, adventure), stars).getString();
    }

    public static String detailedTitle(Component pokemonName, boolean adventure, RaidType type, String stars) {
        Component typeName;
        String typeId = type.getSerializedName();
        String customKey = "cri.marker.type." + typeId;
        if (I18n.exists(customKey)) {
            typeName = Component.translatable(customKey);
        } else if (type == RaidType.STELLAR) {
            typeName = Component.translatable("cobblemon.terra_type.stellar");
        } else if (type == RaidType.NONE) {
            typeName = Component.literal(typeId);
        } else {
            typeName = Component.translatable("cobblemon.type." + typeId);
        }
        return Component.translatable("cri.marker.detailed", displayName(pokemonName, adventure), typeName, stars).getString();
    }
}
