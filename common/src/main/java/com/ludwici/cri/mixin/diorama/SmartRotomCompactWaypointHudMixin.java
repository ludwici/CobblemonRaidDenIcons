package com.ludwici.cri.mixin.diorama;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Pseudo
@Mixin(targets = "com.smartrotom.client.CompactWaypointHud", remap = false)
public abstract class SmartRotomCompactWaypointHudMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Ldev/diorama/waypoint/WaypointStore;in(Ljava/lang/String;)Ljava/util/List;"), remap = false, require = 0)
    private static List<?> cri$hideSmartRotomWorldMarkers(List<?> waypoints) {
        if (!ModCompat.SMART_ROTOM_DIORAMA_040) {
            return waypoints;
        }
        return DioramaMarkerManager.visibleWorldWaypoints(waypoints);
    }
}
