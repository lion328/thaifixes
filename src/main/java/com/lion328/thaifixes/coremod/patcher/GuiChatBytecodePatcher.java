/*
 * Copyright (c) 2014-2015 Waritnan Sookbuntherng
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

package com.lion328.thaifixes.coremod.patcher;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.lion328.thaifixes.ThaiFixesConfiguration;
import com.lion328.thaifixes.ThaiFixesFontRenderer;
import com.lion328.thaifixes.ThaiFixesFontStyle;
import com.lion328.thaifixes.classmap.ClassMap;

public class GuiChatBytecodePatcher implements IBytecodePatcher {

	public static final ClassMap CLASSMAP = ClassMap.getClassMap("net.minecraft.client.gui.GuiChat");
	
	@Override
	public byte[] patchClass(byte[] source) {
		if(ThaiFixesConfiguration.getFontStyle() != ThaiFixesFontStyle.MCPX) return source;
		
		ClassReader classReader = new ClassReader(source);
		ClassNode classNode = new ClassNode();
		classReader.accept(classNode, 0);
		
		for(MethodNode method : classNode.methods) {
			boolean drawScreenFlag;
			if((drawScreenFlag = (method.name.equals(CLASSMAP.getMethod("drawScreen")) && method.desc.equals("(IIF)V"))) || (method.name.equals(CLASSMAP.getMethod("initGui")) && method.desc.equals("()V"))) {
				for(int i = 0; i < method.instructions.size(); i++) {
					if((method.instructions.get(i).getOpcode() == Opcodes.BIPUSH) && (method.instructions.get(i + 1).getOpcode() == Opcodes.ISUB)) {
						IntInsnNode node = (IntInsnNode)method.instructions.get(i);
						if(node.operand == (drawScreenFlag ? 14 : 12)) method.instructions.set(node, new VarInsnNode(Opcodes.BIPUSH, (drawScreenFlag ? ThaiFixesFontRenderer.MCPX_CHATBLOCK_HEIGHT + 2 : ThaiFixesFontRenderer.MCPX_CHATBLOCK_TEXT_YPOS + 2)));
					}
				}
			}
		}
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	@Override
	public ClassMap getClassMap() {
		return CLASSMAP;
	}

}
