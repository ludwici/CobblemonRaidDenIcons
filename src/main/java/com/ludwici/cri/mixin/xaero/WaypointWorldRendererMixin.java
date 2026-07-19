package com.ludwici.cri.mixin.xaero;

import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.element.render.*;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderContext;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderer;

@Mixin(WaypointWorldRenderer.class)
public abstract class WaypointWorldRendererMixin extends MinimapElementRenderer<Waypoint, WaypointWorldRenderContext> {
    public WaypointWorldRendererMixin(MinimapElementReader<Waypoint, WaypointWorldRenderContext> elementReader, MinimapElementRenderProvider<Waypoint, WaypointWorldRenderContext> provider, WaypointWorldRenderContext context) {
        super(elementReader, provider, context);
    }

    @Inject(method = "renderElement(Lxaero/common/minimap/waypoints/Waypoint;ZZDFDDLxaero/hud/minimap/element/render/MinimapElementRenderInfo;Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)Z", cancellable = true, at = @At("HEAD"))
    private void render(Waypoint w, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, CallbackInfoReturnable<Boolean> cir) {
        RaidBossHolder holder = XaeroMarkerManager.INSTANCE.getHolder((new BlockPos(w.getX(), w.getY(), w.getZ()).toShortString()));
        if (holder != null) {
            cir.setReturnValue(false);
        }
    }
}
