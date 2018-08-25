package com.lion328.thaifixes.mixin;

import com.lion328.thaifixes.FontRendererWrapper;
import com.lion328.thaifixes.ThaiFixes;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer
{

    private char lastChar = 0;

    @Inject(method = "<init>*", at = @At("RETURN"))
    protected void onConstructed(CallbackInfo ci)
    {
        ThaiFixes.getLogger().info("Hello Mixin!");
    }
}
