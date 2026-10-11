package com.ludwici.cri.client;

import com.ludwici.cri.ModCompat;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.block.RaidCrystalBlock;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class RaidMarkerLifecycle {
    private static final Map<BlockPos, MarkerEntry> entries = new HashMap<>();
    private static ClientLevel trackedLevel;

    private RaidMarkerLifecycle() {}

    public static void track(ClientLevel level, BlockPos pos, RaidBossHolder holder) {
        if (level == null || Minecraft.getInstance().level != level) {
            return;
        }
        if (!ModCompat.JOURNEYMAP_LOADED && !ModCompat.XAERO_WORLD_MAP_LOADED) {
            return;
        }
        changeLevel(level);
        MarkerEntry entry = entries.computeIfAbsent(pos.immutable(), ignored -> new MarkerEntry());
        if (holder != null) {
            entry.packetHolder = holder;
        }
        update(level, pos, entry);
    }

    public static void bossRegistered(ResourceLocation bossId) {
        Minecraft.getInstance().execute(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null || trackedLevel != level) {
                return;
            }
            for (Map.Entry<BlockPos, MarkerEntry> entry : entries.entrySet()) {
                BlockPos pos = entry.getKey();
                if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    continue;
                }
                if (bossId.equals(getBossId(level, pos, entry.getValue()))) {
                    update(level, pos, entry.getValue());
                }
            }
        });
    }

    public static void bossRemoved(ResourceLocation bossId) {
        Minecraft.getInstance().execute(() -> {
            for (Map.Entry<BlockPos, MarkerEntry> entry : entries.entrySet()) {
                if (entry.getValue().active == null || !bossId.equals(entry.getValue().active.bossId())) {
                    continue;
                }
                MarkerDispatcher.unregister(entry.getKey());
                entry.getValue().active = null;
            }
        });
    }

    public static void registryCleared() {
        Minecraft.getInstance().execute(() -> {
            for (Map.Entry<BlockPos, MarkerEntry> entry : entries.entrySet()) {
                if (entry.getValue().active == null) {
                    continue;
                }
                MarkerDispatcher.unregister(entry.getKey());
                entry.getValue().active = null;
            }
        });
    }

    public static void markerApiReady() {
        Minecraft.getInstance().execute(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null || trackedLevel != level) {
                return;
            }
            for (Map.Entry<BlockPos, MarkerEntry> entry : entries.entrySet()) {
                if (entry.getValue().active == null) {
                    update(level, entry.getKey(), entry.getValue());
                }
            }
        });
    }

    public static void untrack(ClientLevel level, BlockPos pos) {
        if (level == null || trackedLevel != level) {
            return;
        }
        MarkerEntry entry = entries.remove(pos);
        if (entry != null && entry.active != null) {
            MarkerDispatcher.unregister(pos);
        }
    }

    public static void chunkUnloaded(ClientLevel level, ChunkPos chunkPos) {
        if (level == null || trackedLevel != level) {
            return;
        }
        Iterator<Map.Entry<BlockPos, MarkerEntry>> iterator = entries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, MarkerEntry> entry = iterator.next();
            BlockPos pos = entry.getKey();
            if ((pos.getX() >> 4) != chunkPos.x || (pos.getZ() >> 4) != chunkPos.z) {
                continue;
            }
            if (entry.getValue().active != null) {
                MarkerDispatcher.unregister(pos);
            }
            iterator.remove();
        }
    }

    public static void changeLevel(ClientLevel level) {
        if (trackedLevel == level) {
            return;
        }
        for (Map.Entry<BlockPos, MarkerEntry> entry : entries.entrySet()) {
            if (entry.getValue().active != null) {
                MarkerDispatcher.unregister(entry.getKey());
            }
        }
        entries.clear();
        trackedLevel = level;
    }

    private static ResourceLocation getBossId(ClientLevel level, BlockPos pos, MarkerEntry entry) {
        if (level.getBlockEntity(pos) instanceof RaidCrystalBlockEntity blockEntity) {
            ResourceLocation bossId = blockEntity.getRaidBossLocation();
            if (bossId != null) {
                return bossId;
            }
        }
        return entry.packetHolder == null ? null : entry.packetHolder.bossId();
    }

    private static void update(ClientLevel level, BlockPos pos, MarkerEntry entry) {
        if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return;
        }
        if (!(level.getBlockState(pos).getBlock() instanceof RaidCrystalBlock)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof RaidCrystalBlockEntity blockEntity)) {
            return;
        }
        ResourceLocation bossId = getBossId(level, pos, entry);
        if (bossId == null) {
            return;
        }
        if (entry.active != null && !entry.active.bossId().equals(bossId)) {
            MarkerDispatcher.unregister(pos);
            entry.active = null;
        }
        var boss = ClientRaidRegistry.getRaidBoss(bossId);
        if (boss == null) {
            return;
        }

        RaidBossHolder holder = entry.packetHolder;
        if (holder == null || !holder.bossId().equals(bossId)) {
            String stars;
            try {
                stars = boss.getTier().getStars();
            } catch (MatchException ignored) {
                stars = "";
            }
            holder = new RaidBossHolder(boss.getId(), boss.getType(), stars);
        }
        if (holder.equals(entry.active)) {
            return;
        }
        if (entry.active != null) {
            MarkerDispatcher.unregister(pos);
            entry.active = null;
        }
        if (MarkerDispatcher.register(blockEntity, level, holder)) {
            entry.active = holder;
        }
    }

    private static final class MarkerEntry {
        private RaidBossHolder packetHolder;
        private RaidBossHolder active;
    }
}
