package com.ludwici.cri.plugins.xaero;

import com.ludwici.cri.client.RaidMarkerTexts;
import com.ludwici.cri.CobblemonRaidDenIcons;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.core.XaeroWorldMapCore;
import xaero.map.gui.GuiMap;
import xaero.map.mods.SupportMods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XaeroMarkerManager {
    public static final XaeroMarkerManager INSTANCE = new XaeroMarkerManager();
    public static final ResourceLocation ORIGIN = CobblemonRaidDenIcons.id("raid_marker");
    private static final Map<BlockPos, Marker> trackedMarkers = new HashMap<>();
    private static boolean initializing;

    public XaeroMarkerManager() {}

    public RaidBossHolder getHolder(BlockPos pos) {
        Marker marker = trackedMarkers.get(pos);
        return marker == null ? null : marker.holder();
    }

    public static boolean isReady() {
        return getWaypointSet(false) != null;
    }

    public static boolean isInitializing() {
        return initializing;
    }

    public static boolean isRegistered(BlockPos pos) {
        Marker marker = trackedMarkers.get(pos);
        WaypointSet currentSet = getWaypointSet(false);
        if (marker == null || currentSet == null || marker.set() != currentSet) {
            return false;
        }
        var boss = ClientRaidRegistry.getRaidBoss(marker.holder().bossId());
        if (boss == null || !marker.waypoint().getName().equals(RaidMarkerTexts.displayName(boss.getDisplaySpecies().getTranslatedName(), marker.holder().adventure()))) {
            return false;
        }
        for (Waypoint waypoint : currentSet.getWaypoints()) {
            if (waypoint == marker.waypoint()) {
                return true;
            }
        }
        return false;
    }

    public static boolean registerMarker(RaidCrystalBlockEntity blockEntity, RaidBossHolder holder) {
        if (blockEntity == null) {
            return false;
        }
        var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
        if (boss == null) {
            return false;
        }
        WaypointSet set = getWaypointSet(true);
        if (set == null) {
            return false;
        }

        BlockPos pos = blockEntity.getBlockPos();
        unregisterMarker(pos);
        ArrayList<Waypoint> previous = new ArrayList<>();
        set.getWaypoints().forEach(previous::add);
        for (Waypoint existing : previous) {
            if (ORIGIN.equals(existing.getThirdPartyOrigin()) && samePos(pos, existing)) {
                set.remove(existing);
            }
        }

        Waypoint waypoint = new Waypoint(pos.getX(), pos.getY(), pos.getZ(), RaidMarkerTexts.displayName(boss.getDisplaySpecies().getTranslatedName(), holder.adventure()), holder.adventure() ? "A" : "S", WaypointColor.AQUA);
        waypoint.setTemporary(true);
        waypoint.setThirdPartyOrigin(ORIGIN);
        set.add(waypoint);
        trackedMarkers.put(pos.immutable(), new Marker(holder, waypoint, set));
        SupportMods.xaeroMinimap.requestWaypointsRefresh();
        return true;
    }

    private static boolean samePos(BlockPos pos, Waypoint waypoint) {
        return pos.getX() == waypoint.getX() && pos.getY() == waypoint.getY() && pos.getZ() == waypoint.getZ();
    }

    private static WaypointSet getWaypointSet(boolean initialize) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || SupportMods.xaeroMinimap == null) {
            return null;
        }
        XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
        if (session == null || session.getWaypointsManager() == null) {
            return null;
        }
        WaypointWorld world = session.getWaypointsManager().getCurrentWorld();
        if ((world == null || !minecraft.level.dimension().equals(world.getDimId())) && initialize) {
            initWaypoints();
            world = session.getWaypointsManager().getCurrentWorld();
        }
        if (world == null || !minecraft.level.dimension().equals(world.getDimId())) {
            return null;
        }
        return world.getWaypointSet("gui.xaero_default");
    }

    private static void initWaypoints() {
        Minecraft minecraft = Minecraft.getInstance();
        WorldMapSession currentSession = XaeroWorldMapCore.currentSession;
        if (minecraft.level == null || minecraft.player == null || currentSession == null) {
            return;
        }
        ResourceKey<Level> dimension = minecraft.level.dimension();
        MapProcessor mapProcessor = currentSession.getMapProcessor();
        if (mapProcessor == null || mapProcessor.getMapWorld() == null) {
            return;
        }
        GuiMap guiMap = new GuiMap(null, null, mapProcessor, minecraft.player);
        initializing = true;
        try {
            SupportMods.xaeroMinimap.checkWaypoints(mapProcessor.getMapWorld().isMultiplayer(), dimension, "", guiMap.width, guiMap.height, guiMap, mapProcessor.getMapWorld(), mapProcessor.getWorldDimensionTypeRegistry());
        } finally {
            initializing = false;
        }
    }

    public static void unregisterMarker(BlockPos pos) {
        Marker marker = trackedMarkers.remove(pos);
        if (marker != null) {
            marker.set().remove(marker.waypoint());
            if (SupportMods.xaeroMinimap != null) {
                SupportMods.xaeroMinimap.requestWaypointsRefresh();
            }
        }
    }

    private record Marker(RaidBossHolder holder, Waypoint waypoint, WaypointSet set) {}
}
