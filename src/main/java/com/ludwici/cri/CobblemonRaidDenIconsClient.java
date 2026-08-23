package com.ludwici.cri;

import com.ludwici.cri.config.Config;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CobblemonRaidDenIcons.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CobblemonRaidDenIcons.MODID, value = Dist.CLIENT)
public class CobblemonRaidDenIconsClient {
    public CobblemonRaidDenIconsClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(CobblemonRaidDenIconsClient::onConfigReload);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }

    private static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() != ModConfig.Type.CLIENT) {
            return;
        }

        if (ModCompat.JOURNEYMAP_LOADED) {
            JourneyMarkerManager.refreshIconSize(Config.iconSize());
        }
    }
}
