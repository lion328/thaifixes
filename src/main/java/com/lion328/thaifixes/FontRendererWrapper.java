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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontRendererWrapper extends FakeFontRenderer {

    private static Map<String, ResourceLocation> resourceLocationPool = new HashMap<String, ResourceLocation>();

    private static List<IFontRenderer> renderers = new ArrayList<IFontRenderer>();
    private TextureManager renderEngine;
    private char lastChar = 0;

    public static File getMinecraftDirectory() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    public FontRendererWrapper(GameSettings settings, ResourceLocation asciiTex, TextureManager texMan, boolean unicode) {
        super(settings, asciiTex, texMan, unicode);

        renderEngine = texMan;
    }

    public void addRenderer(IFontRenderer renderer) {
        if (renderers.contains(renderer)) return;
        renderer.setFontRendererWrapper(this);
        renderers.add(renderer);
    }

    public void removeRenderer(IFontRenderer renderer) {
        renderers.remove(renderer);
        renderer.setFontRendererWrapper(null);
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

    @Override
    public float renderCharAtPos(int asciiPos, char c, boolean italic) {
        float ret = Float.NaN;
        for (IFontRenderer renderer : renderers)
            if (renderer.isSupportedCharacter(c)) {
                ret = renderer.renderCharacter(c, italic);
                break;
            }
        if (Float.isNaN(ret))
            ret = super.renderCharAtPos(asciiPos, c, italic);
        return ret;
    }

    @Override
    public float renderCharAtPos(char c, boolean italic) {
        float ret = Float.NaN;
        for (IFontRenderer renderer : renderers)
            if (renderer.isSupportedCharacter(c)) {
                ret = renderer.renderCharacter(c, italic);
                break;
            }
        if (Float.isNaN(ret))
            ret = super.renderCharAtPos(c, italic);
        return ret;
    }

    @Override
    public int getCharWidth(char c) {
        for (IFontRenderer renderer : renderers)
            if (renderer.isSupportedCharacter(c))
                return renderer.getCharacterWidth(c);
        return super.getCharWidth(c);
    }

    @Override
    public void renderStringAtPos(String text, boolean shadow) {
        lastChar = 0;
        super.renderStringAtPos(text, shadow);
        lastChar = 0;
    }

    @Override
    public float getCharWidthFloat(char c) {
        for (IFontRenderer renderer : renderers)
            if (renderer.isSupportedCharacter(c))
                return (float) renderer.getCharacterWidth(c);
        return super.getCharWidthFloat(c);
    }

    @Override
    public void onCharRendered(char c) {
        lastChar = c;
    }

    public char getLastCharacterRendered() {
        return lastChar;
    }
}
