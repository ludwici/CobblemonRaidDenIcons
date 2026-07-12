package com.ludwici.cri;

import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

import static com.ludwici.cri.CobblemonRaidDenIcons.MODID;

@EventBusSubscriber(modid = MODID)
public class ChunkScanListener {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        ChunkAccess chunkAccess = event.getChunk();

        Set<BlockPos> positions = chunkAccess.getBlockEntitiesPos();
        LevelAccessor level = event.getLevel();

        positions.forEach(blockPos -> {
            BlockEntity be = chunkAccess.getBlockEntity(blockPos);
            if (be instanceof RaidCrystalBlockEntity raidCrystalBlock) {
                if (level.isClientSide()) {
                    var rb = ClientRaidRegistry.getRaidBoss(raidCrystalBlock.getRaidBossLocation());
                    RaidBossHolder holder = new RaidBossHolder(rb.getId(), rb.getType());
                    RaidBlockMarkerManager.registerMarker(raidCrystalBlock, (Level) level, holder);
                } else {
                    var rb = raidCrystalBlock.getRaidBoss();
                    RaidBossHolder holder = new RaidBossHolder(rb.getId(), rb.getType());
                    RaidBlockSpawnS2CPayload payload = new RaidBlockSpawnS2CPayload(blockPos, holder);
                    ChunkPos chunkPos = new ChunkPos(blockPos);
                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunkPos, payload);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        ChunkAccess chunkAccess = event.getChunk();
        LevelAccessor level = event.getLevel();

        Set<BlockPos> positions = chunkAccess.getBlockEntitiesPos();
        positions.forEach(blockPos -> {
            BlockEntity be = chunkAccess.getBlockEntity(blockPos);
            if (be instanceof RaidCrystalBlockEntity raidCrystalBlock) {
                if (level.isClientSide()) {
                    RaidBlockMarkerManager.unregisterMarker(be.getBlockPos());
                } else {
                    RaidBlockDespawnS2CPayload payload = new RaidBlockDespawnS2CPayload(blockPos);
                    ChunkPos chunkPos = new ChunkPos(blockPos);
                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunkPos, payload);

                }
            }
        });
    }
}
