package com.ludwici.cri;

import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.entity.BlockEntity;

public class ChunkScanListener {
    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof RaidCrystalBlockEntity raidCrystalBlock) {
                    RaidBlockMarkerManager.registerMarker(raidCrystalBlock, world);
                }
            }
        }));
        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof RaidCrystalBlockEntity raidCrystalBlock) {
                    RaidBlockMarkerManager.unregisterMarker(raidCrystalBlock, world);
                }
            }
        });
    }
}
