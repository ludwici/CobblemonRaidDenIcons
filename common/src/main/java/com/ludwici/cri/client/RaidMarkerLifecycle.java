package com.ludwici.cri.client;

import com.ludwici.cri.CriPlatform;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.network.RaidBossHolder;
import com.ludwici.cri.plugins.diorama.DioramaMarkerManager;
import com.ludwici.cri.plugins.ftb.FtbMarkerManager;
import com.ludwici.cri.plugins.xaero.XaeroMarkerManager;
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
import java.util.Objects;

public final class RaidMarkerLifecycle {
    private static final Map<BlockPos, MarkerEntry> entries = new HashMap<>();
    private static ClientLevel trackedLevel;
    private static boolean serverAccess;

    private RaidMarkerLifecycle() {}

    public static void setServerAccess(boolean allowed) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.isSameThread()) {
            minecraft.execute(() -> setServerAccess(allowed));
            return;
        }
        if (serverAccess == allowed) {
            return;
        }
        serverAccess = allowed;
        if (!allowed) {
            for (Map.Entry<BlockPos, MarkerEntry> tracked : entries.entrySet()) {
                MarkerEntry entry = tracked.getValue();
                if (entry.active != null) {
                    MarkerDispatcher.unregister(tracked.getKey());
                    entry.active = null;
                }
                entry.packetHolder = null;
                entry.refreshRequested = false;
            }
            return;
        }
        markerApiReady();
    }

    public static void track(ClientLevel level, BlockPos pos, RaidBossHolder holder) {
        if (level == null || Minecraft.getInstance().level != level) {
            return;
        }
        if (!ModCompat.JOURNEYMAP_LOADED && !ModCompat.XAERO_WORLD_MAP_LOADED && !ModCompat.DIORAMA_LOADED && !ModCompat.FTB_CHUNKS_LOADED) {
            return;
        }
        changeLevel(level);
        MarkerEntry entry = entries.computeIfAbsent(pos.immutable(), ignored -> new MarkerEntry());
        if (holder != null && serverAccess) {
            entry.packetHolder = holder;
            entry.refreshRequested = true;
        } else {
            if (!entry.bossObserved && level.getBlockEntity(pos) instanceof RaidCrystalBlockEntity blockEntity) {
                entry.observedBossId = blockEntity.getRaidBossLocation();
                entry.bossObserved = true;
            }
            if (serverAccess && !entry.refreshRequested) {
                entry.refreshRequested = CriPlatform.requestRefresh(pos);
            }
        }
        update(level, pos, entry);
    }

    public static void blockEntityUpdated(ClientLevel level, BlockPos pos) {
        if (level == null || Minecraft.getInstance().level != level) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof RaidCrystalBlockEntity blockEntity)) {
            return;
        }
        changeLevel(level);
        MarkerEntry entry = entries.computeIfAbsent(pos.immutable(), ignored -> new MarkerEntry());
        ResourceLocation bossId = blockEntity.getRaidBossLocation();
        if (!entry.bossObserved) {
            entry.observedBossId = bossId;
            entry.bossObserved = true;
        } else if (!Objects.equals(entry.observedBossId, bossId)) {
            entry.observedBossId = bossId;
            entry.packetHolder = null;
            entry.refreshRequested = false;
        }
        track(level, pos, null);
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
            if (ModCompat.XAERO_WORLD_MAP_LOADED && !ModCompat.JOURNEYMAP_LOADED && !ModCompat.DIORAMA_LOADED && !XaeroMarkerManager.isReady()) {
                return;
            }
            for (Map.Entry<BlockPos, MarkerEntry> tracked : entries.entrySet()) {
                BlockPos pos = tracked.getKey();
                MarkerEntry entry = tracked.getValue();
                if (entry.active != null && ModCompat.XAERO_WORLD_MAP_LOADED && !ModCompat.JOURNEYMAP_LOADED && XaeroMarkerManager.isReady() && !XaeroMarkerManager.isRegistered(pos)) {
                    MarkerDispatcher.unregister(pos);
                    entry.active = null;
                }
                if (entry.active != null && ModCompat.DIORAMA_LOADED && DioramaMarkerManager.isReady() && !DioramaMarkerManager.isRegistered(pos)) {
                    MarkerDispatcher.unregister(pos);
                    entry.active = null;
                }
                if (entry.active != null && ModCompat.FTB_CHUNKS_LOADED && FtbMarkerManager.isReady() && !FtbMarkerManager.isRegistered(pos)) {
                    MarkerDispatcher.unregister(pos);
                    entry.active = null;
                }
                update(level, pos, entry);
                if (serverAccess && !entry.refreshRequested) {
                    entry.refreshRequested = CriPlatform.requestRefresh(pos);
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
        if (ModCompat.FTB_CHUNKS_LOADED) {
            FtbMarkerManager.levelChanged();
        }
        if (level == null) {
            serverAccess = false;
        }
    }

    private static ResourceLocation getBossId(ClientLevel level, BlockPos pos, MarkerEntry entry) {
        if (entry.packetHolder != null) {
            return entry.packetHolder.bossId();
        }
        if (level.getBlockEntity(pos) instanceof RaidCrystalBlockEntity blockEntity) {
            return blockEntity.getRaidBossLocation();
        }
        return null;
    }

    private static void update(ClientLevel level, BlockPos pos, MarkerEntry entry) {
        if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return;
        }
        if (!serverAccess) {
            if (entry.active != null) {
                MarkerDispatcher.unregister(pos);
                entry.active = null;
            }
            return;
        }
        if (!(level.getBlockState(pos).getBlock() instanceof RaidCrystalBlock)) {
            if (entry.active != null) {
                MarkerDispatcher.unregister(pos);
                entry.active = null;
            }
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof RaidCrystalBlockEntity blockEntity)) {
            return;
        }
        ResourceLocation bossId = getBossId(level, pos, entry);
        if (entry.active != null && (bossId == null || !entry.active.bossId().equals(bossId))) {
            MarkerDispatcher.unregister(pos);
            entry.active = null;
        }
        if (bossId == null) {
            return;
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
            boolean xaeroReady = ModCompat.JOURNEYMAP_LOADED || !ModCompat.XAERO_WORLD_MAP_LOADED || !XaeroMarkerManager.isReady() || XaeroMarkerManager.isRegistered(pos);
            boolean dioramaReady = !ModCompat.DIORAMA_LOADED || !DioramaMarkerManager.isReady() || DioramaMarkerManager.isRegistered(pos);
            boolean ftbReady = !ModCompat.FTB_CHUNKS_LOADED || !FtbMarkerManager.isReady() || FtbMarkerManager.isRegistered(pos);
            if (xaeroReady && dioramaReady && ftbReady) {
                return;
            }
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
        private ResourceLocation observedBossId;
        private boolean bossObserved;
        private boolean refreshRequested;
    }
}
