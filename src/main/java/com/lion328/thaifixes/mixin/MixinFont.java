package com.lion328.thaifixes.mixin;

import com.lion328.thaifixes.HangingThaiCharacterTexturedGlyph;
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

    @Inject(method = "func_211185_a", at = @At("RETURN"), cancellable = true)
    public void onTexturedGlyphRequest(IGlyphInfo glyphInfo, CallbackInfoReturnable<TexturedGlyph> cir) {
        if (!ThaiFixes.processingThaiChars.containsKey(glyphInfo))
        {
            return;
        }

        TexturedGlyph parent = cir.getReturnValue();
        char c = ThaiFixes.processingThaiChars.get(glyphInfo);

        cir.setReturnValue(HangingThaiCharacterTexturedGlyph.fromParent(parent, c));

        ThaiFixes.processingThaiChars.remove(glyphInfo);
    }
}
