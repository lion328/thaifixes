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

package com.lion328.thaifixes;

import com.lion328.thaifixes.renderer.IFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FontRendererWrapper extends FakeFontRenderer {

    private Map<String, ResourceLocation> resourceLocationPool = new HashMap<>();

    private IFontRenderer renderer;
    private TextureManager renderEngine;
    private char lastChar = 0;
    private float lastPosX = Float.NaN;
    private float lastPosY = Float.NaN;
    private float lastCharShift = Float.NaN;

    public static File getMinecraftDirectory() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    public FontRendererWrapper(GameSettings settings, ResourceLocation asciiTex, TextureManager texMan, boolean unicode) {
        super(settings, asciiTex, texMan, unicode);

        renderEngine = texMan;
    }

    public void setRenderer(IFontRenderer newRenderer) {
        if (renderer != null)
            renderer.setWrapper(null);

        if (newRenderer != null)
            newRenderer.setWrapper(this);
        renderer = newRenderer;
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

    public int getDefaultCharacterWidth(char c) {
        return super.getCharWidth(c);
    }

    public float getX() {
        return super.posX;
    }

    public float getY() {
        return super.posY;
    }

    public void setX(float v) {
        posX = v;
    }

    @Override
    public float renderCharAtPos(int asciiPos, char c, boolean italic) {
        float ret;

        if (renderer != null && renderer.isSupportedCharacter(c))
            ret = renderer.renderCharacter(c, italic);
        else
            ret = super.renderCharAtPos(asciiPos, c, italic);

        lastCharShift = ret;
        return ret;
    }

    @Override
    public float renderCharAtPos(char c, boolean italic) {
        float ret;

        if (renderer != null && renderer.isSupportedCharacter(c))
            ret = renderer.renderCharacter(c, italic);
        else
            ret = super.renderCharAtPos(c, italic);

        lastCharShift = ret;
        return ret;
    }

    @Override
    public int getCharWidth(char c) {
        if (renderer != null && renderer.isSupportedCharacter(c))
            return renderer.getCharacterWidth(c);
        return super.getCharWidth(c);
    }

    @Override
    public void renderStringAtPos(String text, boolean shadow) {
        lastChar = 0;
        if (renderer != null)
            text = renderer.beforeStringRendered(text);

        super.renderStringAtPos(text, shadow);

        if (renderer != null)
            renderer.afterStringRendered();
        lastChar = 0;
    }

    @Override
    public float getCharWidthFloat(char c) {
        if (renderer != null && renderer.isSupportedCharacter(c))
            return (float) renderer.getCharacterWidth(c);
        return super.getCharWidthFloat(c);
    }

    @Override
    public void onCharRendered(char c) {
        lastChar = c;

        if (renderer != null)
            renderer.afterCharacterRendered(c);
    }

    @Override
    protected float getShadowShiftSize(char c, float f) {
        if (renderer != null) {
            renderer.beforeCharacterRendered(c);

            if (renderer.isSupportedCharacter(c))
                return renderer.getShadowShiftSize(c, f);
        }
        return f;
    }

    @Override
    protected float getBoldShiftSize(char c, float f) {
        if (renderer != null && renderer.isSupportedCharacter(c))
            return renderer.getBoldShiftSize(c, f);
        return f;
    }

    public char getLastCharacterRendered() {
        return lastChar;
    }

    public float getLastCharacterShiftOriginal() {
        return lastCharShift;
    }
}
