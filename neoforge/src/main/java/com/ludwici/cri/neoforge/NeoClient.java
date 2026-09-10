package com.ludwici.cri.neoforge;

import com.ludwici.cri.ModCompat;
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
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(NeoClient::onConfigReload);
    }

    private static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() != ModConfig.Type.CLIENT) {
            return;
        }

        if (ModCompat.JOURNEYMAP_LOADED) {
            JourneyMarkerManager.refreshIconSize(NeoConfig.iconSize());
        }
    }
}
