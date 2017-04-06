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
import com.lion328.thaifixes.renderer.MCPXFontRenderer;
import com.lion328.thaifixes.renderer.UnicodeFontRenderer;

public enum FontStyle
{

    UNICODE("Unicode", UnicodeFontRenderer.class),
    MCPX("MCPX", MCPXFontRenderer.class),
    DISABLE("Disable", null);

    private final Class<? extends IFontRenderer> clazz;
    private final String name;

    FontStyle(String name, Class<? extends IFontRenderer> clazz)
    {
        this.name = name;
        this.clazz = clazz;
    }

    public static FontStyle fromString(String s)
    {
        for (FontStyle fontStyle : FontStyle.values())
        {
            if (s.equalsIgnoreCase(fontStyle.name))
            {
                return fontStyle;
            }
        }

        return null;
    }

    public static String[] asStringArray()
    {
        FontStyle[] available = FontStyle.values();
        String[] ret = new String[FontStyle.values().length];

        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = available[i].toString();
        }

        return ret;
    }

    public Class<? extends IFontRenderer> getRendererClass()
    {
        return clazz;
    }

    public IFontRenderer newInstance() throws IllegalAccessException, InstantiationException
    {
        if (clazz == null)
        {
            return null;
        }

        return clazz.newInstance();
    }

    public String toString()
    {
        return name;
    }
}
