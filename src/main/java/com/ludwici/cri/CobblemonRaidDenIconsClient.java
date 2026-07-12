package com.ludwici.cri;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = CobblemonRaidDenIcons.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CobblemonRaidDenIcons.MODID, value = Dist.CLIENT)
public class CobblemonRaidDenIconsClient {
    public CobblemonRaidDenIconsClient(ModContainer container) {
//        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }
}
