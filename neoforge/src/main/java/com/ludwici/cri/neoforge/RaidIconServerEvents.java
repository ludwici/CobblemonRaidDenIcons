package com.ludwici.cri.neoforge;

import com.ludwici.cri.CobblemonRaidDenIcons;
import com.ludwici.cri.network.RaidIconServerAccess;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = CobblemonRaidDenIcons.MODID)
public final class RaidIconServerEvents {
    private RaidIconServerEvents() {}

    @SubscribeEvent
    public static void onStarting(ServerStartingEvent event) {
        RaidIconServerAccess.serverStarting();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaidIconServerAccess.sync(player, true);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaidIconServerAccess.sync(player, true);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RaidIconServerAccess.disconnect(player);
        }
    }
}
