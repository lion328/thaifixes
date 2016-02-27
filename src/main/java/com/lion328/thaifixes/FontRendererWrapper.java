package com.lion328.thaifixes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class FontRendererWrapper extends FontRenderer {

	public static final boolean PATCHED = false;

	public static File getMinecraftDirectory() {
		return null;
	}
	
	private static void initialize() {

	}
	
	public FontRendererWrapper(GameSettings settings, ResourceLocation asciiTex, TextureManager texMan, boolean unicode) {
		super(settings, asciiTex, texMan, unicode);
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
