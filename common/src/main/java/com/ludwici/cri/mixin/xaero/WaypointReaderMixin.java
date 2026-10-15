package com.ludwici.cri.mixin.xaero;

import com.ludwici.cri.ModCompat;
import com.ludwici.cri.config.CriConfig;
import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.xaero.XaeroMarkerApi;
import com.necro.raid.dens.common.data.raid.RaidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointReader;
import xaero.map.mods.gui.WaypointRenderContext;

@Mixin(value = WaypointReader.class, remap = false)
public class WaypointReaderMixin {
    @Unique
    private static int cri$iconSize(Waypoint waypoint) {
        RaidBossHolder holder = XaeroMarkerApi.getHolder(waypoint);
        if (holder == null) {
            return -1;
        }
        boolean teraIcon = (XaeroMarkerApi.isTeraRaid(holder) || holder.raidType() == RaidType.STELLAR) && ModCompat.MEGA_SHOWDOWN_LOADED;
        return teraIcon ? CriConfig.teraIconSize() : CriConfig.iconSize();
    }

    @Inject(method = "getInteractionBoxLeft(Lxaero/map/mods/gui/Waypoint;Lxaero/map/mods/gui/WaypointRenderContext;F)I", at = @At("HEAD"), cancellable = true)
    private void cri$getInteractionBoxLeft(Waypoint waypoint, WaypointRenderContext context, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        int size = cri$iconSize(waypoint);
        if (size >= 0) {
            cir.setReturnValue(-size / 2);
        }
    }

    @Inject(method = "getInteractionBoxRight(Lxaero/map/mods/gui/Waypoint;Lxaero/map/mods/gui/WaypointRenderContext;F)I", at = @At("HEAD"), cancellable = true)
    private void cri$getInteractionBoxRight(Waypoint waypoint, WaypointRenderContext context, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        int size = cri$iconSize(waypoint);
        if (size >= 0) {
            cir.setReturnValue((size + 1) / 2);
        }
    }

    @Inject(method = "getInteractionBoxTop(Lxaero/map/mods/gui/Waypoint;Lxaero/map/mods/gui/WaypointRenderContext;F)I", at = @At("HEAD"), cancellable = true)
    private void cri$getInteractionBoxTop(Waypoint waypoint, WaypointRenderContext context, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        int size = cri$iconSize(waypoint);
        if (size >= 0) {
            cir.setReturnValue(-size / 2);
        }
    }

    @Inject(method = "getInteractionBoxBottom(Lxaero/map/mods/gui/Waypoint;Lxaero/map/mods/gui/WaypointRenderContext;F)I", at = @At("HEAD"), cancellable = true)
    private void cri$getInteractionBoxBottom(Waypoint waypoint, WaypointRenderContext context, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        int size = cri$iconSize(waypoint);
        if (size >= 0) {
            cir.setReturnValue((size + 1) / 2);
        }
    }
}
