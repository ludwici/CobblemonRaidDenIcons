package com.ludwici.cri.fabric;

import com.ludwici.cri.ModCompat;
import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.ludwici.cri.config.CriConfig;
import com.ludwici.cri.fabric.config.ModConfig;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionResult;

import java.util.List;

public final class CobblemonRaidDenIconsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModCompat.init();

        registerConfig();
        registerClientNetwork();

        ChunkScanListener.register();
    }

    private static void registerConfig() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        if (!ModCompat.MEGA_SHOWDOWN_LOADED) {
            AutoConfig.getGuiRegistry(ModConfig.class).registerAnnotationProvider(
                    (i18n, field, config, defaults, registry) -> List.of(),
                    field -> field.getName().equals("teraIconSize"),
                    ConfigEntry.BoundedDiscrete.class
            );
        }
        CriConfig.setIconSizeSupplier(() -> ModConfig.get().iconSize);
        CriConfig.setTeraIconSizeSupplier(() -> ModConfig.get().teraIconSize);
        AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener((holder, config) -> {
            if (ModCompat.JOURNEYMAP_LOADED) {
                JourneyMarkerManager.refreshIconSizes(config.iconSize, config.teraIconSize);
            }
            return InteractionResult.PASS;
        });
    }

    public static void registerClientNetwork() {
        ClientPlayNetworking.registerGlobalReceiver(RaidBlockSpawnS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientLevel world = context.client().level;
                if (world != null) {
                    RaidMarkerLifecycle.track(world, payload.pos(), payload.holder());
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(RaidBlockDespawnS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientLevel world = context.client().level;
                if (world != null) {
                    RaidMarkerLifecycle.untrack(world, payload.pos());
                }
            });
        });
    }
}
