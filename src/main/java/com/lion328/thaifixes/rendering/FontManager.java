/*
 * Copyright (c) 2022 Waritnan Sookbuntherng
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

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class FontManager {
    private final ExtendedFontRenderer renderer;
    private final Map<String, ResourceLocation> resourceLocationPool = new HashMap<>();

    public FontManager(ExtendedFontRenderer renderer) {
        this.renderer = renderer;
    }

    public float getX() {
        return renderer.getXThaiFixes();
    }

    public float getY() {
        return renderer.getYThaiFixes();
    }

    public void setX(float x) {
        renderer.setXThaiFixes(x);
    }

    public char getLastCharacterRendered() {
        return renderer.getLastCharacterRenderedThaiFixes();
    }

    public float getLastCharacterShiftOriginal() {
        return renderer.getLastCharacterShiftOriginalThaiFixes();
    }

    public void bindTexture(String location) {
        if (!resourceLocationPool.containsKey(location))
            resourceLocationPool.put(location, new ResourceLocation(location));
        renderer.getTextureManagerThaiFixes().bindTexture(resourceLocationPool.get(location));
    }

    public int getRawUnicodeWidth(char c) {
        return renderer.getGlyphWidthThaiFixes()[c];
    }

    public void loadUnicodeTexture(int texture) {
        renderer.loadGlyphTextureThaiFixes(texture);
    }
}
