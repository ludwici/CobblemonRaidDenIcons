package com.ludwici.cri.mixin;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
    @Inject(method = "setBlockEntity", at = @At("TAIL"))
    private void cri$onBlockEntityAdded(BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof RaidCrystalBlockEntity && ((LevelChunk) (Object) this).getLevel() instanceof ClientLevel level) {
            RaidMarkerLifecycle.blockEntityUpdated(level, blockEntity.getBlockPos());
        }
    }
}
