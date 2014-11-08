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

import com.lion328.thaifixes.nmod.ClassMap;
import com.lion328.thaifixes.nmod.ThaiFixesConfiguration;
import com.lion328.thaifixes.nmod.ThaiFixesCore;
import com.lion328.thaifixes.nmod.ThaiFixesFontRenderer;
import com.lion328.thaifixes.nmod.ThaiFixesFontStyle;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion(ThaiFixesCore.MCVERSION)
public class ThaiFixesLoader implements IFMLLoadingPlugin, IClassTransformer {

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


	static {
		ClassMap.load();
		ThaiFixesConfiguration.load();
		ThaiFixesFontStyle.values(); // load
	}
	
	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		return patchClass(arg0, arg2);
	}
	
	private byte[] patchClass(String classname, byte[] b) {
		String[] maps_str = new String[] {
			"net.minecraft.client.gui.GuiNewChat",
			"net.minecraft.client.gui.GuiChat",
			"net.minecraft.client.renderer.entity.RendererLivingEntity",
		};
		
		ClassMap[] maps = new ClassMap[maps_str.length];
		for(int i = 0; i < maps.length; i++) maps[i] = ClassMap.getClassMap(maps_str[i]);
		
		boolean breakFlag = true;
		for(ClassMap map : maps) if(map.getClassInfo().getProductionClassName().equals(classname)) {
			breakFlag = false;
			break;
		}
		if(breakFlag) return b;
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(b);
		classReader.accept(classNode, 0);

		for(MethodNode method : classNode.methods) {
			if(classname.equals(maps[0].getClassInfo().getProductionClassName())) {
				if(method.name.equals(maps[0].getMethod("drawChat")) && method.desc.equals("(I)V")) {
					AbstractInsnNode currentNode = null;
					for(int i = 0; i < method.instructions.size(); i++) {
						currentNode = method.instructions.get(i);
						if(currentNode.getOpcode() == Opcodes.BIPUSH) {
							if(method.instructions.get(i + 1).getOpcode() == Opcodes.IMUL) method.instructions.set(currentNode, new VarInsnNode(Opcodes.BIPUSH, ThaiFixesFontRenderer.MCPX_CHATBLOCK_HEIGHT));
							else if(method.instructions.get(i + 1).getOpcode() == Opcodes.ISUB && method.instructions.get(i - 1).getOpcode() == Opcodes.ILOAD) {
								IntInsnNode node = (IntInsnNode)currentNode;
								if(node.operand == 9) method.instructions.set(currentNode, new VarInsnNode(Opcodes.BIPUSH, ThaiFixesFontRenderer.MCPX_CHATBLOCK_HEIGHT));
								else if(node.operand == 8) method.instructions.set(currentNode, new VarInsnNode(Opcodes.BIPUSH, ThaiFixesFontRenderer.MCPX_CHATBLOCK_TEXT_YPOS));
							}
						}
					}
				} else if(method.name.equals(maps[0].getMethod("func_146236_a")) && method.desc.equals("(II)L" + ClassMap.getClassMap("net.minecraft.util.IChatComponent").getClassInfo().getProductionClassName().replace('.', '/') + ";")) {
					for(int i = 0; i < method.instructions.size(); i++) {
						if(method.instructions.get(i).getOpcode() == Opcodes.GETFIELD) {
							FieldInsnNode node = (FieldInsnNode)method.instructions.get(i);
							if(node.owner.equals(ClassMap.getClassMap("net.minecraft.client.gui.FontRenderer").getClassInfo().getProductionClassName().replace('.', '/')) && node.name.equals(ClassMap.getClassMap("net.minecraft.client.gui.FontRenderer").getField("FONT_HEIGHT"))) {
								method.instructions.set(node, new VarInsnNode(Opcodes.BIPUSH, ThaiFixesFontRenderer.MCPX_CHATBLOCK_HEIGHT));
								method.instructions.remove(method.instructions.get(i - 1)); // GETFIELD Minecraft.mc
								method.instructions.remove(method.instructions.get(i - 2)); // GETFIELD GuiNewChat.mc
								method.instructions.remove(method.instructions.get(i - 3)); // ALOAD 0
							}
						}
					}
				}
			} else if(classname.equals(maps[1].getClassInfo().getProductionClassName())) {
				boolean drawScreenFlag;
				if((drawScreenFlag = (method.name.equals(maps[1].getMethod("drawScreen")) && method.desc.equals("(IIF)V"))) || (method.name.equals(maps[1].getMethod("initGui")) && method.desc.equals("()V"))) {
					for(int i = 0; i < method.instructions.size(); i++) {
						if((method.instructions.get(i).getOpcode() == Opcodes.BIPUSH) && (method.instructions.get(i + 1).getOpcode() == Opcodes.ISUB)) {
							IntInsnNode node = (IntInsnNode)method.instructions.get(i);
							if(node.operand == (drawScreenFlag ? 14 : 12)) method.instructions.set(node, new VarInsnNode(Opcodes.BIPUSH, (drawScreenFlag ? ThaiFixesFontRenderer.MCPX_CHATBLOCK_HEIGHT + 2 : ThaiFixesFontRenderer.MCPX_CHATBLOCK_TEXT_YPOS + 2)));
						}
					}
				}
			} else if(classname.equals(maps[2].getClassInfo().getProductionClassName())) {
				/*  LDC 8.0
    			 *	DCONST_0 
    			 *  height = a_height - 1.0D
    			 *  LdcInsnNode
    			 */
				/*if(method.name.equals(entityRendererClassMap.getMethod("passSpecialRender")) && method.desc.equals("(L" + ClassMap.getClassMap("net.minecraft.entity.EntityLivingBase").getClassInfo().getProductionClassName().replace('.', '/') + ";DDD)V")) {
					for(int i = 0; i < method.instructions.size(); i++) {
						if(method.instructions.get(i).getOpcode() == Opcodes.LDC) {
							LdcInsnNode node = (LdcInsnNode)method.instructions.get(i);
							if((new Double(8.0D).equals(node.cst) || new Float(8.0F).equals(node.cst)) && (method.instructions.get(i + 1).getOpcode() == Opcodes.DCONST_0)) {
								System.out.println("HI");
								method.instructions.set(node, new LdcInsnNode((double)20.D - 1.0D));
							}
						}
					}
				}*/
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		System.out.println("Class \"" + classname + "\" is patched.");
		return writer.toByteArray();
	}
	
}
