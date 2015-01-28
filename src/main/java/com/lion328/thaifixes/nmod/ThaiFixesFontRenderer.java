/*
 * Copyright (c) 2014 Waritnan Sookbuntherng
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

package com.lion328.thaifixes.nmod;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class ThaiFixesFontRenderer extends FontRenderer {

	private ResourceLocation mcpx_font;
	private Field posX, posY, glyphWidth;
	private int[] thaiCharWidth;
	private char beforeChar = 0;
	
	private GameSettings gameSettings;
	private TextureManager renderEngine;
	
	public static final int THAI_CHAR_START = 3584, THAI_CHAR_END = 3675, THAI_CHAR_SIZE = THAI_CHAR_END - THAI_CHAR_START;
	public static final byte MCPX_CHATBLOCK_HEIGHT = 14, MCPX_CHATBLOCK_TEXT_YPOS = 11;
	
	private MethodHandle getCharWidthFloatMethodHandler = null;
	
	public ThaiFixesFontRenderer(GameSettings gs, ResourceLocation resLoc, TextureManager texMan, boolean unicodeFlag) {
		super(gs, resLoc, texMan, unicodeFlag);
		gameSettings = gs;
		renderEngine = texMan;
		setUnicodeFlag(unicodeFlag);
		if(ThaiFixesCore.USING_OPTIFINE) {
			try {
				getCharWidthFloatMethodHandler = MethodHandles.lookup().findSpecial(FontRenderer.class, "getCharWidthFloat", MethodType.methodType(float.class, char.class), this.getClass());
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
		
		try {
			posX = FontRenderer.class.getDeclaredField(ClassMap.getClassMap("net.minecraft.client.gui.FontRenderer").getField("posX"));
			posX.setAccessible(true);
			
			posY = FontRenderer.class.getDeclaredField(ClassMap.getClassMap("net.minecraft.client.gui.FontRenderer").getField("posY"));
			posY.setAccessible(true);
			
			glyphWidth = FontRenderer.class.getDeclaredField(ClassMap.getClassMap("net.minecraft.client.gui.FontRenderer").getField("glyphWidth"));
			glyphWidth.setAccessible(true);
			
			if(ThaiFixesConfiguration.getFontStyle() == ThaiFixesFontStyle.MCPX) {
				thaiCharWidth = new int[THAI_CHAR_SIZE];
				mcpx_font = new ResourceLocation("thaifixes", "textures/font/thai.png");
				
				BufferedImage bufferedimage = ImageIO.read(this.getClass().getResourceAsStream("/assets/thaifixes/textures/font/thai.png"));

				int width = bufferedimage.getWidth();
				int height = bufferedimage.getHeight();
				int[] texture = new int[width * height];
				bufferedimage.getRGB(0, 0, width, height, texture, 0, width);
				int xSize = width / 16;
				int ySize = height / 16;
				byte space = 1;
				float f = 8.0F / (float)xSize;
				int charPos = 0;
	
				while(charPos < THAI_CHAR_SIZE) {
					int col = charPos % 16;
					int row = charPos / 16;
					int l1 = xSize - 1;
					while(true) {
						if(l1 >= 0) {
							boolean end = true;
							for(int j2 = 0; j2 < ySize && end; ++j2) if((texture[(col * xSize + l1) + (row * xSize + j2) * width] >> 24 & 0xFF) != 0) end = false;
							if(end) {
								--l1;
								continue;
							}
						}
						++l1;
						thaiCharWidth[charPos] = (int)(0.5D + (double)((float)l1 * f)) + space;
						++charPos;
						break;
					}
				}
			}
			super.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public float renderCharAtPos(int ascii, char c, boolean italic) {
		float out = ThaiFixesUtils.isThaiChar(c) ? renderThaiChar(c, italic) : super.renderCharAtPos(ascii, c, italic);
		beforeChar = c;
		return out;
	}
	
	private float renderThaiChar(char c, boolean italic) {
		try {
			float cPosX, cPosY;
			switch(ThaiFixesConfiguration.getFontStyle()) {
			default:
			case UNICODE:
				if(ThaiFixesUtils.isSpecialThaiChar(c)) {
					//posX.setFloat(this, posX.getFloat(this) - 4); // move character to before character position
					byte size = ((byte[])glyphWidth.get(this))[c];
					if (size == 0) return 0.0F;
					else {
						invokeMethod("loadGlyphTexture", new Class[] {int.class}, (int)(c / 256)); // load texture

						float charHeightOnTexture = 4.98F,
							beginTexcoordY = ThaiFixesUtils.isUpperThaiChar(c) ? 0F : 15.98F - charHeightOnTexture,
							quadHeight = charHeightOnTexture / 2; // vertex position, not for texture coordinate.
						
						// glyphWidth format:
						// XXXXYYYY, XXXX as start X position on texture coordinate and YYYY as width.
						float startTexcoordX = (float)(size >>> 4);
						float charWidth = (float)((size & 0xF) + 1);
						
						float texcoordX = (float)(c % 16 * 16) + startTexcoordX;
						float texcoordY = (float)((c & 255) / 16 * 16);
						float realCharWidth = charWidth - startTexcoordX - 0.02F;
						float italicSize = italic ? 1.0F : 0.0F;
						
						cPosX = posX.getFloat(this) - (realCharWidth + 0.02F) / 2 - 1; // get current position
						cPosY = posY.getFloat(this);
						
						GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
						GL11.glTexCoord2f(texcoordX / 256.0F, (texcoordY + beginTexcoordY) / 256.0F);
						GL11.glVertex2f(cPosX + italicSize, cPosY + (beginTexcoordY / 2));
						GL11.glTexCoord2f(texcoordX / 256.0F, (texcoordY + beginTexcoordY + charHeightOnTexture) / 256.0F);
						GL11.glVertex2f(cPosX - italicSize, cPosY + (beginTexcoordY / 2) + quadHeight);
						GL11.glTexCoord2f((texcoordX + realCharWidth) / 256.0F, (texcoordY + beginTexcoordY) / 256.0F);
						GL11.glVertex2f(cPosX + realCharWidth / 2.0F + italicSize, cPosY + (beginTexcoordY / 2));
						GL11.glTexCoord2f((texcoordX + realCharWidth) / 256.0F, (texcoordY + beginTexcoordY + charHeightOnTexture) / 256.0F);
						GL11.glVertex2f(cPosX + realCharWidth / 2.0F - italicSize, cPosY + (beginTexcoordY / 2) + quadHeight);
						GL11.glEnd();
						return 0;//(realCharWidth + 0.02F) / 2.0F + 1.0F;
					}
				}
			case DISABLE:
				return (Float)invokeMethod("renderUnicodeChar", new Class[] {char.class, boolean.class}, c, italic);
			case MCPX:
				//if(ThaiFixesUtils.isSpecialThaiChar(c)) posX.setFloat(this, posX.getFloat(this) - 5.0F);
				cPosX = posX.getFloat(this) - (ThaiFixesUtils.isSpecialThaiChar(c) ? 5F : 0F); // get current position
				cPosY = posY.getFloat(this) + (ThaiFixesUtils.isSpecialThaiChar(c) ? (ThaiFixesUtils.isUpperThaiChar(c) ? -7.0F : 2.0F) - (!ThaiFixesUtils.isSpecialSpecialThaiChar(beforeChar) ? (ThaiFixesUtils.isSpecialThaiChar(beforeChar) ? 2.0F : (ThaiFixesUtils.isVeryLongTailThaiChar(beforeChar) ? 1.0F : 0.0F)) : 0.0F) : 0.0F);
				
				int posOnArray = c - THAI_CHAR_START;
				float texcoordX = (float)(posOnArray % 16 * 8);
				float texcoordY = (float)(posOnArray / 16 * 8);
				float italicSize = italic ? 1.0F : 0.0F;
				renderEngine.bindTexture(mcpx_font);
				float f3 = (float)thaiCharWidth[posOnArray] - 0.01F;
				GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
				GL11.glTexCoord2f(texcoordX / 128.0F, texcoordY / 128.0F);
				GL11.glVertex2f(cPosX + italicSize, cPosY);
				GL11.glTexCoord2f(texcoordX / 128.0F, (texcoordY + 7.99F) / 128.0F);
				GL11.glVertex2f(cPosX - italicSize, cPosY + 7.99F);
				GL11.glTexCoord2f((texcoordX + f3 - 1.0F) / 128.0F, texcoordY / 128.0F);
				GL11.glVertex2f(cPosX + f3 - 1.0F + italicSize, cPosY);
				GL11.glTexCoord2f((texcoordX + f3 - 1.0F) / 128.0F, (texcoordY + 7.99F) / 128.0F);
				GL11.glVertex2f(cPosX + f3 - 1.0F - italicSize, cPosY + 7.99F);
				GL11.glEnd();
				return ThaiFixesUtils.isSpecialThaiChar(c) ? 0 : (float)thaiCharWidth[posOnArray];
			}
		} catch(Exception e) {
			System.out.println("[ThaiFixes] Error during render an thai character '" + c + "'" + (italic ? " with italic style" : "") + ".");
			e.printStackTrace();
		}
		return 0.0F;
	}
	
	@Override
	public int getCharWidth(char c) {
		if(ThaiFixesUtils.isSpecialThaiChar(c) && (ThaiFixesConfiguration.getFontStyle() != ThaiFixesFontStyle.DISABLE)) return 0;
		if(ThaiFixesUtils.isThaiChar(c) && (ThaiFixesConfiguration.getFontStyle() == ThaiFixesFontStyle.MCPX)) return thaiCharWidth[c - THAI_CHAR_START];
		return super.getCharWidth(c);
	}
	
	public float getCharWidthFloat(char c) { // OptiFine compatibility
		if(ThaiFixesUtils.isSpecialThaiChar(c) && (ThaiFixesConfiguration.getFontStyle() != ThaiFixesFontStyle.DISABLE)) return 0.0F;
		if(ThaiFixesUtils.isThaiChar(c) && (ThaiFixesConfiguration.getFontStyle() == ThaiFixesFontStyle.MCPX)) return (float)thaiCharWidth[c - THAI_CHAR_START];
		try {
			return (float)getCharWidthFloatMethodHandler.invoke(this, c);
		} catch(Throwable e) {
			if(ThaiFixesCore.USING_OPTIFINE) {
				e.printStackTrace();
				return 0.0F;
			}
			return (float)getCharWidth(c);
		}
	}
	
	@Override
	public void setUnicodeFlag(boolean flag) {
		if((ThaiFixesConfiguration.getFontStyle() == ThaiFixesFontStyle.MCPX) && gameSettings.language.equalsIgnoreCase("th-TH") && !gameSettings.forceUnicodeFont && !flag) {
			super.setUnicodeFlag(false);
			return;
		}
		super.setUnicodeFlag(flag);
	}
	
	private final Object invokeMethod(String methodName, Class<?>[] methodParamsType, Object... params) throws Exception {
		Method parentMethod = FontRenderer.class.getDeclaredMethod(ClassMap.getClassMap("net.minecraft.client.gui.FontRenderer").getMethod(methodName), methodParamsType);
		parentMethod.setAccessible(true);
		return parentMethod.invoke(this, params);
	}
	
	private static Object fieldGet(FontRenderer renderer, String fieldName) {
		try {
			Field f = FontRenderer.class.getDeclaredField(ClassMap.getClassMap("net.minecraft.client.gui.FontRenderer").getField(fieldName));
			f.setAccessible(true);
			return f.get(renderer);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ThaiFixesFontRenderer convert(GameSettings gs, FontRenderer renderer) throws Exception {
		if(gs == null) gs = Minecraft.getMinecraft().gameSettings;
		if(renderer == null) renderer = Minecraft.getMinecraft().fontRendererObj;
		ResourceLocation locationFontTexture = (ResourceLocation)fieldGet(renderer, "locationFontTexture");
		TextureManager renderEngine = (TextureManager)fieldGet(renderer, "renderEngine");
		boolean unicodeFlag = (Boolean)fieldGet(renderer, "unicodeFlag");
		return new ThaiFixesFontRenderer(gs, locationFontTexture, renderEngine, unicodeFlag);
	}
}
