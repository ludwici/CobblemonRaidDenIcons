package com.ludwici.cri.neoforge;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

public final class NeoClientPayloads {
    private NeoClientPayloads() {}

    public static void spawn(Level level, RaidBlockSpawnS2CPayload payload) {
        if (level instanceof ClientLevel world) {
            RaidMarkerLifecycle.track(world, payload.pos(), payload.holder());
        }
    }

    public static void despawn(Level level, RaidBlockDespawnS2CPayload payload) {
        if (level instanceof ClientLevel world) {
            RaidMarkerLifecycle.untrack(world, payload.pos());
        }
    }
}
