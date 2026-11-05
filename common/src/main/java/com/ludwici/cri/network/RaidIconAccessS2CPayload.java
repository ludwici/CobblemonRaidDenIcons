package com.ludwici.cri.network;

import com.ludwici.cri.CobblemonRaidDenIcons;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RaidIconAccessS2CPayload(boolean allowed) implements CustomPacketPayload {
    public static final ResourceLocation PAYLOAD_ID = CobblemonRaidDenIcons.id("raid_icon_access");
    public static final Type<RaidIconAccessS2CPayload> ID = new Type<>(PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RaidIconAccessS2CPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeBoolean(payload.allowed()),
            buf -> new RaidIconAccessS2CPayload(buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
