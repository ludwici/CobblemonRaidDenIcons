package com.ludwici.cri;

import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChunkScanListener {
    public static void register() {
        ClientChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof RaidCrystalBlockEntity raidCrystalBlock) {
                    var rb = ClientRaidRegistry.getRaidBoss(raidCrystalBlock.getRaidBossLocation());
                    String stars;
                    try {
                        stars = rb.getTier().getStars();
                    } catch (MatchException e) {
                        stars = "";
                    }
                    RaidBossHolder holder = new RaidBossHolder(rb.getId(), rb.getType(), stars);
                    if (FabricLoader.getInstance().isModLoaded("journeymap")) {
                        JourneyMarkerManager.registerMarker(raidCrystalBlock, world, holder);
                    } else if (FabricLoader.getInstance().isModLoaded("xaeroworldmap")) {
                        XaeroMarkerManager.registerMarker(raidCrystalBlock, holder);
                    }
                }
            }
        }));
        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof RaidCrystalBlockEntity) {
                    if (FabricLoader.getInstance().isModLoaded("journeymap")) {
                        JourneyMarkerManager.unregisterMarker(be.getBlockPos());
                    } else if (FabricLoader.getInstance().isModLoaded("xaeroworldmap")) {
                        XaeroMarkerManager.unregisterMarker(be.getBlockPos());
                    }
                }
            }
        });
    }
}
