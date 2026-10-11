package com.ludwici.cri.neoforge;

import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class NeoPayloads {
    private NeoPayloads() {
    }

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("cri").versioned("1.0.0").optional();
        registrar.playToClient(RaidBlockSpawnS2CPayload.ID, RaidBlockSpawnS2CPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> NeoClientPayloads.spawn(context.player().level(), payload));
        });
        registrar.playToClient(RaidBlockDespawnS2CPayload.ID, RaidBlockDespawnS2CPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> NeoClientPayloads.despawn(context.player().level(), payload));
        });
    }
}
