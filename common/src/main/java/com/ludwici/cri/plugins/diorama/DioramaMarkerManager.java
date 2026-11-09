package com.ludwici.cri.plugins.diorama;

import com.ludwici.cri.client.RaidMarkerTexts;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import com.necro.raid.dens.common.data.raid.RaidType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ludwici.cri.CobblemonRaidDenIcons.LOGGER;

public final class DioramaMarkerManager {
    private static final Map<BlockPos, Marker> trackedMarkers = new HashMap<>();
    private static final Set<Object> ownedWaypoints = Collections.newSetFromMap(new IdentityHashMap<>());
    private static Api api;
    private static boolean attempted;
    private static boolean updating;

    private DioramaMarkerManager() {}

    public static boolean isUpdating() {
        return updating;
    }

    public static void filterMinimapPins(List<?> pins) {
        if (ownedWaypoints.isEmpty()) {
            return;
        }
        Api bridge = api();
        if (bridge == null) {
            return;
        }
        try {
            Iterator<?> iterator = pins.iterator();
            while (iterator.hasNext()) {
                Object pin = iterator.next();
                Object waypoint = bridge.pinWaypoint.invoke(pin);
                if (!ownedWaypoints.contains(waypoint)) {
                    continue;
                }
                if ((boolean) bridge.pinClamped.invoke(pin)) {
                    iterator.remove();
                }
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot filter Diorama minimap markers", e);
        }
    }

    public static boolean isRaidPin(Object pin) {
        if (pin == null || ownedWaypoints.isEmpty()) {
            return false;
        }
        Api bridge = api();
        if (bridge == null) {
            return false;
        }
        try {
            return ownedWaypoints.contains(bridge.pinWaypoint.invoke(pin));
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot inspect Diorama waypoint pin", e);
            return false;
        }
    }

    public static <T> List<T> visibleWorldWaypoints(List<T> waypoints) {
        if (ownedWaypoints.isEmpty()) {
            return waypoints;
        }
        return waypoints.stream().filter(waypoint -> !ownedWaypoints.contains(waypoint)).toList();
    }

    public static boolean isReady() {
        Api bridge = api();
        if (bridge == null) {
            return false;
        }
        boolean previous = updating;
        updating = true;
        try {
            return bridge.storeGet.invoke(null) != null;
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot access Diorama waypoint store", e);
            return false;
        } finally {
            updating = previous;
        }
    }

    public static boolean isRegistered(BlockPos pos) {
        Marker marker = trackedMarkers.get(pos);
        if (marker == null) {
            return false;
        }
        Api bridge = api();
        if (bridge == null) {
            return false;
        }
        boolean previous = updating;
        updating = true;
        try {
            Object store = bridge.storeGet.invoke(null);
            if (store != marker.store()) {
                return false;
            }
            return ((List<?>) bridge.storeAll.invoke(store)).contains(marker.waypoint());
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot inspect Diorama raid markers", e);
            return false;
        } finally {
            updating = previous;
        }
    }

    public static boolean registerMarker(RaidCrystalBlockEntity blockEntity, Level level, RaidBossHolder holder) {
        Api bridge = api();
        if (bridge == null || blockEntity == null || Minecraft.getInstance().level != level) {
            return false;
        }
        var boss = ClientRaidRegistry.getRaidBoss(holder.bossId());
        if (boss == null) {
            return false;
        }

        boolean previous = updating;
        updating = true;
        try {
            Object store = bridge.storeGet.invoke(null);
            if (store == null) {
                return false;
            }
            BlockPos pos = blockEntity.getBlockPos();
            unregisterMarker(pos);
            String stars = ModCompat.SMART_ROTOM_DIORAMA_040 ? holder.stars().replace('★', '*') : holder.stars();
            String title = RaidMarkerTexts.detailedTitle(boss.getDisplaySpecies().getTranslatedName(), holder.adventure(), holder.raidType(), stars);
            int color = 0xFF000000 | holder.raidType().getColor();
            Object waypoint = bridge.constructor.newInstance(title, pos.getX(), pos.getY(), pos.getZ(), level.dimension().location().toString(), color);
            bridge.kind.set(waypoint, bridge.temporary);
            boolean tera = ("tera".equalsIgnoreCase(boss.getFeature()) || holder.raidType() == RaidType.STELLAR) && ModCompat.MEGA_SHOWDOWN_LOADED;
            bridge.initials.set(waypoint, holder.adventure() ? "A" : tera ? "T" : "R");
            if (bridge.icon != null) {
                bridge.icon.set(waypoint, tera ? "cri:tera_raid" : "cri:raid_den");
            }
            bridge.storeAdd.invoke(store, waypoint);
            trackedMarkers.put(pos.immutable(), new Marker(waypoint, store));
            ownedWaypoints.add(waypoint);
            return true;
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot add Diorama raid marker", e);
            return false;
        } finally {
            updating = previous;
        }
    }

    public static void unregisterMarker(BlockPos pos) {
        Marker marker = trackedMarkers.remove(pos);
        Api bridge = api();
        if (marker == null) {
            return;
        }
        ownedWaypoints.remove(marker.waypoint());
        if (bridge == null) {
            return;
        }
        boolean previous = updating;
        updating = true;
        try {
            bridge.storeRemove.invoke(marker.store(), marker.waypoint());
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot remove Diorama raid marker", e);
        } finally {
            updating = previous;
        }
    }

    public static void storeClosed() {
        trackedMarkers.clear();
        ownedWaypoints.clear();
    }

    private static Api api() {
        if (!ModCompat.DIORAMA_LOADED) {
            return null;
        }
        if (!attempted) {
            attempted = true;
            try {
                api = new Api();
            } catch (ReflectiveOperationException | LinkageError e) {
                LOGGER.warn("Diorama waypoint integration is unavailable", e);
            }
        }
        return api;
    }

    private record Marker(Object waypoint, Object store) {}

    private static final class Api {
        private final Method storeGet;
        private final Method storeAdd;
        private final Method storeRemove;
        private final Method storeAll;
        private final Constructor<?> constructor;
        private final Field kind;
        private final Field initials;
        private final Field icon;
        private final Object temporary;
        private final Method pinWaypoint;
        private final Method pinClamped;

        @SuppressWarnings({"unchecked", "rawtypes"})
        private Api() throws ReflectiveOperationException {
            Class<?> storeClass = Class.forName("dev.diorama.waypoint.WaypointStore");
            Class<?> waypointClass = Class.forName("dev.diorama.waypoint.Waypoint");
            this.storeGet = storeClass.getMethod("get");
            this.storeAdd = storeClass.getMethod("add", waypointClass);
            this.storeRemove = storeClass.getMethod("remove", waypointClass);
            this.storeAll = storeClass.getMethod("all");
            this.constructor = waypointClass.getConstructor(String.class, int.class, int.class, int.class, String.class, int.class);
            this.kind = waypointClass.getField("kind");
            this.initials = waypointClass.getField("initials");
            Field iconField;
            try {
                iconField = waypointClass.getField("icon");
            } catch (NoSuchFieldException ignored) {
                iconField = null;
            }
            this.icon = iconField;
            this.temporary = Enum.valueOf((Class<Enum>) kind.getType().asSubclass(Enum.class), "TEMPORARY");
            Class<?> pinClass = Class.forName("dev.diorama.waypoint.WaypointOverlay$Pin");
            this.pinWaypoint = pinClass.getMethod("waypoint");
            this.pinClamped = pinClass.getMethod("clamped");
        }
    }
}
