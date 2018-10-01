package com.lion328.thaifixes.mixin;

import com.lion328.thaifixes.ThaiFixes;
import com.lion328.thaifixes.ThaiUtil;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.gui.fonts.providers.TextureGlyphProviderUnicode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextureGlyphProviderUnicode.class)
public abstract class MixinTextureGlyphProviderUnicode
{

    @Inject(method = "func_211028_a_", at = @At("RETURN"), cancellable = true)
    public void onGlyphInfoRequest(char c, CallbackInfoReturnable<IGlyphInfo> cir)
    {
        if (!ThaiUtil.isSpecialThaiChar(c))
        {
            return;
        }

        ThaiFixes.processingThaiChars.put(cir.getReturnValue(), c);
    }
}
