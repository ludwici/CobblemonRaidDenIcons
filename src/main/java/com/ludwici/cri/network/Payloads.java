package com.ludwici.cri.network;

import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.ludwici.cri.CobblemonRaidDenIcons.MODID;

@EventBusSubscriber(modid = MODID)
public class Payloads {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();

        registrar.playToClient(RaidBlockSpawnS2CPayload.ID, RaidBlockSpawnS2CPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                Level world = context.player().level();

                RaidCrystalBlockEntity blockEntity = (RaidCrystalBlockEntity) world.getBlockEntity(payload.pos());
                JourneyMarkerManager.registerMarker(blockEntity, world, payload.holder());
            });
        });

        registrar.playToClient(RaidBlockDespawnS2CPayload.ID, RaidBlockDespawnS2CPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> JourneyMarkerManager.unregisterMarker(payload.pos()));
        });
    }
}
