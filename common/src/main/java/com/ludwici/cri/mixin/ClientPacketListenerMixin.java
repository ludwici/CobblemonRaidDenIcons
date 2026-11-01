package com.ludwici.cri.mixin;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.necro.raid.dens.common.blocks.entity.RaidCrystalBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleBlockEntityData", at = @At("TAIL"))
    private void cri$onBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getBlockEntity(packet.getPos()) instanceof RaidCrystalBlockEntity) {
            RaidMarkerLifecycle.blockEntityUpdated(level, packet.getPos());
        }
    }
}
