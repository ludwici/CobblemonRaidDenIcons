package com.ludwici.cri.network;

import static com.ludwici.cri.CobblemonRaidIcons.MODID;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RaidBlockSpawnS2CPayload(BlockPos pos, RaidBossHolder holder) implements CustomPacketPayload {
    public static final ResourceLocation PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(MODID, "raid_block_spawn");
    public static final CustomPacketPayload.Type<RaidBlockSpawnS2CPayload> ID = new Type<>(PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RaidBlockSpawnS2CPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RaidBlockSpawnS2CPayload::pos,
            RaidBossHolder.STREAM_CODEC, RaidBlockSpawnS2CPayload::holder,
            RaidBlockSpawnS2CPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
