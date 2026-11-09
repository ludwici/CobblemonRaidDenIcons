package com.ludwici.cri.client;

import com.necro.raid.dens.common.data.raid.RaidType;
import net.minecraft.network.chat.Component;

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
        if (type == RaidType.STELLAR) {
            typeName = Component.translatable("cobblemon.terra_type.stellar");
        } else if (type == RaidType.NONE) {
            typeName = Component.literal(type.getSerializedName());
        } else {
            typeName = Component.translatable("cobblemon.type." + type.getSerializedName());
        }
        return Component.translatable("cri.marker.detailed", displayName(pokemonName, adventure), typeName, stars).getString();
    }
}
