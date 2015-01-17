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

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(name = ThaiFixesCore.NAME, modid = ThaiFixesCore.MODID, version = ThaiFixesCore.VERSION)
public class ThaiFixesCore {

	public static final String MODID = "thaifixes", NAME = "ThaiFixes", VERSION = "v1.7.10-2.1-pre-2", MCVERSION = "1.7.10";
	public static final boolean OBFUSCATED = true, USING_OPTIFINE = isClassFound("optifine.OptiFineClassTransformer");
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		if(USING_OPTIFINE) System.out.println("Found OptiFine.");
		ThaiFixesConfiguration.loadConfig(Minecraft.getMinecraft().mcDataDir);
		try {
			if(ThaiFixesConfiguration.getFontStyle() != ThaiFixesFontStyle.DISABLE) {
				System.out.println("Converting Minecraft's font renderer...");
				Minecraft.getMinecraft().fontRendererObj = ThaiFixesFontRenderer.convert(null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isClassFound(String name) {
		try {
			Class.forName(name);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}
