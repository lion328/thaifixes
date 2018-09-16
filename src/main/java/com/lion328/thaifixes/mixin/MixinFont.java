package com.lion328.thaifixes.mixin;

import com.lion328.thaifixes.HangingThaiCharacterTexturedGlyph;
import com.lion328.thaifixes.ThaiUtil;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class MixinFont
{

    @Inject(method = "func_211184_b", at = @At("RETURN"), cancellable = true)
    public void onGlyphRequest(char c, CallbackInfoReturnable<IGlyph> cir) {
        if (!ThaiUtil.isSpecialThaiChar(c))
        {
            return;
        }

        IGlyph old = cir.getReturnValue();

        if (!(old instanceof TexturedGlyph)) {
            return;
        }

        HangingThaiCharacterTexturedGlyph glyph = HangingThaiCharacterTexturedGlyph.fromParent((TexturedGlyph) old);

        cir.setReturnValue(glyph);
    }
}
