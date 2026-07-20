package com.ludwici.cri.mixin.xaero;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import com.necro.raid.dens.common.data.raid.RaidType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

import static com.ludwici.cri.CobblemonRaidIcons.LOGGER;

@Mixin(WaypointRenderer.class)
public abstract class WaypointRendererMixin extends MapElementRenderer<Waypoint, WaypointRenderContext, WaypointRenderer> {

    protected WaypointRendererMixin(WaypointRenderContext context, MapElementRenderProvider<Waypoint, WaypointRenderContext> provider, MapElementReader<Waypoint, WaypointRenderContext, WaypointRenderer> reader) {
        super(context, provider, reader);
    }

    @Inject(method = "renderElement(Lxaero/map/mods/gui/Waypoint;ZDFDDLxaero/map/element/render/ElementRenderInfo;Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lxaero/map/graphics/renderer/multitexture/MultiTextureRenderTypeRendererProvider;)Z", at = @At("HEAD"), remap = false, cancellable = true)
    private void render(Waypoint w, boolean hovered, double optionalDepth, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, CallbackInfoReturnable<Boolean> cir) {
        RaidBossHolder holder = XaeroMarkerManager.INSTANCE.getHolder((new BlockPos(w.getX(), w.getY(), w.getZ()).toShortString()));
        if (holder == null) {
            return;
        }

        ResourceLocation image;
        int diameter = 36;
        var raidType = holder.raidType();
        int posOffset = diameter / 2;
        int u;
        int imgW, imgH;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (raidType != RaidType.STELLAR) {
            image = ResourceLocation.fromNamespaceAndPath(Cobblemon.MODID,"textures/gui/types.png");
            var element = ElementalTypes.get(raidType.name());
            int offset = element.getTextureXMultiplier();
            u = diameter*offset;
            imgW = 648;
            imgH = diameter;

        } else if (FabricLoader.getInstance().isModLoaded("mega_showdown")) {
            image = ResourceLocation.parse(String.format("mega_showdown:textures/gui/summary/tera_types/%s.png", raidType.getSerializedName()));
            u = 0;
            imgW = 32;
            imgH = 32;
        } else {
            LOGGER.error("Invalid element: {}", raidType.name());
            return;
        }
        RenderSystem.setShaderTexture(0, image);
        RenderSystem.enableBlend();
        guiGraphics.blit(image, -posOffset, -posOffset, u, 0, diameter, diameter, imgW, imgH);
        RenderSystem.disableBlend();

        if (hovered) {
            PoseStack matrixStack = guiGraphics.pose();

            var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
            var name = String.format("%s (%s)", boss.getDisplaySpecies().getTranslatedName().getString(), holder.stars());
            Font fontRenderer = Minecraft.getInstance().font;
            int len = fontRenderer.width(name);

            matrixStack.scale(1.5F, 1.5F, 1.0F);
            int bgLen = len + 4;
            int color = WaypointColor.BLACK.getHex();
            float red = (float)(color >> 16 & 255) / 255.0F;
            float green = (float)(color >> 8 & 255) / 255.0F;
            float blue = (float)(color & 255) / 255.0F;
            MapRenderHelper.fillIntoExistingBuffer(matrixStack.last().pose(), this.context.textBGConsumer, -bgLen / 2, -1, bgLen / 2, 11, red, green, blue, 1);

            matrixStack.translate(0.0F, 0.0F, 1.0F);
            Misc.drawNormalText(matrixStack, name, (float) ((-len)) / 2.0F, 1.0F, 16777215, false, vanillaBufferSource);
        }

        cir.cancel();
    }

    @Inject(method = "renderElementShadow(Lxaero/map/mods/gui/Waypoint;ZFDDLxaero/map/element/render/ElementRenderInfo;Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lxaero/map/graphics/renderer/multitexture/MultiTextureRenderTypeRendererProvider;)V", at = @At("HEAD"), cancellable = true)
    private void hideShadow(Waypoint w, boolean hovered, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, CallbackInfo ci) {
        RaidBossHolder holder = XaeroMarkerManager.INSTANCE.getHolder((new BlockPos(w.getX(), w.getY(), w.getZ()).toShortString()));
        if (holder != null) {
            ci.cancel();
        }
    }
}
