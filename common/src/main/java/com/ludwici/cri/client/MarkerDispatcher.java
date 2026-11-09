package com.ludwici.cri.client;

import com.ludwici.cri.ModCompat;
import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import com.ludwici.cri.plugins.ftb.FtbMarkerManager;
import com.ludwici.cri.plugins.journey.JourneyMarkerManager;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class MarkerDispatcher {
    private MarkerDispatcher() {}

    public static boolean register(RaidCrystalBlockEntity blockEntity, Level level, RaidBossHolder holder) {
        boolean registered = false;
        if (ModCompat.JOURNEYMAP_LOADED) {
            registered = JourneyMarkerManager.registerMarker(blockEntity, level, holder);
        } else if (ModCompat.XAERO_WORLD_MAP_LOADED) {
            registered = XaeroMarkerManager.registerMarker(blockEntity, holder);
        }
        if (ModCompat.DIORAMA_LOADED) {
            registered = DioramaMarkerManager.registerMarker(blockEntity, level, holder) || registered;
        }
        if (ModCompat.FTB_CHUNKS_LOADED) {
            registered = FtbMarkerManager.registerMarker(blockEntity, level, holder) || registered;
        }
        return registered;
    }

    public static void unregister(BlockPos pos) {
        if (ModCompat.JOURNEYMAP_LOADED) {
            JourneyMarkerManager.unregisterMarker(pos);
        } else if (ModCompat.XAERO_WORLD_MAP_LOADED) {
            XaeroMarkerManager.unregisterMarker(pos);
        }
        if (ModCompat.DIORAMA_LOADED) {
            DioramaMarkerManager.unregisterMarker(pos);
        }
        if (ModCompat.FTB_CHUNKS_LOADED) {
            FtbMarkerManager.unregisterMarker(pos);
        }
    }
}
