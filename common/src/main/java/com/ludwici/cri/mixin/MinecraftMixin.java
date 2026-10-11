package com.ludwici.cri.mixin;

import com.ludwici.cri.client.RaidMarkerLifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "setLevel", at = @At("HEAD"))
    private void cri$onSetLevel(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        RaidMarkerLifecycle.changeLevel(level);
    }

    @Inject(method = "clearClientLevel", at = @At("HEAD"))
    private void cri$onClearClientLevel(Screen screen, CallbackInfo ci) {
        RaidMarkerLifecycle.changeLevel(null);
    }
}
