package com.lion328.thaifixes.mixin;

import com.lion328.thaifixes.ThaiFixes;
import com.lion328.thaifixes.ThaiUtil;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class MixinFont
{

    @Inject(method = "createTexturedGlyph", at = @At("RETURN"), cancellable = true)
    public void onCreateTexturedGlyph(IGlyphInfo glyphInfo, CallbackInfoReturnable<TexturedGlyph> cir)
    {
        if (!ThaiFixes.processingThaiChars.containsKey(glyphInfo))
        {
            return;
        }

        TexturedGlyph texturedGlyph = cir.getReturnValue();
        char c = ThaiFixes.processingThaiChars.get(glyphInfo);

        float posYShift = 0.0f;
        float height = 2.99f;

        if (ThaiUtil.isLowerThaiChar(c))
        {
            posYShift = 6.0f;
            height = 1.99f;
        }

        // X vertices
        texturedGlyph.field_211240_f -= texturedGlyph.advanceWidth;
        texturedGlyph.field_211241_g -= texturedGlyph.advanceWidth;

        // Y vertices
        texturedGlyph.field_211242_h += posYShift;
        texturedGlyph.field_211243_i = texturedGlyph.field_211242_h + height;

        // UV coordinates
        texturedGlyph.v0 += posYShift / 128.0f;
        texturedGlyph.v1 = texturedGlyph.v0 + height / 128.0f;

        texturedGlyph.advanceWidth = 0.0f;

        ThaiFixes.processingThaiChars.remove(glyphInfo);
    }
}
