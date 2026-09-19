package com.ludwici.cri.mixin.xaero;

import com.ludwici.cri.plugins.xaero.XaeroMarkerApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.gui.GuiMap;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.GuiRightClickMenu;
import xaero.map.mods.gui.Waypoint;

@Mixin(GuiRightClickMenu.class)
public class GuiRightClickMenuMixin {
    @Inject(method = "getMenu", at = @At("HEAD"), cancellable = true)
    private static void hideMenu(IRightClickableElement rightClickable, GuiMap screen, int x, int y, int w, CallbackInfoReturnable<GuiRightClickMenu> cir) {
        if (rightClickable instanceof HoveredMapElementHolder elementHolder && elementHolder.getElement() instanceof Waypoint waypoint && XaeroMarkerApi.isTracked(waypoint)) {
            cir.setReturnValue(null);
        }
    }
}
