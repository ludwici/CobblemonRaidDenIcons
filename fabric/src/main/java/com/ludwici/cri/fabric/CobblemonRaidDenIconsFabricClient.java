package com.ludwici.cri.fabric;

import com.ludwici.cri.ModCompat;
import com.ludwici.cri.client.MarkerDispatcher;
import com.ludwici.cri.config.CriConfig;
import com.ludwici.cri.fabric.config.ModConfig;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionResult;

public final class CobblemonRaidDenIconsFabricClient implements ClientModInitializer {
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
        CriConfig.setIconSizeSupplier(() -> ModConfig.get().iconSize);
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
            MarkerDispatcher.register(blockEntity, world, payload.holder());
        }));
        ClientPlayNetworking.registerGlobalReceiver(RaidBlockDespawnS2CPayload.ID, ((payload, context) -> {
            MarkerDispatcher.unregister(payload.pos());
        }));
    }
}
