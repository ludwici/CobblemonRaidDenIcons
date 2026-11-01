package com.ludwici.cri.mixin.xaero;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.hud.minimap.world.state.MinimapWorldState;
import xaero.hud.path.XaeroPath;

import java.util.Objects;

@Mixin(value = MinimapWorldState.class, remap = false)
public class XaeroWorldStateMixin {
    @Unique private XaeroPath cri$lastWorld;

    @Inject(method = "setAutoWorldPath", at = @At("TAIL"))
    private void cri$onWorldChanged(XaeroPath path, CallbackInfo ci) {
        if (Objects.equals(cri$lastWorld, path)) {
            return;
        }
        cri$lastWorld = path;
        RaidMarkerLifecycle.markerApiReady();
    }
}
