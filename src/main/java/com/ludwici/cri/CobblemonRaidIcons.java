package com.ludwici.cri;

import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import com.necro.raid.dens.common.events.RaidDenSpawnEvent;
import com.necro.raid.dens.common.events.RaidEvents;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonRaidIcons implements ModInitializer {
	public static final String MODID = "cri";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		RaidEvents.RAID_DEN_SPAWN.subscribe(raidDenSpawnEvent -> {
			RaidCrystalBlockEntity be = (RaidCrystalBlockEntity) raidDenSpawnEvent.getLevel().getBlockEntity(raidDenSpawnEvent.getBlockPos());
			RaidBlockMarkerManager.registerMarker(be, raidDenSpawnEvent.getLevel());
		});

		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			if (blockEntity instanceof RaidCrystalBlockEntity raidCrystalBlock) {
				RaidBlockMarkerManager.unregisterMarker(raidCrystalBlock, world);
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.of(MODID, path);
	}
}
