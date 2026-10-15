package com.ludwici.cri.mixin.xaero;

import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.xaero.XaeroMarkerApi;
import com.mojang.blaze3d.vertex.PoseStack;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.map.element.MapElementReader;
import xaero.map.element.MapElementRenderProvider;
import xaero.map.element.MapElementRenderer;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.misc.Misc;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointRenderContext;
import xaero.map.mods.gui.WaypointRenderer;

@Mixin(WaypointRenderer.class)
public abstract class WaypointRendererMixin extends MapElementRenderer<Waypoint, WaypointRenderContext, WaypointRenderer> {
    protected WaypointRendererMixin(WaypointRenderContext context, MapElementRenderProvider<Waypoint, WaypointRenderContext> provider, MapElementReader<Waypoint, WaypointRenderContext, WaypointRenderer> reader) {
        super(context, provider, reader);
    }

    @Inject(method = "renderElement(Lxaero/map/mods/gui/Waypoint;ZDFDDLxaero/map/element/render/ElementRenderInfo;Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lxaero/map/graphics/renderer/multitexture/MultiTextureRenderTypeRendererProvider;)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void cri$render(Waypoint w, boolean hovered, double optionalDepth, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, CallbackInfoReturnable<Boolean> cir) {
        RaidBossHolder holder = XaeroMarkerApi.getHolder(w);
        if (holder == null) {
            return;
        }
        if (!XaeroMarkerApi.renderRaidIcon(holder, guiGraphics)) {
            return;
        }

        if (hovered) {
            var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
            if (boss != null) {
                PoseStack matrixStack = guiGraphics.pose();
                matrixStack.pushPose();
                try {
                    String name = String.format("%s (%s)", boss.getDisplaySpecies().getTranslatedName().getString(), holder.stars());
                    Font fontRenderer = Minecraft.getInstance().font;
                    int len = fontRenderer.width(name);
                    float tooltipY = -(XaeroMarkerApi.getIconSize(holder) / 2F + 20F);
                    matrixStack.translate(0F, tooltipY, 0F);
                    matrixStack.scale(1.5F, 1.5F, 1F);
                    int bgLen = len + 4;
                    int color = WaypointColor.BLACK.getHex();
                    float red = (float) (color >> 16 & 255) / 255F;
                    float green = (float) (color >> 8 & 255) / 255F;
                    float blue = (float) (color & 255) / 255F;
                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.last().pose(), this.context.textBGConsumer, -bgLen / 2, -1, bgLen / 2, 11, red, green, blue, 1);
                    matrixStack.translate(0F, 0F, 1F);
                    Misc.drawNormalText(matrixStack, name, (float) (-len) / 2F, 1F, 16777215, false, vanillaBufferSource);
                } finally {
                    matrixStack.popPose();
                }
            }
        }
        cir.cancel();
    }

    @Inject(method = "renderElementShadow(Lxaero/map/mods/gui/Waypoint;ZFDDLxaero/map/element/render/ElementRenderInfo;Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lxaero/map/graphics/renderer/multitexture/MultiTextureRenderTypeRendererProvider;)V", at = @At("HEAD"), cancellable = true)
    private void cri$hideShadow(Waypoint w, boolean hovered, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, CallbackInfo ci) {
        if (XaeroMarkerApi.isTracked(w)) {
            ci.cancel();
        }
    }
}
