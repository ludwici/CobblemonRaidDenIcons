package com.ludwici.cri;

import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChunkScanListener {
    public static void register() {

        ClientChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof RaidCrystalBlockEntity raidCrystalBlock) {
                    var rb = ClientRaidRegistry.getRaidBoss(raidCrystalBlock.getRaidBossLocation());
                    RaidBossHolder holder = new RaidBossHolder(rb.getId(), rb.getType());
                    RaidBlockMarkerManager.registerMarker(raidCrystalBlock, world, holder);
                }
            }
        }));
        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof RaidCrystalBlockEntity) {
                    RaidBlockMarkerManager.unregisterMarker(be.getBlockPos());
                }
            }
        });
    }
}
