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

package com.lion328.thaifixes.renderer;

import com.lion328.thaifixes.FontRendererWrapper;

public abstract class AbstractFontRenderer implements IFontRenderer {
    private FontRendererWrapper wrapper;

    @Override
    public final void setWrapper(FontRendererWrapper wrapper) {
        this.wrapper = wrapper;
        onWrapperChanged();
    }

    @Override
    public final FontRendererWrapper getWrapper() {
        return wrapper;
    }

    public void onWrapperChanged() {
    }

    @Override
    public String beforeStringRendered(String text) {
        return text;
    }

    @Override
    public void beforeCharacterRendered(char c) {
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
    public void afterCharacterRendered(char c) {
    }

    @Override
    public void afterStringRendered() {
    }
}
