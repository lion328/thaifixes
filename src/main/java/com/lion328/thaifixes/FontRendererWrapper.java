package com.lion328.thaifixes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontRendererWrapper extends FontRenderer
{

    public static final boolean PATCHED = true;

    private static final char[] asciiCharmap = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".toCharArray();

    private static Map<String, ResourceLocation> resourceLocationPool = new HashMap<String, ResourceLocation>();

    private static List<IFontRenderer> renderers = new ArrayList<IFontRenderer>();
    private TextureManager renderEngine;
    private char lastChar = 0;

    private static MethodHandle loadGlyphTextureMethodHandle;
    private static MethodHandle getCharWidthFloatMethodHandle;

    public static File getMinecraftDirectory()
    {
        return Minecraft.getMinecraft().mcDataDir;
    }

    public FontRendererWrapper(GameSettings settings, ResourceLocation asciiTex, TextureManager texMan, boolean unicode)
    {
        super(settings, asciiTex, texMan, unicode);

        renderEngine = texMan;
    }

    public void addRenderer(IFontRenderer renderer)
    {
        if (renderers.contains(renderer))
        {
            return;
        }

        renderer.setFontRendererWrapper(this);
        renderers.add(renderer);
    }

    public void loadUnicodeTexture(int tex)
    {
        if (loadGlyphTextureMethodHandle == null)
        {
            ThaiFixes.LOGGER.error("Can't load unicode texture, method not found");

            return;
        }

        try
        {
            loadGlyphTextureMethodHandle.invokeExact(this, tex);
        }
        catch (Throwable throwable)
        {
            ThaiFixes.LOGGER.catching(throwable);
        }
    }

    public byte getRawUnicodeWidth(char c)
    {
        return glyphWidth[c];
    }

    public void bindTexture(String location)
    {
        if (!resourceLocationPool.containsKey(location))
        {
            resourceLocationPool.put(location, new ResourceLocation(location));
        }
        renderEngine.bindTexture(resourceLocationPool.get(location));
    }

    public float getX()
    {
        return posX;
    }

    public float getY()
    {
        return posY;
    }

    @Override
    protected float renderDefaultChar(int c, boolean italic)
    {
        float ret = super.renderDefaultChar(c, italic);

        lastChar = asciiCharmap[c];

        return ret;
    }

    @Override
    protected float renderUnicodeChar(char c, boolean italic)
    {
        IFontRenderer renderer = getCharacterAvailableRenderer(c);

        float ret;

        if (renderer == null)
        {
            ret = super.renderUnicodeChar(c, italic);
        }
        else
        {
            ret = renderer.getCharacterWidth(c);
        }

        lastChar = c;

        return ret;
    }

    @Override
    public int getCharWidth(char c)
    {
        IFontRenderer renderer = getCharacterAvailableRenderer(c);

        if (renderer != null)
        {
            return renderer.getCharacterWidth(c);
        }

        return super.getCharWidth(c);
    }

    public float getCharWidthFloat(char c)
    {
        IFontRenderer renderer = getCharacterAvailableRenderer(c);

        if (renderer != null)
        {
            return renderer.getCharacterWidth(c);
        }

        return 0;
    }

    public char getLastCharacterRenderered()
    {
        return lastChar;
    }

    public IFontRenderer getCharacterAvailableRenderer(char c)
    {
        for (IFontRenderer renderer : renderers)
        {
            if (renderer.isSupportedCharacter(c))
            {
                return renderer;
            }
        }

        return null;
    }

    static
    {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try
        {
            Method method = FontRenderer.class.getMethod("loadGlyphTexture");
            method.setAccessible(true);

            loadGlyphTextureMethodHandle = lookup.unreflect(method);
        }
        catch (NoSuchMethodException | IllegalAccessException e)
        {
            ThaiFixes.LOGGER.catching(e);
        }

        try
        {
            getCharWidthFloatMethodHandle = lookup.findVirtual(FontRenderer.class, "getCharWidthFloat", MethodType.methodType(float.class, char.class));
        }
        catch (NoSuchMethodException ignore)
        {

        }
        catch (IllegalAccessException e)
        {
            ThaiFixes.LOGGER.catching(e);
        }
    }
}