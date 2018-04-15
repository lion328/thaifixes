package com.lion328.thaifixes;

import com.lion328.thaifixes.renderer.IFontRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class FontRendererWrapper
{

    private IFontRenderer renderer;

    public void setRenderer(IFontRenderer renderer)
    {
        this.renderer = renderer;
    }

    public void removeRenderer()
    {
        this.renderer = null;
    }

    public int getCharWidth(char c)
    {
        if (renderer.isSupportedCharacter(c))
        {
            renderer.getCharacterWidth(c);
        }

        return getDefaultCharacterWidth(c);
    }

    public abstract float getX();

    public abstract float getY();

    public abstract char getLastCharacterRendered();

    public abstract void bindTexture(String mcpxTextureLocationResource);

    public abstract void loadUnicodeTexture(int i);

    public abstract int getRawUnicodeWidth(char c);

    public abstract int getDefaultCharacterWidth(char c);
}
