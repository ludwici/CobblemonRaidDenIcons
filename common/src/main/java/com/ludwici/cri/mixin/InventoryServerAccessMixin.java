package com.ludwici.cri.mixin;

import com.ludwici.cri.network.RaidIconServerAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryServerAccessMixin {
    @Inject(method = "setChanged", at = @At("TAIL"))
    private void cri$changed(CallbackInfo ci) {
        RaidIconServerAccess.inventoryChanged((Inventory) (Object) this);
    }

    @Inject(method = "setItem", at = @At("TAIL"))
    private void cri$setItem(int slot, ItemStack stack, CallbackInfo ci) {
        RaidIconServerAccess.inventoryChanged((Inventory) (Object) this);
    }

    @Inject(method = "removeItem", at = @At("TAIL"))
    private void cri$removeItem(int slot, int count, CallbackInfoReturnable<ItemStack> cir) {
        RaidIconServerAccess.inventoryChanged((Inventory) (Object) this);
    }

    @Inject(method = "removeItemNoUpdate", at = @At("TAIL"))
    private void cri$removeItemNoUpdate(int slot, CallbackInfoReturnable<ItemStack> cir) {
        RaidIconServerAccess.inventoryChanged((Inventory) (Object) this);
    }

    @Inject(method = "clearContent", at = @At("TAIL"))
    private void cri$clearContent(CallbackInfo ci) {
        RaidIconServerAccess.inventoryChanged((Inventory) (Object) this);
    }
}
