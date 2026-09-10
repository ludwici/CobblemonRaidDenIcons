package com.ludwici.cri.client;

import com.ludwici.cri.ModCompat;
import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class MarkerDispatcher {
    private MarkerDispatcher() {
    }

    public static void register(RaidCrystalBlockEntity blockEntity, Level level, RaidBossHolder holder) {
        if (ModCompat.JOURNEYMAP_LOADED) {
            JourneyMarkerManager.registerMarker(blockEntity, level, holder);
        } else if (ModCompat.XAERO_WORLD_MAP_LOADED) {
            XaeroMarkerManager.registerMarker(blockEntity, holder);
        }
    }

    public static void unregister(BlockPos pos) {
        if (ModCompat.JOURNEYMAP_LOADED) {
            JourneyMarkerManager.unregisterMarker(pos);
        } else if (ModCompat.XAERO_WORLD_MAP_LOADED) {
            XaeroMarkerManager.unregisterMarker(pos);
        }
    }
}
