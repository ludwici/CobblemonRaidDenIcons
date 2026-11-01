package com.ludwici.cri;

import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Predicate;

public final class CriPlatform {
    @FunctionalInterface
    public interface SpawnSender {
        void send(Level level, BlockEntity blockEntity, RaidBlockSpawnS2CPayload payload);
    }

    private static SpawnSender spawnSender = (level, blockEntity, payload) -> {};
    private static Predicate<BlockPos> refreshRequester = pos -> false;

    private CriPlatform() {}

    public static void installSpawnSender(SpawnSender sender) {
        spawnSender = sender == null ? (level, blockEntity, payload) -> {} : sender;
    }

    public static void sendSpawn(Level level, BlockEntity blockEntity, RaidBlockSpawnS2CPayload payload) {
        spawnSender.send(level, blockEntity, payload);
    }

    public static void installRefreshRequester(Predicate<BlockPos> requester) {
        refreshRequester = requester == null ? pos -> false : requester;
    }

    public static boolean requestRefresh(BlockPos pos) {
        return refreshRequester.test(pos);
    }
}
