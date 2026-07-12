package com.ludwici.cri.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class Payloads {
    public static void register() {
        PayloadTypeRegistry.playS2C().register(RaidBlockSpawnS2CPayload.ID, RaidBlockSpawnS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RaidBlockDespawnS2CPayload.ID, RaidBlockDespawnS2CPayload.CODEC);
    }
}
