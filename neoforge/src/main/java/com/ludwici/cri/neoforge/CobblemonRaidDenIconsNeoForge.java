package com.ludwici.cri.neoforge;

import com.ludwici.cri.CobblemonRaidDenIcons;
import com.ludwici.cri.CriPlatform;
import com.ludwici.cri.ModCompat;
import com.ludwici.cri.config.CriConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(CobblemonRaidDenIcons.MODID)
public final class CobblemonRaidDenIconsNeoForge {
    public CobblemonRaidDenIconsNeoForge(IEventBus modBus, ModContainer container) {
        ModCompat.init();
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, NeoConfig.SPEC);
        CriConfig.setIconSizeSupplier(NeoConfig::iconSize);
        modBus.addListener(NeoPayloads::register);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoClient.init(modBus, container);
        }

        CriPlatform.installSpawnSender((level, blockEntity, payload) -> {
            ChunkPos chunkPos = new ChunkPos(blockEntity.getBlockPos());
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunkPos, payload);
        });
        CobblemonRaidDenIcons.init();
    }
}
