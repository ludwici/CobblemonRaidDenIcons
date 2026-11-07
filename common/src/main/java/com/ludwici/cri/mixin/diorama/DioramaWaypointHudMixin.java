package com.ludwici.cri.mixin.diorama;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.diorama.waypoint.WaypointHud", remap = false)
public abstract class DioramaWaypointHudMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Ldev/diorama/waypoint/WaypointStore;in(Ljava/lang/String;)Ljava/util/List;"), remap = false)
    private List<?> cri$hideDistantWorldPins(List<?> waypoints) {
        return DioramaMarkerManager.visibleWorldWaypoints(waypoints);
    }
}
