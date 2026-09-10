package com.ludwici.cri.plugins.journey;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.config.CriConfig;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import com.necro.raid.dens.common.data.raid.RaidType;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.DisplayType;
import journeymap.api.v2.client.display.MarkerOverlay;
import journeymap.api.v2.client.model.MapImage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;
import static com.ludwici.cri.CobblemonRaidDenIcons.MODID;

public class JourneyMarkerManager {
    private static IClientAPI jmApi;
    private static final Map<String, MarkerOverlay> trackedMarkers = new HashMap<>();
    private static final int TYPE_TEXTURE_SIZE = 36;

    public static void init(IClientAPI api) {
        jmApi = api;
    }

    public static void registerMarker(RaidCrystalBlockEntity blockEntity, Level world, RaidBossHolder holder) {
        if (jmApi == null) {
            return;
        }

        if (!jmApi.playerAccepts(MODID, DisplayType.Marker)) {
            return;
        }

        if (blockEntity == null) {
            LOGGER.error("RaidCrystalBlockEntity is null");
            return;
        }
        var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
        if (boss == null) {
            LOGGER.error("boss is null");
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        String posStr = pos.toShortString();
        if (trackedMarkers.containsKey(posStr)) {
            jmApi.remove(trackedMarkers.get(posStr));
        }

        var raidType = holder.raidType();

        MapImage typeIcon;
        if (raidType != RaidType.STELLAR) {
            var element = ElementalTypes.get(raidType.name());
            int offset = element.getTextureXMultiplier();
            typeIcon = new MapImage(ResourceLocation.fromNamespaceAndPath(Cobblemon.MODID, "textures/gui/types.png"), TYPE_TEXTURE_SIZE * offset, TYPE_TEXTURE_SIZE, TYPE_TEXTURE_SIZE, TYPE_TEXTURE_SIZE, 0xffffff, 1f);
        } else if (ModCompat.MEGA_SHOWDOWN_LOADED) {
            typeIcon = new MapImage(ResourceLocation.parse(String.format("mega_showdown:textures/gui/summary/tera_types/%s.png", raidType.getSerializedName())), TYPE_TEXTURE_SIZE, TYPE_TEXTURE_SIZE);
        } else {
            LOGGER.error("Invalid element: {}", raidType.name());
            return;
        }
        int displaySize = CriConfig.iconSize();

        typeIcon.centerAnchors();
        typeIcon.setDisplayWidth(displaySize);
        typeIcon.setDisplayHeight(displaySize);

        MarkerOverlay marker = new MarkerOverlay(MODID, pos, typeIcon);
        marker.setDimension(world.dimension());
        marker.setTitle(String.format("%s (%s)", boss.getDisplaySpecies().getTranslatedName().getString(), holder.stars()));
        try {
            jmApi.show(marker);
            trackedMarkers.put(posStr, marker);
        } catch (Exception e) {
            LOGGER.error("Can't add marker overlay", e);
        }
    }

    public static void unregisterMarker(BlockPos pos) {
        if (jmApi == null) {
            return;
        }
        String posStr = pos.toShortString();
        try {
            var marker = trackedMarkers.remove(posStr);
            jmApi.remove(marker);
        } catch (Exception e) {
            LOGGER.error("Can't remove marker overlay", e);
        }
    }

    public static void refreshIconSize(int displaySize) {
        if (jmApi == null) {
            return;
        }
        for (MarkerOverlay marker : trackedMarkers.values()) {
            marker.getIcon()
                    .setDisplayWidth(displaySize)
                    .setDisplayHeight(displaySize)
                    .centerAnchors();

            try {
                jmApi.show(marker);
            } catch (Exception e) {
                LOGGER.error(
                        "Can't refresh marker overlay icon size",
                        e
                );
            }
        }
    }
}
