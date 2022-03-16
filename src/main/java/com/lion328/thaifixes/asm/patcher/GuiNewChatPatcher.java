/*
 * Copyright (c) 2017 Waritnan Sookbuntherng
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

package com.lion328.thaifixes.asm.patcher;

import com.lion328.thaifixes.asm.mapper.ClassMap;
import com.lion328.thaifixes.asm.util.InstructionFinder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiNewChatPatcher extends SingleClassPatcher {

    private ClassMap classMap;

    public GuiNewChatPatcher(ClassMap classMap) {
        this.classMap = classMap;
    }

    public static MethodInsnNode getInjectedIntMethod(String methodName) {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/lion328/thaifixes/InjectedConstants", methodName, "()I", false);
    }

    @Override
    public String getClassName() {
        return classMap.getClass("net/minecraft/client/gui/GuiNewChat").getObfuscatedName().replace('/', '.');
    }

    @Override
    public byte[] patch(byte[] original) {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        for (MethodNode mn : n.methods) {
            InsnList insns = mn.instructions;

            for (int i = 0; i < insns.size(); i++) {
                AbstractInsnNode insn = insns.get(i);

                if (replaceFontHeight(insns, insn)) {
                    continue;
                }

                if (replaceChatLineHeight(insns, insn)) {
                    continue;
                }

                replaceTextYOffset(insns, insn);
            }
        }

        n.methods.forEach(method -> {
            InsnList list = method.instructions;
            InstructionFinder<Void> finder = InstructionFinder.create().withClassMap(classMap);

            finder
                    .var(Opcodes.ALOAD).number(0).whenMatch(list::remove)
                    .field(Opcodes.GETFIELD).whenMatch(list::remove)
                    .field(Opcodes.GETFIELD).whenMatch(list::remove)
                    .field(Opcodes.GETFIELD).owner("net/minecraft/client/gui/FontRenderer").desc("I")
                    .whenMatch(node -> list.insert(getConfigFontHeightMethod()))
                    .whenMatch(list::remove)
                    .find(list);
        });

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        n.accept(w);

        return w.toByteArray();
    }

    private MethodInsnNode getConfigFontHeightMethod() {
        return getInjectedIntMethod("getFontHeight");
    }

    private boolean replaceFontHeight(InsnList insns, AbstractInsnNode insn) {
        if (insn.getOpcode() != Opcodes.GETFIELD) {
            return false;
        }

        FieldInsnNode fieldInsn = (FieldInsnNode) insn;

        if (!fieldInsn.owner.equals(classMap.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName())) {
            return false;
        }

        if (!fieldInsn.desc.equals("I")) {
            return false;
        }

        insns.insert(insn, getConfigFontHeightMethod());

        int i = insns.indexOf(insn) - 3;

        insns.remove(insns.get(i)); // ALOAD 0
        insns.remove(insns.get(i)); // GETFIELD GuiNewChat.mc
        insns.remove(insns.get(i)); // GETFIELD Minecraft.fontRendererObj
        insns.remove(insns.get(i)); // GETFIELD FontRenderer.FONT_HEIGHT

        return true;
    }

    private boolean replaceChatLineHeight(InsnList insns, AbstractInsnNode insn) {
        if (insn.getOpcode() != Opcodes.BIPUSH) {
            return false;
        }

        IntInsnNode intInsn = (IntInsnNode) insn;

        if (intInsn.operand != 9) {
            return false;
        }

        insns.insert(insn, getConfigFontHeightMethod());
        insns.remove(insn); // BIPUSH 9

        return true;
    }

    private boolean replaceTextYOffset(InsnList insns, AbstractInsnNode insn) {
        if (insn.getOpcode() != Opcodes.BIPUSH) {
            return false;
        }

        IntInsnNode intInsn = (IntInsnNode) insn;

        if (intInsn.operand != 8) {
            return false;
        }

        insns.insert(insn, getInjectedIntMethod("getChatLineTextYOffset"));
        insns.remove(insn); // BIPUSH 8

        return true;
    }
}
