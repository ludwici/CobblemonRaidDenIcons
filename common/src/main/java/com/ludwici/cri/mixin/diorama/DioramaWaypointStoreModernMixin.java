package com.ludwici.cri.mixin.diorama;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.diorama.waypoint.WaypointStore", remap = false)
public abstract class DioramaWaypointStoreModernMixin {
    @Inject(method = "mapChanged", at = @At("TAIL"), remap = false)
    private static void cri$mapChanged(String dimension, CallbackInfo ci) {
        RaidMarkerLifecycle.markerApiReady();
    }
}
