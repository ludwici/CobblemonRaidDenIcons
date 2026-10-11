package com.ludwici.cri.neoforge;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;

public final class NeoClientChunks {
    private NeoClientChunks() {}

    public static void track(LevelAccessor level, BlockPos pos) {
        if (level instanceof ClientLevel world) {
            Minecraft.getInstance().execute(() -> RaidMarkerLifecycle.track(world, pos, null));
        }
    }

    public static void unload(LevelAccessor level, ChunkPos pos) {
        if (level instanceof ClientLevel world) {
            Minecraft.getInstance().execute(() -> RaidMarkerLifecycle.chunkUnloaded(world, pos));
        }
    }
}
