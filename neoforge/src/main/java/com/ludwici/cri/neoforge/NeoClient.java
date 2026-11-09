package com.ludwici.cri.neoforge;

import com.ludwici.cri.CriPlatform;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.plugins.ftb.FtbMarkerManager;
import com.ludwici.cri.network.RaidBlockRefreshC2SPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class NeoClient {
    private NeoClient() {
    }

    public static void init(IEventBus modEventBus, ModContainer container) {
        if (ModCompat.FTB_CHUNKS_LOADED) {
            FtbMarkerManager.init();
        }
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) ->
                new ConfigurationScreen(modContainer, parent,
                        (context, key, original) -> ModCompat.MEGA_SHOWDOWN_LOADED || !key.equals("teraIconSize")
                                ? original
                                : null));
        modEventBus.addListener(NeoClient::onConfigReload);
        CriPlatform.installRefreshRequester(pos -> {
            var connection = Minecraft.getInstance().getConnection();
            if (connection == null || !NetworkRegistry.hasChannel(connection.getConnection(), null, RaidBlockRefreshC2SPayload.PAYLOAD_ID)) {
                return false;
            }
            PacketDistributor.sendToServer(new RaidBlockRefreshC2SPayload(pos));
            return true;
        });
    }

    private static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() != ModConfig.Type.CLIENT) {
            return;
        }

        if (ModCompat.JOURNEYMAP_LOADED) {
            JourneyMarkerManager.refreshIconSizes(NeoConfig.iconSize(), NeoConfig.teraIconSize());
        }
    }
}
