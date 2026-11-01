package com.ludwici.cri.mixin.xaero;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.map.mods.SupportXaeroMinimap;

@Mixin(value = SupportXaeroMinimap.class, remap = false)
public class SupportXaeroMinimapMixin {
    @Shadow private WaypointWorld waypointWorld;
    @Shadow private WaypointSet waypointSet;
    @Unique private WaypointWorld cri$previousWorld;
    @Unique private WaypointSet cri$previousSet;

    @Inject(method = "checkWaypoints", at = @At("TAIL"))
    private void cri$onWaypointsChanged(CallbackInfo ci) {
        if (cri$previousWorld == waypointWorld && cri$previousSet == waypointSet) {
            return;
        }
        cri$previousWorld = waypointWorld;
        cri$previousSet = waypointSet;
        if (!XaeroMarkerManager.isInitializing()) {
            RaidMarkerLifecycle.markerApiReady();
        }
    }
}
