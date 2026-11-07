package com.ludwici.cri.mixin.diorama;

import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.diorama.waypoint.WaypointHud", remap = false)
public abstract class DioramaWaypointHudMixin {
    @ModifyVariable(method = "extractRenderState", at = @At("STORE"), ordinal = 0, remap = false)
    private List<?> cri$hideDistantWorldPins(List<?> waypoints) {
        return DioramaMarkerManager.visibleWorldWaypoints(waypoints);
    }
}
