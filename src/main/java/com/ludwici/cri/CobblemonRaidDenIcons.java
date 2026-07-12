package com.ludwici.cri;

import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.events.RaidEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(CobblemonRaidDenIcons.MODID)
public class CobblemonRaidDenIcons {
    public static final String MODID = "cri";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CobblemonRaidDenIcons(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        RaidEvents.RAID_DEN_SPAWN.subscribe(raidDenSpawnEvent -> {
            BlockPos pos = raidDenSpawnEvent.getBlockPos();
            BlockEntity blockEntity = raidDenSpawnEvent.getLevel().getBlockEntity(pos);
            if (blockEntity instanceof RaidCrystalBlockEntity) {
                var rb = raidDenSpawnEvent.getRaidBoss();
                RaidBossHolder holder = new RaidBossHolder(rb.getId(), rb.getType());
                RaidBlockSpawnS2CPayload payload = new RaidBlockSpawnS2CPayload(pos, holder);
                ChunkPos chunkPos = new ChunkPos(pos);
                PacketDistributor.sendToPlayersTrackingChunk(raidDenSpawnEvent.getLevel(), chunkPos, payload);
            }
        });

//        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
