package com.ludwici.cri.plugins.xaero;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.config.CriConfig;
import com.ludwici.cri.network.RaidBossHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import com.necro.raid.dens.common.data.raid.RaidType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;

public final class XaeroMarkerApi {
    private static final int TYPE_TEXTURE_SIZE = 36;

    private XaeroMarkerApi() {}

    public static RaidBossHolder getHolder(int x, int y, int z) {
        return XaeroMarkerManager.INSTANCE.getHolder(new BlockPos(x, y, z).toShortString());
    }

    public static RaidBossHolder getHolder(xaero.common.minimap.waypoints.Waypoint waypoint) {
        return getHolder(waypoint.getX(), waypoint.getY(), waypoint.getZ());
    }

    public static RaidBossHolder getHolder(xaero.map.mods.gui.Waypoint waypoint) {
        return getHolder(waypoint.getX(), waypoint.getY(), waypoint.getZ());
    }

    public static boolean isTracked(xaero.common.minimap.waypoints.Waypoint waypoint) {
        return getHolder(waypoint) != null;
    }

    public static boolean isTracked(xaero.map.mods.gui.Waypoint waypoint) {
        return getHolder(waypoint) != null;
    }

    public static boolean isTeraRaid(RaidBossHolder holder) {
        var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
        return boss != null && "tera".equalsIgnoreCase(boss.getFeature());
    }

    public static int getIconSize(RaidBossHolder holder) {
        if ((isTeraRaid(holder) || holder.raidType() == RaidType.STELLAR) && ModCompat.MEGA_SHOWDOWN_LOADED) {
            return CriConfig.teraIconSize();
        }
        return CriConfig.iconSize();
    }

    public static boolean renderRaidIcon(RaidBossHolder holder, GuiGraphics guiGraphics) {
        ResourceLocation image;
        RaidType raidType = holder.raidType();
        boolean teraIcon = (isTeraRaid(holder) || raidType == RaidType.STELLAR) && ModCompat.MEGA_SHOWDOWN_LOADED;
        int displaySize = getIconSize(holder);
        int posOffset = displaySize / 2;
        int u;
        int sourceWidth;
        int sourceHeight;
        int textureWidth;
        int textureHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        if (teraIcon) {
            image = ResourceLocation.parse(String.format("mega_showdown:textures/gui/summary/tera_types/%s.png", raidType.getSerializedName()));
            u = 0;
            sourceWidth = sourceHeight = textureWidth = textureHeight = 32;
        } else if (raidType != RaidType.STELLAR) {
            image = ResourceLocation.fromNamespaceAndPath(Cobblemon.MODID, "textures/gui/types.png");
            var element = ElementalTypes.get(raidType.name());
            u = TYPE_TEXTURE_SIZE * element.getTextureXMultiplier();
            sourceWidth = sourceHeight = TYPE_TEXTURE_SIZE;
            textureWidth = 648;
            textureHeight = TYPE_TEXTURE_SIZE;
        } else {
            LOGGER.error("Invalid element: {}", raidType.name());
            return false;
        }

        RenderSystem.setShaderTexture(0, image);
        RenderSystem.enableBlend();
        guiGraphics.blit(image, -posOffset, -posOffset, displaySize, displaySize, (float) u, 0F, sourceWidth, sourceHeight, textureWidth, textureHeight);
        RenderSystem.disableBlend();
        return true;
    }
}
