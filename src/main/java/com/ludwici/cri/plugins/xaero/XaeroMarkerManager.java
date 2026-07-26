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

import static com.ludwici.cri.CobblemonRaidIcons.LOGGER;

public class XaeroMarkerManager {
    public static final XaeroMarkerManager INSTANCE = new XaeroMarkerManager();

    private static final Map<String, RaidBossHolder> trackedMarkers = new HashMap<>();

    public XaeroMarkerManager() {}

    public RaidBossHolder getHolder(String pos) {
        return trackedMarkers.get(pos);
    }

    public static void registerMarker(RaidCrystalBlockEntity blockEntity, RaidBossHolder holder) {
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

        trackedMarkers.put(posStr, holder);

        Waypoint waypoint = new Waypoint(pos.getX(), pos.getY(), pos.getZ(), boss.getDisplaySpecies().getTranslatedName().getString(), "S", WaypointColor.AQUA);

        SupportXaeroMinimapAccessor minimap = (SupportXaeroMinimapAccessor) SupportMods.xaeroMinimap;
        if (minimap.waypointWorld() == null) {
            initWaypoints();
        }
//        if (minimap.waypointWorld().getWaypointSet("gui.xaero_default").isEmpty()) {
//            minimap.waypointWorld().addWaypointSet("gui.xaero_default");
//        }

        var wWorld = minimap.waypointWorld();
        if (wWorld == null) {
            return;
        }

        wWorld.getCurrentWaypointSet().add(waypoint);

//        var waypointSet = minimap.waypointWorld().getWaypointSet("gui.xaero_default");
//        waypointSet.add(waypoint);
    }

    private static void initWaypoints() {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceKey<Level> dimension = Minecraft.getInstance().level.dimension();
        WorldMapSession currentSession = XaeroWorldMapCore.currentSession;
        MapProcessor mapProcessor = currentSession.getMapProcessor();
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
