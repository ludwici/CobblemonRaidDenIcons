package com.ludwici.cri.neoforge;

import com.ludwici.cri.CobblemonRaidDenIcons;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.blocks.block.RaidCrystalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

@EventBusSubscriber(modid = CobblemonRaidDenIcons.MODID)
public class ChunkScanListener {
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        ChunkAccess chunkAccess = event.getChunk();

        Set<BlockPos> positions = chunkAccess.getBlockEntitiesPos();
        LevelAccessor level = event.getLevel();
        positions.forEach(blockPos -> {
            BlockEntity be = chunkAccess.getBlockEntity(blockPos);
            if (level.isClientSide()) {
                if (be instanceof RaidCrystalBlockEntity || chunkAccess.getBlockState(blockPos).getBlock() instanceof RaidCrystalBlock) {
                    NeoClientChunks.track(level, blockPos);
                }
            } else if (be instanceof RaidCrystalBlockEntity raidCrystalBlock) {
                    var rb = raidCrystalBlock.getRaidBoss();
                    if (rb == null) {
                        return;
                    }
                    String stars;
                    try {
                        stars = rb.getTier().getStars();
                    } catch (MatchException e) {
                        stars = "";
                    }
                    RaidBossHolder holder = new RaidBossHolder(rb.getId(), rb.getType(), stars);
                    RaidBlockSpawnS2CPayload payload = new RaidBlockSpawnS2CPayload(blockPos, holder);
                    ChunkPos chunkPos = new ChunkPos(blockPos);
                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunkPos, payload);
            }
        });
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        ChunkAccess chunkAccess = event.getChunk();
        LevelAccessor level = event.getLevel();
        Set<BlockPos> positions = chunkAccess.getBlockEntitiesPos();
        if (level.isClientSide()) {
            NeoClientChunks.unload(level, chunkAccess.getPos());
            return;
        }
        positions.forEach(blockPos -> {
            BlockEntity be = chunkAccess.getBlockEntity(blockPos);
            if (be instanceof RaidCrystalBlockEntity) {
                RaidBlockDespawnS2CPayload payload = new RaidBlockDespawnS2CPayload(blockPos);
                ChunkPos chunkPos = new ChunkPos(blockPos);
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunkPos, payload);
            }
        });
    }
}
