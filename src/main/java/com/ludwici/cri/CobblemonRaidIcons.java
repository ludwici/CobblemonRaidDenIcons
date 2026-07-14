package com.ludwici.cri;

import com.ludwici.cri.network.Payloads;
import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.ludwici.cri.network.RaidBlockSpawnS2CPayload;
import com.ludwici.cri.network.RaidBossHolder;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.events.RaidEvents;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonRaidIcons implements ModInitializer {
	public static final String MODID = "cri";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		Payloads.register();

		RaidEvents.RAID_DEN_SPAWN.subscribe(raidDenSpawnEvent -> {
			BlockEntity blockEntity = raidDenSpawnEvent.getLevel().getBlockEntity(raidDenSpawnEvent.getBlockPos());
			if (blockEntity instanceof RaidCrystalBlockEntity) {
				var rb = raidDenSpawnEvent.getRaidBoss();
				String stars;
				try {
					stars = rb.getTier().getStars();
				} catch (MatchException e) {
					stars = "";
				}
				RaidBossHolder holder = new RaidBossHolder(rb.getId(), rb.getType(), stars);
				RaidBlockSpawnS2CPayload payload = new RaidBlockSpawnS2CPayload(raidDenSpawnEvent.getBlockPos(), holder);
				for (ServerPlayer player : PlayerLookup.tracking(blockEntity)) {
					ServerPlayNetworking.send(player, payload);
				}
			}
		});

		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof RaidCrystalBlockEntity) {
				RaidBlockDespawnS2CPayload payload = new RaidBlockDespawnS2CPayload(blockEntity.getBlockPos());
				for (ServerPlayer player : PlayerLookup.tracking(blockEntity)) {
					ServerPlayNetworking.send(player, payload);
				}
			}
		});
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
