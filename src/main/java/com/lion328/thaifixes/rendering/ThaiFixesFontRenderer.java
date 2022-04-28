/*
 * Copyright (c) 2017 Waritnan Sookbuntherng
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

package com.lion328.thaifixes.rendering;

import com.lion328.thaifixes.rendering.font.Font;
import com.lion328.thaifixes.rendering.font.StubFont;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ThaiFixesFontRenderer extends FakeFontRenderer {

    private Map<String, ResourceLocation> resourceLocationPool = new HashMap<>();

    private Font font = new StubFont();
    private TextureManager renderEngine;
    private char lastChar = 0;
    private float lastCharShift = Float.NaN;

    public ThaiFixesFontRenderer(GameSettings settings, ResourceLocation asciiTex, TextureManager texMan, boolean unicode) {
        super(settings, asciiTex, texMan, unicode);

        renderEngine = texMan;
    }

    public boolean isSuperclassPatched() {
        try {
            Field field = getClass().getSuperclass().getField("PATCHED_BY_THAIFIXES");
            return (boolean) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
            return false;
        }
    }

    public void setFont(Font newFont) {
        font.setFontRenderer(null);
        newFont.setFontRenderer(this);
        font = newFont;
    }

    public void loadUnicodeTexture(int tex) {
        super.loadGlyphTexture(tex);
    }

    public byte getRawUnicodeWidth(char c) {
        return super.glyphWidth[c];
    }

    public void bindTexture(String location) {
        if (!resourceLocationPool.containsKey(location))
            resourceLocationPool.put(location, new ResourceLocation(location));
        renderEngine.bindTexture(resourceLocationPool.get(location));
    }

    public float getX() {
        return super.posX;
    }

    public float getY() {
        return super.posY;
    }

    public void setX(float v) {
        super.posX = v;
    }

    @Override
    public float renderCharAtPos(int asciiPos, char c, boolean italic) {
        float ret;

        if (font.isSupportedCharacter(c))
            ret = font.renderCharacter(c, italic);
        else
            ret = super.renderCharAtPos(asciiPos, c, italic);

        lastCharShift = ret;
        return ret;
    }

    @Override
    public float renderCharAtPos(char c, boolean italic) {
        float ret;

        if (font.isSupportedCharacter(c))
            ret = font.renderCharacter(c, italic);
        else
            ret = super.renderCharAtPos(c, italic);

        lastCharShift = ret;
        return ret;
    }

    @Override
    public int getCharWidth(char c) {
        if (font.isSupportedCharacter(c))
            return font.getCharacterWidth(c);
        return super.getCharWidth(c);
    }

    @Override
    public void renderStringAtPos(String text, boolean shadow) {
        lastChar = 0;
        text = font.preStringRendered(text);

        super.renderStringAtPos(text, shadow);

        font.postStringRendered();
        lastChar = 0;
    }

    @Override
    public float getCharWidthFloat(char c) {
        if (font.isSupportedCharacter(c))
            return (float) font.getCharacterWidth(c);
        return super.getCharWidthFloat(c);
    }

    @Override
    public void onCharRenderedThaiFixes(char c) {
        lastChar = c;
        font.postCharacterRendered(c);
    }

    @Override
    protected float getShadowShiftSizeThaiFixes(char c, float f) {
        font.preCharacterRendered(c);

        if (font.isSupportedCharacter(c))
            return font.getShadowShiftSize(c, f);

        return f;
    }

    @Override
    protected float getBoldShiftSizeThaiFixes(char c, float f) {
        if (font.isSupportedCharacter(c))
            return font.getBoldShiftSize(c, f);
        return f;
    }

    public char getLastCharacterRendered() {
        return lastChar;
    }

    public float getLastCharacterShiftOriginal() {
        return lastCharShift;
    }
}
