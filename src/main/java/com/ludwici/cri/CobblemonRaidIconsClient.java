package com.ludwici.cri;

import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;

public class CobblemonRaidIconsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerClientNetwork();
        ChunkScanListener.register();
    }

    public static void registerClientNetwork() {
        ClientPlayNetworking.registerGlobalReceiver(RaidBlockSpawnS2CPayload.ID, ((payload, context) -> {
            ClientLevel world = context.client().level;
            if (world == null) {
                return;
            }
            RaidCrystalBlockEntity blockEntity = (RaidCrystalBlockEntity) world.getBlockEntity(payload.pos());
            RaidBlockMarkerManager.registerMarker(blockEntity, world, payload.holder());
        }));

        ClientPlayNetworking.registerGlobalReceiver(RaidBlockDespawnS2CPayload.ID, ((payload, context) -> {
            ClientLevel world = context.client().level;
            if (world == null) {
                return;
            }
            RaidBlockMarkerManager.unregisterMarker(payload.pos());
        }));
    }
}
