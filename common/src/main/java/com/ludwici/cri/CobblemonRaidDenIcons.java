package com.ludwici.cri;

import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.events.RaidEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CobblemonRaidDenIcons {
    public static final String MODID = "cri";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    private static boolean initialized;

    private CobblemonRaidDenIcons() {}

    public static synchronized void init() {
        if (initialized) return;
        initialized = true;

        RaidEvents.RAID_DEN_SPAWN.subscribe(event -> {
            BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getBlockPos());
            if (!(blockEntity instanceof RaidCrystalBlockEntity)) return;

            var raidBoss = event.getRaidBoss();
            String stars;
            try {
                stars = raidBoss.getTier().getStars();
            } catch (MatchException ignored) {
                stars = "";
            }

            var holder = new RaidBossHolder(raidBoss.getId(), raidBoss.getType(), stars);
            CriPlatform.sendSpawn(event.getLevel(), blockEntity, new RaidBlockSpawnS2CPayload(event.getBlockPos(), holder));
        });
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
