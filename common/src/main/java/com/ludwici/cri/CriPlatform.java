package com.ludwici.cri;

import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class CriPlatform {
    @FunctionalInterface
    public interface SpawnSender {
        void send(Level level, BlockEntity blockEntity, RaidBlockSpawnS2CPayload payload);
    }

    private static SpawnSender spawnSender = (level, blockEntity, payload) -> {};

    private CriPlatform() {}

    public static void installSpawnSender(SpawnSender sender) {
        spawnSender = sender == null ? (level, blockEntity, payload) -> {} : sender;
    }

    public static void sendSpawn(Level level, BlockEntity blockEntity, RaidBlockSpawnS2CPayload payload) {
        spawnSender.send(level, blockEntity, payload);
    }
}
