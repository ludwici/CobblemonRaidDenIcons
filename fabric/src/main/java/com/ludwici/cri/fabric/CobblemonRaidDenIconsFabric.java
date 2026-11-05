package com.ludwici.cri.fabric;

import com.ludwici.cri.CobblemonRaidDenIcons;
import com.ludwici.cri.CriPlatform;
import com.ludwici.cri.config.RaidServerConfig;
import com.ludwici.cri.network.RaidIconAccessS2CPayload;
import com.ludwici.cri.network.RaidIconServerAccess;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockRefreshC2SPayload;
import com.ludwici.cri.network.RaidSyncServer;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class CobblemonRaidDenIconsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricPayloads.register();
        RaidIconServerAccess.installSender((player, payload) -> {
            if (!ServerPlayNetworking.canSend(player, RaidIconAccessS2CPayload.ID)) {
                return false;
            }
            ServerPlayNetworking.send(player, payload);
            return true;
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> RaidIconServerAccess.serverStarting());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> RaidIconServerAccess.sync(handler.player, true));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> RaidIconServerAccess.disconnect(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> RaidIconServerAccess.sync(newPlayer, true));
        ServerPlayNetworking.registerGlobalReceiver(RaidBlockRefreshC2SPayload.ID, (payload, context) -> {
            context.server().execute(() -> RaidSyncServer.refresh(context.player(), payload.pos(), reply -> ServerPlayNetworking.send(context.player(), reply)));
        });
        CriPlatform.installSpawnSender((level, blockEntity, payload) -> {
            for (ServerPlayer player : PlayerLookup.tracking(blockEntity)) {
                if (RaidServerConfig.allowed(player)) {
                    ServerPlayNetworking.send(player, payload);
                }
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
