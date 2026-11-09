package com.ludwici.cri.fabric;

import com.ludwici.cri.CriPlatform;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.ludwici.cri.config.CriConfig;
import com.ludwici.cri.fabric.config.ModConfig;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockRefreshC2SPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.network.RaidIconAccessS2CPayload;
import com.ludwici.cri.plugins.ftb.FtbMarkerManager;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;

import java.util.ArrayList;
import java.util.List;

public final class CobblemonRaidDenIconsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModCompat.init();
        if (ModCompat.FTB_CHUNKS_LOADED) {
            FtbMarkerManager.init();
        }

        registerConfig();
        registerClientNetwork();

        CriPlatform.installRefreshRequester(pos -> {
            if (!ClientPlayNetworking.canSend(RaidBlockRefreshC2SPayload.ID)) {
                return false;
            }
            ClientPlayNetworking.send(new RaidBlockRefreshC2SPayload(pos));
            return true;
        });
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
        if (!ModCompat.DIORAMA_LOADED) {
            AutoConfig.getGuiRegistry(ModConfig.class).registerPredicateProvider(
                    (i18n, field, config, defaults, registry) -> List.of(),
                    field -> field.getName().equals("dioramaIconStyle")
            );
        }
        if (!ModCompat.FTB_CHUNKS_LOADED) {
            AutoConfig.getGuiRegistry(ModConfig.class).registerPredicateProvider(
                    (i18n, field, config, defaults, registry) -> List.of(),
                    field -> field.getName().equals("ftbIconStyle")
            );
        }
        if (ModCompat.DIORAMA_LOADED) {
            registerCategoryHeader("dioramaIconStyle");
        }
        if (ModCompat.FTB_CHUNKS_LOADED) {
            registerCategoryHeader("ftbIconStyle");
        }
        CriConfig.setIconSizeSupplier(() -> ModConfig.get().iconSize);
        CriConfig.setTeraIconSizeSupplier(() -> ModConfig.get().teraIconSize);
        CriConfig.setDioramaIconStyleSupplier(() -> ModConfig.get().dioramaIconStyle);
        CriConfig.setFtbIconStyleSupplier(() -> ModConfig.get().ftbIconStyle);
        AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener((holder, config) -> {
            if (ModCompat.JOURNEYMAP_LOADED) {
                JourneyMarkerManager.refreshIconSizes(config.iconSize, config.teraIconSize);
            }
            if (ModCompat.DIORAMA_LOADED) {
                DioramaMarkerManager.refreshIconStyle();
            }
            if (ModCompat.FTB_CHUNKS_LOADED) {
                FtbMarkerManager.refreshIconStyle();
            }
            return InteractionResult.PASS;
        });
    }

    private static void registerCategoryHeader(String fieldName) {
        AutoConfig.getGuiRegistry(ModConfig.class).registerPredicateTransformer(
                (entries, i18n, field, config, defaults, registry) -> {
                    List<AbstractConfigListEntry> result = new ArrayList<>(entries.size() + 1);
                    result.add(ConfigEntryBuilder.create().startTextDescription(Component.translatable(i18n + ".@PrefixText")).build());
                    result.addAll(entries);
                    return result;
                },
                field -> field.getName().equals(fieldName)
        );
    }

    public static void registerClientNetwork() {
        ClientPlayNetworking.registerGlobalReceiver(RaidIconAccessS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> RaidMarkerLifecycle.setServerAccess(payload.allowed()));
        });
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
