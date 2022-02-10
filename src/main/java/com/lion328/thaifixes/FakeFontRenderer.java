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

package com.lion328.thaifixes;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class FakeFontRenderer {

    protected float posX;
    protected float posY;
    protected final byte[] glyphWidth = null;

    public FakeFontRenderer(GameSettings settings, ResourceLocation asciiTex, TextureManager texMan, boolean unicode) {
        throw new IllegalArgumentException();
    }

    protected void loadGlyphTexture(int page) {
        throw new IllegalArgumentException();
    }

    public float renderCharAtPos(char c, boolean italic) {
        throw new IllegalArgumentException();
    }

    public float renderCharAtPos(int asciiPos, char c, boolean italic) {
        throw new IllegalArgumentException();
    }

    public void renderStringAtPos(String text, boolean shadow) {
        throw new IllegalArgumentException();
    }

    public int getCharWidth(char character) {
        throw new IllegalArgumentException();
    }

    // Optifine
    public float getCharWidthFloat(char c) {
        throw new IllegalArgumentException();
    }
}
