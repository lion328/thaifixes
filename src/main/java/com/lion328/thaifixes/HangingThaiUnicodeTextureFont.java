/*
 * Copyright (c) 2019 Waritnan Sookbuntherng
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

import com.lion328.thaifixes.mixin.UnicodeTextureGlyphAccessor;
import net.minecraft.client.font.RenderableGlyph;

public class HangingThaiUnicodeTextureFont implements RenderableGlyph
{
    private final UnicodeTextureGlyphAccessor parent;
    private final int yShift;
    private final int height;

    public HangingThaiUnicodeTextureFont(UnicodeTextureGlyphAccessor parent, boolean hangAbove)
    {
        this.parent = parent;

        if (hangAbove)
        {
            yShift = 0;
            height = 6;
        }
        else
        {
            yShift = 11;
            height = 5;
        }
    }

    @Override
    public int getWidth()
    {
        return parent.getWidth();
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public void upload(int x, int y)
    {
        parent.getImage().upload(0, x, y,
                parent.getUnpackSkipPixels(), parent.getUnpackSkipRows() + yShift,
                getWidth(), getHeight(),
                false);
    }

    @Override
    public boolean hasColor()
    {
        return parent.hasColor();
    }

    @Override
    public float getOversample()
    {
        return parent.getOversample();
    }

    @Override
    public float getBoldOffset()
    {
        return parent.getBoldOffset();
    }

    @Override
    public float getShadowOffset()
    {
        return parent.getShadowOffset();
    }

    @Override
    public float getAdvance()
    {
        return 0;
    }

    @Override
    public float getXMin()
    {
        return parent.getXMin() - parent.getAdvance();
    }

    @Override
    public float getXMax()
    {
        return parent.getXMax() - parent.getAdvance();
    }

    @Override
    public float getYMin()
    {
        return parent.getYMin() + (float)yShift / 2;
    }

    @Override
    public float getYMax()
    {
        return getYMin() + (float)height / 2;
    }
}
