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
}
