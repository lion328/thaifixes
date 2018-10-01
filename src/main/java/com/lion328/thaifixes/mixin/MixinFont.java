/*
 * Copyright (c) 2018 Waritnan Sookbuntherng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lion328.thaifixes.mixin;

import com.lion328.thaifixes.ThaiFixes;
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
        boolean hangBelow = ThaiFixes.processingThaiChars.get(glyphInfo);

        float posYShift = 0.0f;
        float height = 2.99f;

        if (hangBelow)
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
