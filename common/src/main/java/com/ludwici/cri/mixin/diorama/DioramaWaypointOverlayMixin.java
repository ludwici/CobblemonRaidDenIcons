package com.ludwici.cri.mixin.diorama;

import com.ludwici.cri.ModCompat;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.diorama.waypoint.WaypointOverlay", remap = false)
public abstract class DioramaWaypointOverlayMixin {
    @Inject(method = "layout", at = @At("RETURN"), remap = false)
    private static void cri$hideDistantMinimapPins(@Coerce Object view, List<?> waypoints, boolean fullscreen, CallbackInfoReturnable<List<?>> cir) {
        if (!fullscreen || ModCompat.SMART_ROTOM_DIORAMA_040) {
            DioramaMarkerManager.filterMinimapPins(cir.getReturnValue());
        }
    }
}
