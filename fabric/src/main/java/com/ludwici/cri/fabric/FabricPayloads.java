package com.ludwici.cri.fabric;

import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockRefreshC2SPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class FabricPayloads {
    private FabricPayloads() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(RaidBlockSpawnS2CPayload.ID, RaidBlockSpawnS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RaidBlockDespawnS2CPayload.ID, RaidBlockDespawnS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RaidBlockRefreshC2SPayload.ID, RaidBlockRefreshC2SPayload.CODEC);
    }
}
