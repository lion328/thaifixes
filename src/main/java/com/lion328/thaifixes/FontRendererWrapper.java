package com.lion328.thaifixes;

import com.lion328.thaifixes.renderer.IFontRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class FontRendererWrapper
{

    private IFontRenderer renderer;

    public void setRenderer(IFontRenderer renderer)
    {
        if (renderer == null)
        {
            removeRenderer();
            return;
        }

        renderer.setFontRendererWrapper(this);
        this.renderer = renderer;
    }

    public IFontRenderer getRenderer()
    {
        return renderer;
    }

    public void removeRenderer()
    {
        if (renderer != null)
        {
            renderer.setFontRendererWrapper(null);
        }

        this.renderer = null;
    }

    public abstract float getX();

    public abstract float getY();

    public abstract char getLastCharacterRendered();

    public abstract void bindTexture(String mcpxTextureLocationResource);

    public abstract void loadUnicodeTexture(int i);

    public abstract int getRawUnicodeWidth(char c);
}
