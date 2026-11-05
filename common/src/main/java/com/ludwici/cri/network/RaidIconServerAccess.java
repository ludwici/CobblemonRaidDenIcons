package com.ludwici.cri.network;

import com.ludwici.cri.config.RaidServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiPredicate;

public final class RaidIconServerAccess {
    private static final Map<UUID, Boolean> lastSent = new HashMap<>();
    private static BiPredicate<ServerPlayer, RaidIconAccessS2CPayload> sender = (player, payload) -> false;

    private RaidIconServerAccess() {}

    public static void installSender(BiPredicate<ServerPlayer, RaidIconAccessS2CPayload> accessSender) {
        sender = accessSender;
    }

    public static void serverStarting() {
        lastSent.clear();
        RaidServerConfig.load();
    }

    public static void sync(ServerPlayer player, boolean force) {
        if (player.connection == null) {
            return;
        }
        boolean allowed = RaidServerConfig.allowed(player);
        if (!force && Objects.equals(lastSent.get(player.getUUID()), allowed)) {
            return;
        }
        if (sender.test(player, new RaidIconAccessS2CPayload(allowed))) {
            lastSent.put(player.getUUID(), allowed);
        }
    }

    public static void inventoryChanged(Inventory inventory) {
        if (inventory.player instanceof ServerPlayer player) {
            sync(player, false);
        }
    }

    public static void disconnect(ServerPlayer player) {
        lastSent.remove(player.getUUID());
    }
}
