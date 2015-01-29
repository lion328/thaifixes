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
		// Minecraft 1.8 classes, fields and methods mapping.
		
		ClassMap fontRenderer = new ClassMap(new ClassInfo("net.minecraft.client.gui.FontRenderer", "bty"));
		fontRenderer.putField("locationFontTexture", "field_111273_g");
		fontRenderer.putField("renderEngine", "field_78298_i");
		fontRenderer.putField("unicodeFlag", "field_78293_l");
		fontRenderer.putField("unicodePageLocations", "field_111274_c");
		fontRenderer.putField("posX", "field_78295_j");
		fontRenderer.putField("posY", "field_78296_k");
		fontRenderer.putField("FONT_HEIGHT", "field_78288_b");
		fontRenderer.putField("glyphWidth", "field_78287_e");
		fontRenderer.putMethod("renderUnicodeChar", "func_78277_a");
		fontRenderer.putMethod("loadGlyphTexture", "func_78257_a");
		addClassMap(fontRenderer);
		
		ClassMap guiNewChat = new ClassMap(new ClassInfo("net.minecraft.client.gui.GuiNewChat", "buh"));
		guiNewChat.putField("mc", "field_146247_f");
		guiNewChat.putMethod("getLineCount", "i"); //func_146232_i
		guiNewChat.putMethod("func_146232_i", guiNewChat.getMethod("getLineCount")); // backward compatibility (or lazy to fix it)
		guiNewChat.putMethod("getChatComponent", "a"); //func_146236_a
		guiNewChat.putMethod("func_146236_a", guiNewChat.getMethod("getChatComponent"));
		guiNewChat.putMethod("drawChat", "func_146230_a");
		addClassMap(guiNewChat);

		ClassMap guiChat = new ClassMap(new ClassInfo("net.minecraft.client.gui.GuiChat", "bvx"));
		guiChat.putMethod("initGui", "func_73866_w_");
		guiChat.putMethod("drawScreen", "func_73863_a");
		addClassMap(guiChat);
		
		ClassMap rendererLivingEnitity = new ClassMap(new ClassInfo("net.minecraft.client.renderer.entity.RendererLivingEntity", "cqv"));
		rendererLivingEnitity.putMethod("passSpecialRender", "func_77033_b");
		addClassMap(rendererLivingEnitity);
		
		ClassMap guiLanguage$List = new ClassMap(new ClassInfo("net.minecraft.client.gui.GuiLanguage$List", "bwt"));
		guiLanguage$List.putMethod("elementClicked", "func_148144_a");
		addClassMap(guiLanguage$List);
		
		addClassMap(new ClassMap(new ClassInfo("net.minecraft.util.IChatComponent", "ho")));
		addClassMap(new ClassMap(new ClassInfo("net.minecraft.entity.EntityLivingBase", "xm")));
	}

	public static void load() {}
	
}
