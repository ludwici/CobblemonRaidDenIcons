package com.ludwici.cri;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.DisplayType;
import journeymap.api.v2.client.display.MarkerOverlay;
import journeymap.api.v2.client.model.MapImage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ludwici.cri.CobblemonRaidIcons.LOGGER;
import static com.ludwici.cri.CobblemonRaidIcons.MODID;

public class RaidBlockMarkerManager {
    private static IClientAPI jmApi;
    private static final Map<UUID, MarkerOverlay> trackedMarkers = new HashMap<>();

    public static void init(IClientAPI api) {
        jmApi = api;
    }

    public static void registerMarker(RaidCrystalBlockEntity blockEntity, World world) {
        if (jmApi == null) {
            return;
        }

        if (!jmApi.playerAccepts(MODID, DisplayType.Marker)) {
            return;
        }

        if (blockEntity == null) {
            return;
        }

        UUID uuid = blockEntity.getUuid();

        if (trackedMarkers.containsKey(uuid)) {
            return;
        }

        BlockPos pos = blockEntity.getPos();
        var boss = blockEntity.getRaidBoss();
        if (boss == null) {
            return;
        }

        var raidType = boss.getType();

        int diameter = 36;
        var element = ElementalTypes.get(raidType.name());
        int offset = element.getTextureXMultiplier();

        MapImage typeIcon = new MapImage(Identifier.of(Cobblemon.MODID, "textures/gui/types.png"), diameter * offset, diameter, diameter, diameter, 0xffffff, 1f);
        typeIcon.centerAnchors();

        MarkerOverlay marker = new MarkerOverlay(MODID, pos, typeIcon);

        marker.setDimension(world.getRegistryKey());
        try {
            jmApi.show(marker);
            trackedMarkers.put(uuid, marker);
        } catch (Exception e) {
            LOGGER.error("Can't add marker overlay", e);
        }
    }

    public static void unregisterMarker(RaidCrystalBlockEntity blockEntity, World world) {
        if (jmApi == null) {
            return;
        }
        UUID uuid = blockEntity.getUuid();

        try {
            var marker = trackedMarkers.remove(uuid);
            jmApi.remove(marker);
        } catch (Exception e) {
            LOGGER.error("Can't remove marker overlay", e);
        }
    }
}
