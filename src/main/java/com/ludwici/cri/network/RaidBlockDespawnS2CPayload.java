package com.ludwici.cri.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static com.ludwici.cri.CobblemonRaidIcons.MODID;

public record RaidBlockDespawnS2CPayload(BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(MODID, "raid_block_despawn");
    public static final CustomPacketPayload.Type<RaidBlockDespawnS2CPayload> ID = new Type<>(PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RaidBlockDespawnS2CPayload> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, RaidBlockDespawnS2CPayload::pos, RaidBlockDespawnS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
