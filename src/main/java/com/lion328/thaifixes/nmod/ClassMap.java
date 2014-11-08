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

import java.util.HashMap;
import java.util.Map;

public class ClassMap {

	private static Map<String, ClassMap> classMap = new HashMap<String, ClassMap>();
	
	private ClassInfo info;
	private Map<String, String> fields = new HashMap<String, String>(), methods = new HashMap<String, String>();
	
	public ClassMap(ClassInfo info) {
		this.info = info;
	}
	
	public void putField(String key, String value) {
		fields.put(key, value);
	}

	public void putMethod(String key, String value) {
		methods.put(key, value);
	}
	
	public String getField(String key) {
		return fields.containsKey(key) ? (ThaiFixesCore.OBFUSCATED ? fields.get(key) : key) : key;
	}
	
	public String getMethod(String key) {
		return methods.containsKey(key) ? (ThaiFixesCore.OBFUSCATED ? methods.get(key) : key) : key;
	}
	
	public ClassInfo getClassInfo() {
		return info;
	}
	
	public static void addClassMap(ClassMap map) {
		classMap.put(map.info.getClassName(), map);
	}
	
	public static ClassMap getClassMap(String className) {
		return classMap.get(className);
	}

	static {
		// Minecraft 1.7.10 fields and method mapping.
		
		ClassMap fontRenderer = new ClassMap(new ClassInfo("net.minecraft.client.gui.FontRenderer", "bbu"));
		fontRenderer.putField("locationFontTexture", "field_111273_g");
		fontRenderer.putField("renderEngine", "field_78298_i");
		fontRenderer.putField("unicodeFlag", "field_78293_l");
		fontRenderer.putField("unicodePageLocations", "field_111274_c");
		fontRenderer.putField("posX", "field_78295_j");
		fontRenderer.putField("posY", "field_78296_k");
		fontRenderer.putField("FONT_HEIGHT", "field_78288_b");
		fontRenderer.putMethod("renderUnicodeChar", "func_78277_a");
		addClassMap(fontRenderer);
		
		ClassMap guiNewChat = new ClassMap(new ClassInfo("net.minecraft.client.gui.GuiNewChat", "bcc"));
		guiNewChat.putField("mc", "field_146247_f");
		guiNewChat.putMethod("func_146232_i", "i");
		guiNewChat.putMethod("func_146236_a", "a");
		guiNewChat.putMethod("drawChat", "a");
		addClassMap(guiNewChat);
		
		ClassMap guiChat = new ClassMap(new ClassInfo("net.minecraft.client.gui.GuiChat", "bct"));
		guiChat.putMethod("initGui", "b");
		guiChat.putMethod("drawScreen", "a");
		addClassMap(guiChat);
		
		ClassMap rendererLivingEnitity = new ClassMap(new ClassInfo("net.minecraft.client.renderer.entity.RendererLivingEntity", "boh"));
		rendererLivingEnitity.putMethod("passSpecialRender", "b");
		addClassMap(rendererLivingEnitity);
		
		ClassMap guiLanguage$List = new ClassMap(new ClassInfo("net.minecraft.client.gui.GuiLanguage$List", "bdk"));
		guiLanguage$List.putMethod("elementClicked", "a");
		addClassMap(guiLanguage$List);
		
		addClassMap(new ClassMap(new ClassInfo("net.minecraft.util.IChatComponent", "fj")));
		addClassMap(new ClassMap(new ClassInfo("net.minecraft.entity.EntityLivingBase", "sv")));
	}
	
	public static void load() {} // nope
}
