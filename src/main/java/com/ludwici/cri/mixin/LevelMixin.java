package com.ludwici.cri.mixin;

import com.ludwici.cri.network.RaidBlockDespawnS2CPayload;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "removeBlockEntity", at = @At("HEAD"))
    private void onRemoveRaidCrystal(BlockPos pos, CallbackInfo ci) {
        Level self = (Level) (Object) this;

        if (self.isClientSide()) {
            return;
        }
        BlockEntity blockEntity = self.getBlockEntity(pos);

        if (blockEntity instanceof RaidCrystalBlockEntity) {
            RaidBlockDespawnS2CPayload payload = new RaidBlockDespawnS2CPayload(blockEntity.getBlockPos());
            ChunkPos chunkPos = new ChunkPos(pos);
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) self, chunkPos, payload);
        }
    }
}
