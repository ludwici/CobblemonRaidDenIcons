package com.ludwici.cri.fabric;

import com.ludwici.cri.CobblemonRaidDenIcons;
import com.ludwici.cri.CriPlatform;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockRefreshC2SPayload;
import com.ludwici.cri.network.RaidSyncServer;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class CobblemonRaidDenIconsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricPayloads.register();
        ServerPlayNetworking.registerGlobalReceiver(RaidBlockRefreshC2SPayload.ID, (payload, context) -> {
            context.server().execute(() -> RaidSyncServer.refresh(context.player(), payload.pos(), reply -> ServerPlayNetworking.send(context.player(), reply)));
        });
        CriPlatform.installSpawnSender((level, blockEntity, payload) -> {
            for (ServerPlayer player : PlayerLookup.tracking(blockEntity)) {
                ServerPlayNetworking.send(player, payload);
            }
        });
        CobblemonRaidDenIcons.init();

        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
            if (blockEntity instanceof RaidCrystalBlockEntity) {
                RaidBlockDespawnS2CPayload payload = new RaidBlockDespawnS2CPayload(blockEntity.getBlockPos());
                for (ServerPlayer player : PlayerLookup.tracking(blockEntity)) {
                    ServerPlayNetworking.send(player, payload);
                }
            }
        });
    }
}
