package com.ludwici.cri.mixin;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin {
    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void cri$refreshRaidMarkers(ResourceManager resourceManager, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null) {
            minecraft.execute(RaidMarkerLifecycle::refreshLocalizedMarkers);
        }
    }
}
