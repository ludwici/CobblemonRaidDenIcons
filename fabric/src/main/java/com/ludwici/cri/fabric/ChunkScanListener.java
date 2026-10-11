package com.ludwici.cri.fabric;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.blocks.block.RaidCrystalBlock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.core.BlockPos;

public class ChunkScanListener {
    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            for (BlockPos pos : chunk.getBlockEntitiesPos()) {
                if (chunk.getBlockEntity(pos) instanceof RaidCrystalBlockEntity || chunk.getBlockState(pos).getBlock() instanceof RaidCrystalBlock) {
                    RaidMarkerLifecycle.track(world, pos, null);
                }
            }
        });
        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> RaidMarkerLifecycle.chunkUnloaded(world, chunk.getPos()));
    }
}
