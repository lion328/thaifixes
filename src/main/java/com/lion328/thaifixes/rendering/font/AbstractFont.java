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

package com.lion328.thaifixes.rendering.font;

import com.lion328.thaifixes.rendering.ThaiFixesFontRenderer;

public abstract class AbstractFont implements Font {
    private ThaiFixesFontRenderer renderer;

    @Override
    public final void setFontRenderer(ThaiFixesFontRenderer renderer) {
        this.renderer = renderer;
        onFontRendererChanged();
    }

    @Override
    public final ThaiFixesFontRenderer getRenderer() {
        return renderer;
    }

    public void onFontRendererChanged() {
    }

    @Override
    public String preStringRendered(String text) {
        return text;
    }

    @Override
    public void preCharacterRendered(char c) {
    }

    @Override
    public float getShadowShiftSize(char c, float shift) {
        return shift;
    }

    @Override
    public float getBoldShiftSize(char c, float shift) {
        return shift;
    }

    @Override
    public void postCharacterRendered(char c) {
    }

    @Override
    public void postStringRendered() {
    }
}
