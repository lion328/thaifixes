package com.lion328.thaifixes.mixin;

import com.lion328.thaifixes.FontRendererWrapper;
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

    @Shadow
    private float posX;
    @Shadow
    private float posY;

    @Shadow
    protected abstract int getCharWidth(char c);

    @Inject(method = "<init>*", at = @At("RETURN"))
    protected void onConstructed(CallbackInfo ci)
    {

    }

    @Inject(method = "renderChar(CZ)F", at = @At("HEAD"))
    protected void onRenderChar(char c, boolean italic, CallbackInfoReturnable<Float> cir)
    {

    }

    @Inject(method = "getCharWidthFloat(C)F", remap = false, at = @At("HEAD"))
    protected void onGetCharWidthFloat(char c, CallbackInfoReturnable<Float> cir)
    {

    }

    @Unique
    protected float getCharWidthFloat(char c)
    {
        return (float) getCharWidth(c);
    }
}
