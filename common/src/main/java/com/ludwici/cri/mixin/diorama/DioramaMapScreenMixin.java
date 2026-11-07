package com.ludwici.cri.mixin.diorama;

import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "dev.diorama.MapScreen", remap = false)
public abstract class DioramaMapScreenMixin {
    @ModifyArgs(method = {"render", "method_25394"}, at = @At(value = "INVOKE", target = "Ldev/diorama/waypoint/WaypointOverlay;draw"), remap = false)
    private void cri$hideDuplicateRaidTooltip(Args args) {
        if (DioramaMarkerManager.isRaidPin(args.get(4))) {
            args.set(3, false);
        }
    }
}
