package com.ludwici.cri.mixin.diorama;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.ludwici.cri.ModCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "dev.diorama.waypoint.WaypointOverlay", remap = false)
public abstract class DioramaSmartRotomMapLabelsMixin {
    @ModifyExpressionValue(method = "draw", at = @At(value = "INVOKE", target = "Ldev/diorama/render/MapViewport$View;zoom()D"), remap = false, require = 0)
    private static double cri$labelsOnlyWhenHovered(double zoom) {
        if (ModCompat.SMART_ROTOM_DIORAMA_040) {
            return Math.min(zoom, 1.0);
        }
        return zoom;
    }
}
