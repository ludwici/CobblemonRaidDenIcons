package com.ludwici.cri;

import com.ludwici.cri.config.ModConfig;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionResult;

public class CobblemonRaidIconsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModCompat.init();

        registerConfig();
        registerClientNetwork();

        ChunkScanListener.register();
    }

    private static void registerConfig() {
        AutoConfig.register(
                ModConfig.class,
                GsonConfigSerializer::new
        );

        AutoConfig
                .getConfigHolder(ModConfig.class)
                .registerSaveListener((holder, config) -> {
                    if (ModCompat.JOURNEYMAP_LOADED) {
                        JourneyMarkerManager.refreshIconSize(config.iconSize);
                    }
                    return InteractionResult.PASS;
                });
    }

    public static void registerClientNetwork() {
        ClientPlayNetworking.registerGlobalReceiver(RaidBlockSpawnS2CPayload.ID, ((payload, context) -> {
            ClientLevel world = context.client().level;
            if (world == null) {
                return;
            }
            RaidCrystalBlockEntity blockEntity = (RaidCrystalBlockEntity) world.getBlockEntity(payload.pos());
            if (ModCompat.JOURNEYMAP_LOADED) {
                JourneyMarkerManager.registerMarker(blockEntity, world, payload.holder());
            } else if (ModCompat.XAERO_WORLD_MAP_LOADED) {
                XaeroMarkerManager.registerMarker(blockEntity, payload.holder());
            }
        }));

        ClientPlayNetworking.registerGlobalReceiver(RaidBlockDespawnS2CPayload.ID, ((payload, context) -> {
            if (ModCompat.JOURNEYMAP_LOADED) {
                JourneyMarkerManager.unregisterMarker(payload.pos());
            } else if (ModCompat.XAERO_WORLD_MAP_LOADED) {
                XaeroMarkerManager.unregisterMarker(payload.pos());
            }
        }));
    }
}
