package com.ludwici.cri.mixin.diorama;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.diorama.waypoint.WaypointStore", remap = false)
public abstract class DioramaWaypointStoreMixin {
    @Unique
    private static Object cri$lastStore;

    @Inject(method = "get", at = @At("RETURN"), remap = false)
    private static void cri$storeReady(CallbackInfoReturnable<Object> cir) {
        Object store = cir.getReturnValue();
        if (store == null || cri$lastStore == store) {
            return;
        }
        cri$lastStore = store;
        if (!DioramaMarkerManager.isUpdating()) {
            RaidMarkerLifecycle.markerApiReady();
        }
    }

    @Inject(method = "mapChanged", at = @At("TAIL"), remap = false)
    private static void cri$mapChanged(String dimension, CallbackInfo ci) {
        RaidMarkerLifecycle.markerApiReady();
    }

    @Inject(method = "onLeave", at = @At("TAIL"), remap = false)
    private static void cri$onLeave(CallbackInfo ci) {
        cri$lastStore = null;
        DioramaMarkerManager.storeClosed();
    }
}
