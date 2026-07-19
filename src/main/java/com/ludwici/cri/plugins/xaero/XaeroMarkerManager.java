package com.ludwici.cri.plugins.xaero;

import com.ludwici.cri.mixin.xaero.SupportXaeroMinimapAccessor;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.minecraft.core.BlockPos;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.hud.minimap.waypoint.WaypointColor;
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

//        WorldMapSession currentSession = XaeroWorldMapCore.currentSession;

//        GuiMap guiMap = new GuiMap(null, null, currentSession.getMapProcessor(), Minecraft.getInstance().player);
//        ResourceKey<Level> dimension = Minecraft.getInstance().level.dimension();
//        SupportMods.xa
        Waypoint waypoint = new Waypoint(pos.getX(), pos.getY(), pos.getZ(), "Boss", "S", WaypointColor.AQUA);

//        Waypoint waypoint = new Waypoint(pos, false, "Boss", 200);
        SupportXaeroMinimapAccessor minimap = (SupportXaeroMinimapAccessor) SupportMods.xaeroMinimap;
        if (minimap.waypointWorld() == null) {
            LOGGER.error("waypointWorld is null");
            return;
        }
        if (!minimap.waypointWorld().getSets().containsKey("gui.xaero_default")) {
            minimap.waypointWorld().addSet("gui.xaero_default");
        }

        WaypointSet waypointSet = minimap.waypointWorld().getSets().get("gui.xaero_default");
        waypointSet.add(waypoint);
    }

    public static void unregisterMarker(BlockPos pos) {
        String posStr = pos.toShortString();
        try {
            var marker = trackedMarkers.remove(posStr);
        } catch (Exception e) {
            LOGGER.error("Can't remove marker overlay", e);
        }

    }
}
