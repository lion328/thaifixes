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

package com.lion328.thaifixes.coremod;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

import com.lion328.thaifixes.coremod.patcher.*;
import com.lion328.thaifixes.nmod.ClassMap;
import com.lion328.thaifixes.nmod.ThaiFixesConfiguration;
import com.lion328.thaifixes.nmod.ThaiFixesCore;
import com.lion328.thaifixes.nmod.ThaiFixesFontRenderer;
import com.lion328.thaifixes.nmod.ThaiFixesFontStyle;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion(ThaiFixesCore.MCVERSION)
public class ThaiFixesLoader implements IFMLLoadingPlugin, IClassTransformer {

	private static final Map<String, IBytecodePatcher> patchers = new HashMap<String, IBytecodePatcher>();
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {this.getClass().getName()};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		ThaiFixesConfiguration.loadConfig((File)data.get("mcLocation"));
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public byte[] transform(String className, String arg1, byte[] source) {
		if(patchers.containsKey(className)) {
			ThaiFixesCore.getLogger().info("Patching class \"" + className + "\"...");
			return patchers.get(className).patchClass(source);
		}
		return source;
	}
	
	private static void addBytecodePatcherClass(IBytecodePatcher patcher) {
		String className = patcher.getClassMap().getClassInfo().getProductionClassName();
		if(patchers.containsKey(className)) return;
		patchers.put(className, patcher);
	}
	
	static {
		ClassMap.load();
		ThaiFixesConfiguration.load();
		ThaiFixesFontStyle.values(); // load
		
		addBytecodePatcherClass(new GuiNewChatBytecodePatcher());
		addBytecodePatcherClass(new GuiChatBytecodePatcher());
	}
}
