package com.aryston.arkea.mixin;

import com.aryston.arkea.integration.KeyModifierRepair;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Inject(method = "load", at = @At("RETURN"))
    private void arkea$restoreKeyModifiers(CallbackInfo info) {
        KeyModifierRepair.run((Options) (Object) this);
    }
}
