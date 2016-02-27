package com.lion328.thaifixes;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.io.File;

public class FontRendererWrapper extends FontRenderer {

    public static final boolean PATCHED = false;

    public FontRendererWrapper(GameSettings settings, ResourceLocation asciiTex, TextureManager texMan, boolean unicode) {
        super(settings, asciiTex, texMan, unicode);
    }

    public static File getMinecraftDirectory() {
        return null;
    }

    private static void initialize() {

    }

    public void addRenderer(IFontRenderer renderer) {

    }

    public void loadUnicodeTexture(int tex) {

    }

    public byte getRawUnicodeWidth(char c) {
        return 0;
    }

    public void bindTexture(String location) {

    }

    public int getDefaultCharacterWidth(char c) {
        return 0;
    }

    public float getX() {
        return posX;
    }

    public float getY() {
        return posY;
    }

    public float renderCharAtPos(int asciiPos, char c, boolean italic) {
        return 0;
    }

    public int getCharWidth(char c) {
        return 0;
    }

    public float getCharWidthFloat(char c) {
        return 0;
    }

    public char getLastCharacterRenderered() {
        return 0;
    }
}
