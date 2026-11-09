package com.ludwici.cri.network;

import com.ludwici.cri.config.RaidServerConfig;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public final class RaidSyncServer {
    private RaidSyncServer() {}

    public static void refresh(ServerPlayer player, BlockPos pos, Consumer<CustomPacketPayload> reply) {
        if (!RaidServerConfig.allowed(player)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        int distance = (Math.max(32, player.server.getPlayerList().getViewDistance()) + 2) * 16;
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > (double) distance * distance || !level.hasChunkAt(pos)) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof RaidCrystalBlockEntity blockEntity) {
            var boss = blockEntity.getRaidBoss();
            if (boss != null) {
                String stars;
                try {
                    stars = boss.getTier().getStars();
                } catch (MatchException ignored) {
                    stars = "";
                }
                reply.accept(new RaidBlockSpawnS2CPayload(pos, new RaidBossHolder(boss.getId(), boss.getType(), stars, boss.isAdventure())));
                return;
            }
            if (blockEntity.getRaidBossLocation() != null) {
                return;
            }
        }
        reply.accept(new RaidBlockDespawnS2CPayload(pos));
    }
}
