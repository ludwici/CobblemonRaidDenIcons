package com.ludwici.cri.mixin.xaero;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.config.ModConfig;
import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.necro.raid.dens.common.data.raid.RaidType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;

import static com.ludwici.cri.CobblemonRaidIcons.LOGGER;

@Mixin(WaypointMapRenderer.class)
public class WaypointMapRendererMixin {
    @Unique
    private static final int TYPE_TEXTURE_SIZE = 36;

    @Inject(method = "renderElement(Lxaero/common/minimap/waypoints/Waypoint;ZZDFDDLxaero/hud/minimap/element/render/MinimapElementRenderInfo;Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)Z", at = @At("HEAD"), cancellable = true)
    private void render(Waypoint w, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, CallbackInfoReturnable<Boolean> cir) {
        RaidBossHolder holder = XaeroMarkerManager.INSTANCE.getHolder((new BlockPos(w.getX(), w.getY(), w.getZ()).toShortString()));
        if (holder == null) {
            return;
        }

        if (outOfBounds) {
            cir.cancel();
            return;
        }

        ResourceLocation image;
//        int diameter = 36;
        int displaySize = ModConfig.get().iconSize;
        var raidType = holder.raidType();
        int posOffset = displaySize / 2;
        int u;
        int sourceWidth;
        int sourceHeight;

        int textureWidth;
        int textureHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (raidType != RaidType.STELLAR) {
            image = ResourceLocation.fromNamespaceAndPath(Cobblemon.MODID,"textures/gui/types.png");
            var element = ElementalTypes.get(raidType.name());
            int offset = element.getTextureXMultiplier();
            u = TYPE_TEXTURE_SIZE * offset;

            sourceWidth = TYPE_TEXTURE_SIZE;
            sourceHeight = TYPE_TEXTURE_SIZE;

            textureWidth = 648;
            textureHeight = TYPE_TEXTURE_SIZE;
        } else if (ModCompat.MEGA_SHOWDOWN_LOADED) {
            image = ResourceLocation.parse(String.format("mega_showdown:textures/gui/summary/tera_types/%s.png", raidType.getSerializedName()));
            u = 0;
            sourceWidth = 32;
            sourceHeight = 32;

            textureWidth = 32;
            textureHeight = 32;
        } else {
            LOGGER.error("Invalid element: {}", raidType.name());
            return;
        }
        RenderSystem.setShaderTexture(0, image);
        RenderSystem.enableBlend();
        guiGraphics.blit(image, -posOffset, -posOffset, displaySize, displaySize, (float)u, 0F, sourceWidth, sourceHeight, textureWidth, textureHeight);
        RenderSystem.disableBlend();
        cir.cancel();
    }
}
