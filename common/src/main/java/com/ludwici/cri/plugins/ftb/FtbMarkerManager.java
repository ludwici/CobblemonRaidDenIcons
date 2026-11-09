package com.ludwici.cri.plugins.ftb;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.ludwici.cri.client.RaidMarkerTexts;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import dev.ftb.mods.ftbchunks.api.client.event.WaypointManagerEvent;
import dev.ftb.mods.ftbchunks.api.client.waypoint.Waypoint;
import dev.ftb.mods.ftbchunks.api.client.waypoint.WaypointManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;

public final class FtbMarkerManager {
    private static final Map<BlockPos, Marker> trackedMarkers = new HashMap<>();
    private static WaypointManager manager;
    private static ClientLevel managerLevel;
    private static boolean listening;

    private FtbMarkerManager() {}

    public static void init() {
        if (listening) {
            return;
        }
        try {
            WaypointManagerEvent.AVAILABLE.register(FtbMarkerManager::managerAvailable);
            listening = true;
        } catch (LinkageError e) {
            LOGGER.warn("FTB Chunks waypoint integration requires version 2101.1.10 or newer", e);
        }
    }

    private static void managerAvailable(WaypointManager available) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.isSameThread()) {
            minecraft.execute(() -> managerAvailable(available));
            return;
        }
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        if (manager == available && managerLevel == level) {
            return;
        }
        trackedMarkers.clear();
        manager = available;
        managerLevel = level;
        RaidMarkerLifecycle.markerApiReady();
    }

    public static void levelChanged(ClientLevel level) {
        trackedMarkers.clear();
        if (managerLevel != level) {
            manager = null;
            managerLevel = null;
        }
    }

    public static boolean isReady() {
        return manager != null && managerLevel != null && Minecraft.getInstance().level == managerLevel;
    }

    public static boolean isRegistered(BlockPos pos) {
        Marker marker = trackedMarkers.get(pos);
        return marker != null && marker.manager() == manager && manager != null && manager.getAllWaypoints().stream().anyMatch(waypoint -> waypoint == marker.waypoint());
    }

    public static boolean registerMarker(RaidCrystalBlockEntity blockEntity, Level level, RaidBossHolder holder) {
        if (!isReady() || blockEntity == null || Minecraft.getInstance().level != level) {
            return false;
        }
        var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
        if (boss == null) {
            return false;
        }

        BlockPos pos = blockEntity.getBlockPos();
        unregisterMarker(pos);
        for (Waypoint waypoint : manager.getAllWaypoints()) {
            if (waypoint.getPos().equals(pos)) {
                return false;
            }
        }

        String title = RaidMarkerTexts.detailedTitle(boss.getDisplaySpecies().getTranslatedName(), holder.adventure(), holder.raidType(), holder.stars());
        Waypoint waypoint = manager.addTransientWaypointAt(pos, title);
        if (manager.getAllWaypoints().stream().noneMatch(existing -> existing == waypoint)) {
            return false;
        }
        waypoint.setColor(holder.raidType().getColor() & 0xFFFFFF);
        hideWorldHud(waypoint);
        trackedMarkers.put(pos.immutable(), new Marker(manager, waypoint));
        return true;
    }

    public static void unregisterMarker(BlockPos pos) {
        Marker marker = trackedMarkers.remove(pos);
        if (marker == null) {
            return;
        }
        if (marker.manager().getAllWaypoints().stream().anyMatch(existing -> existing == marker.waypoint())) {
            marker.manager().removeWaypoint(marker.waypoint());
        }
    }

    private static void hideWorldHud(Waypoint waypoint) {
        try {
            Field drawDistance = waypoint.getClass().getField("inWorldDistance");
            if (drawDistance.getType() == double.class) {
                drawDistance.setDouble(waypoint, -1.0);
            }
        } catch (ReflectiveOperationException | SecurityException e) {
            LOGGER.debug("FTB Chunks world waypoint distance override is unavailable", e);
        }
    }

    private record Marker(WaypointManager manager, Waypoint waypoint) {}
}
