package com.ludwici.cri.mixin.diorama;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Pseudo
@Mixin(targets = "dev.diorama.waypoint.WaypointOverlay", remap = false)
public abstract class DioramaSmartRotomMapLabelsMixin {
    @WrapOperation(
            method = "draw",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/diorama/waypoint/WaypointOverlay$Pin;clamped()Z",
                    ordinal = 1
            ),
            remap = false,
            require = 0
    )
    private static boolean cri$hideRaidLabels(@Coerce Object pin, Operation<Boolean> original) {
        if (ModCompat.SMART_ROTOM_DIORAMA_040 && DioramaMarkerManager.isRaidPin(pin)) {
            return true;
        }
        return original.call(pin);
    }
}
