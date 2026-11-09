package com.ludwici.cri.mixin.ftb;

import com.ludwici.cri.plugins.ftb.FtbMarkerManager;
import dev.ftb.mods.ftbchunks.api.client.icon.MapType;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbchunks.client.mapicon.WaypointMapIcon", remap = false)
public abstract class FtbWaypointMapIconMixin {
    @Inject(method = "draw(Ldev/ftb/mods/ftbchunks/api/client/icon/MapType;Lnet/minecraft/client/gui/GuiGraphics;IIIIZI)V", at = @At("HEAD"), remap = false, cancellable = true)
    private void cri$drawRaidTypeIcon(MapType mapType, GuiGraphics graphics, int x, int y, int width, int height, boolean outsideVisibleArea, int alpha, CallbackInfo ci) {
        if (!outsideVisibleArea && FtbMarkerManager.drawRaidTypeIcon(this, mapType, graphics, x, y, width, height, alpha)) {
            ci.cancel();
        }
    }

    @Inject(method = "isVisible(Ldev/ftb/mods/ftbchunks/api/client/icon/MapType;DZ)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void cri$showRaidOnLargeMap(MapType mapType, double distanceToPlayer, boolean outsideVisibleArea, CallbackInfoReturnable<Boolean> cir) {
        if (mapType.isLargeMap() && FtbMarkerManager.isRaidIcon(this)) {
            cir.setReturnValue(!outsideVisibleArea);
        }
    }
}
