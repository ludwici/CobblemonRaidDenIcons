package com.ludwici.cri;

import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
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
            if (FabricLoader.getInstance().isModLoaded("journeymap")) {
                JourneyMarkerManager.registerMarker(blockEntity, world, payload.holder());
            } else if (FabricLoader.getInstance().isModLoaded("xaeroworldmap")) {
                XaeroMarkerManager.registerMarker(blockEntity, payload.holder());
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(RaidBlockDespawnS2CPayload.ID, ((payload, context) -> {
            if (FabricLoader.getInstance().isModLoaded("journeymap")) {
                JourneyMarkerManager.unregisterMarker(payload.pos());
            } else if (FabricLoader.getInstance().isModLoaded("xaeroworldmap")) {
                XaeroMarkerManager.unregisterMarker(payload.pos());
            }
        }));
    }
}
