package com.ludwici.cri.plugins.xaero;

import com.ludwici.cri.mixin.xaero.SupportXaeroMinimapAccessor;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.core.XaeroWorldMapCore;
import xaero.map.gui.GuiMap;
import xaero.map.mods.SupportMods;

import java.util.HashMap;
import java.util.Map;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;

public class XaeroMarkerManager {
    public static final XaeroMarkerManager INSTANCE = new XaeroMarkerManager();

    private static final Map<String, RaidBossHolder> trackedMarkers = new HashMap<>();

    public XaeroMarkerManager() {}

    public RaidBossHolder getHolder(String pos) {
        return trackedMarkers.get(pos);
    }

    public static boolean registerMarker(RaidCrystalBlockEntity blockEntity, RaidBossHolder holder) {
        if (blockEntity == null) {
            LOGGER.error("RaidCrystalBlockEntity is null");
            return false;
        }

        var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
        if (boss == null) {
            LOGGER.error("boss is null");
            return false;
        }

        BlockPos pos = blockEntity.getBlockPos();
        String posStr = pos.toShortString();

        Waypoint waypoint = new Waypoint(pos.getX(), pos.getY(), pos.getZ(), boss.getDisplaySpecies().getTranslatedName().getString(), "S", WaypointColor.AQUA);
        waypoint.setTemporary(true);
        if (SupportMods.xaeroMinimap == null) return false;
        SupportXaeroMinimapAccessor minimap = (SupportXaeroMinimapAccessor) SupportMods.xaeroMinimap;
        if (minimap.waypointWorld() == null) {
            initWaypoints();
        }

        var wWorld = minimap.waypointWorld();
        if (wWorld == null) return false;
        var waypointSet = wWorld.getWaypointSet("gui.xaero_default");
        if (waypointSet == null) return false;
        waypointSet.add(waypoint);
        trackedMarkers.put(posStr, holder);
        return true;

    }

    private static void initWaypoints() {
        Minecraft minecraft = Minecraft.getInstance();
        WorldMapSession currentSession = XaeroWorldMapCore.currentSession;
        if (minecraft.level == null || minecraft.player == null || currentSession == null) return;
        ResourceKey<Level> dimension = minecraft.level.dimension();
        MapProcessor mapProcessor = currentSession.getMapProcessor();
        if (mapProcessor == null || mapProcessor.getMapWorld() == null) return;
        GuiMap guiMap = new GuiMap(null, null, mapProcessor, minecraft.player);
        SupportMods.xaeroMinimap.checkWaypoints(mapProcessor.getMapWorld().isMultiplayer(), dimension, "", guiMap.width, guiMap.height, guiMap, mapProcessor.getMapWorld(), mapProcessor.getWorldDimensionTypeRegistry());
    }

    public static void unregisterMarker(BlockPos pos) {
        String posStr = pos.toShortString();
        try {
            var marker = trackedMarkers.remove(posStr);
            SupportXaeroMinimapAccessor minimap = (SupportXaeroMinimapAccessor) SupportMods.xaeroMinimap;
            if (minimap.waypointWorld() == null) {
                initWaypoints();
            }
            var waypointSet = minimap.waypointWorld().getWaypointSet("gui.xaero_default");
            var set = waypointSet.getWaypoints();
            int slot = 0;
            for (var w : set) {
                var tmpPos = new BlockPos(w.getX(), w.getY(), w.getZ());
                if (pos.equals(tmpPos)) {
                    break;
                }
                slot++;
            }
            waypointSet.remove(slot);
        } catch (Exception e) {
            LOGGER.error("Can't remove marker overlay", e);
        }

    }
}
