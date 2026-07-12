package com.ludwici.cri.network;

import com.necro.raid.dens.common.data.raid.RaidType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record RaidBossHolder(ResourceLocation bossId, RaidType raidType) {
    public static final StreamCodec<RegistryFriendlyByteBuf, RaidBossHolder> STREAM_CODEC = StreamCodec.of(RaidBossHolder::writeEntry, RaidBossHolder::readEntry);

    private static void writeEntry(FriendlyByteBuf buf, RaidBossHolder holder) {
        buf.writeResourceLocation(holder.bossId);
        buf.writeEnum(holder.raidType);
    }

    private static RaidBossHolder readEntry(FriendlyByteBuf buf) {
        return new RaidBossHolder(buf.readResourceLocation(), buf.readEnum(RaidType.class));
    }
}
