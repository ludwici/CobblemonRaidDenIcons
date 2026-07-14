package com.ludwici.cri;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
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

public class RaidBlockMarkerManager {
    private static IClientAPI jmApi;
    private static final Map<String, MarkerOverlay> trackedMarkers = new HashMap<>();

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

        int diameter = 36;
        var element = ElementalTypes.get(raidType.name());
        int offset = element.getTextureXMultiplier();

        MapImage typeIcon = new MapImage(ResourceLocation.fromNamespaceAndPath(Cobblemon.MODID, "textures/gui/types.png"), diameter * offset, diameter, diameter, diameter, 0xffffff, 1f);
        typeIcon.centerAnchors();

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
}
