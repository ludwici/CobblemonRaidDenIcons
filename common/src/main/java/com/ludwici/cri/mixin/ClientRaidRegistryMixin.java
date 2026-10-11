package com.ludwici.cri.mixin;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import com.necro.raid.dens.common.client.ClientRaidRegistry;
import com.necro.raid.dens.common.data.raid.RaidBoss;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientRaidRegistry.class, remap = false)
public class ClientRaidRegistryMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void cri$onRegister(RaidBoss boss, CallbackInfo ci) {
        RaidMarkerLifecycle.bossRegistered(boss.getId());
    }

    @Inject(method = "remove", at = @At("TAIL"))
    private static void cri$onRemove(ResourceLocation bossId, CallbackInfo ci) {
        RaidMarkerLifecycle.bossRemoved(bossId);
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private static void cri$onClear(CallbackInfo ci) {
        RaidMarkerLifecycle.registryCleared();
    }
}
