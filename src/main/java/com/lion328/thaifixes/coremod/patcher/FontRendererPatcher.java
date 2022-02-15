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

package com.lion328.thaifixes.coremod.patcher;

import com.lion328.thaifixes.coremod.mapper.IClassDetail;
import com.lion328.thaifixes.coremod.mapper.IClassMap;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;

public class FontRendererPatcher extends SingleClassPatcher {

    private IClassMap classMap;
    private IClassDetail fontRendererClass;

    public FontRendererPatcher(IClassMap classMap) {
        this.classMap = classMap;
        fontRendererClass = classMap.getClass("net/minecraft/client/gui/FontRenderer");
    }

    @Override
    public String getClassName() {
        return fontRendererClass.getObfuscatedName().replace('/', '.');
    }

    @Override
    public byte[] patch(byte[] original) {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        for (MethodNode mn : n.methods) {
            if ((mn.access & Opcodes.ACC_PRIVATE) != 0) {
                mn.access |= Opcodes.ACC_PROTECTED;
                mn.access &= ~Opcodes.ACC_PRIVATE;
            }
            InsnList insns = mn.instructions;
            for (int i = 0; i < insns.size(); i++) {
                AbstractInsnNode insn = insns.get(i);
                if (insn.getOpcode() == Opcodes.INVOKESPECIAL) {
                    MethodInsnNode methodInsn = (MethodInsnNode) insn;
                    if (methodInsn.owner.equals(fontRendererClass.getObfuscatedName())) {
                        methodInsn.setOpcode(Opcodes.INVOKEVIRTUAL);
                    }
                }
            }
        }

        ArrayList<FieldNode> allObjectField = new ArrayList<FieldNode>();
        for (FieldNode fn : n.fields) {
            if ((fn.access & Opcodes.ACC_STATIC) == 0) {
                allObjectField.add(fn);
            }
            if ((fn.access & Opcodes.ACC_PRIVATE) != 0) {
                fn.access |= Opcodes.ACC_PROTECTED;
                fn.access &= ~Opcodes.ACC_PRIVATE;
            }
        }

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        MethodVisitor mv = w.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null);
        mv.visitCode();
        for (FieldNode fn : allObjectField) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            if (fn.desc.length() == 1) { // primitives
                if (fn.desc.equals("F")) {
                    mv.visitInsn(Opcodes.FCONST_0);
                } else if (fn.desc.equals("D")) {
                    mv.visitInsn(Opcodes.DCONST_0);
                } else if (fn.desc.equals("J")) {
                    mv.visitInsn(Opcodes.LCONST_0);
                } else {
                    mv.visitInsn(Opcodes.ICONST_0);
                }
            } else {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }
            mv.visitFieldInsn(Opcodes.PUTFIELD, fontRendererClass.getObfuscatedName(), fn.name, fn.desc);
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        patchRenderStringAtPos(n, w);
        patchIndicator(w);

        n.accept(w);
        return w.toByteArray();
    }

    private void patchIndicator(ClassVisitor visitor) {
        visitor.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                "PATCHED_BY_THAIFIXES", "Z", null, true).visitEnd();
    }

    private void patchRenderStringAtPos(ClassNode classNode, ClassVisitor visitor) {
        String desc = "(Ljava/lang/String;Z)V";
        String name = fontRendererClass.getMethod("renderStringAtPos", desc);

        for (MethodNode mn : classNode.methods) {
            if (!name.equals(mn.name) || !desc.equals(mn.desc)) {
                continue;
            }

            // A new variable storing the original content of the shift variable.
            int originalShiftVar = mn.maxLocals;
            int boldShiftVar = originalShiftVar + 1;

            int charVar = patchOnCharRendered(mn, visitor);
            int shiftVar = patchShadowShiftSize(mn, visitor, charVar, originalShiftVar);
            patchBoldShiftSize(mn, visitor, charVar, shiftVar, originalShiftVar, boldShiftVar);

            break;
        }
    }

    private int patchOnCharRendered(MethodNode node, ClassVisitor visitor) {
        InsnList insns = node.instructions;
        // Find the character variable inside the loop.
        int charVar = -1;
        for (int i = 0; i < insns.size(); i++) {
            AbstractInsnNode absInsn = insns.get(i);

            if (absInsn instanceof MethodInsnNode) {
                MethodInsnNode insn = (MethodInsnNode) absInsn;
                if (!"java/lang/String".equals(insn.owner) || !"charAt".equals(insn.name))
                    continue;

                AbstractInsnNode nextInsn = insns.get(i + 1);
                if (nextInsn.getOpcode() == Opcodes.ISTORE) {
                    charVar = ((VarInsnNode) nextInsn).var;
                    break;
                }
            }
        }

        // Find index where the loop condition belong.
        int conditionIdx = -1;
        for (int i = 0; i < insns.size(); i++) {
            AbstractInsnNode insn0 = insns.get(i);
            AbstractInsnNode insn1 = insns.get(i + 1);

            if (!(insn1 instanceof JumpInsnNode && insn0 instanceof MethodInsnNode))
                continue;

            MethodInsnNode insn = (MethodInsnNode) insn0;
            if ("java/lang/String".equals(insn.owner) && "length".equals(insn.name)) {
                conditionIdx = i;
                break;
            }
        }

        // Find jump destination point back from the end of the loop.
        LabelNode firstLabelNode = null;
        for (int i = conditionIdx - 1; i >= 0; i--) {
            AbstractInsnNode insn = insns.get(i);

            if (insn instanceof LabelNode) {
                firstLabelNode = (LabelNode) insn;
                break;
            }
        }

        // Find a jump where its destination = firstLabelNode.
        JumpInsnNode jumpInsn = null;
        for (int i = 0; i < insns.size(); i++) {
            AbstractInsnNode absInsn = insns.get(i);
            if (absInsn instanceof JumpInsnNode) {
                JumpInsnNode insn = (JumpInsnNode) absInsn;
                if (insn.label == firstLabelNode) {
                    jumpInsn = insn;
                    break;
                }
            }
        }

        InsnList insertList = new InsnList();
        insertList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insertList.add(new VarInsnNode(Opcodes.ILOAD, charVar));
        insertList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, fontRendererClass.getObfuscatedName(),
                "onCharRenderedThaiFixes", "(C)V", false));
        insns.insertBefore(jumpInsn, insertList);

        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PROTECTED, "onCharRenderedThaiFixes", "(C)V",
                null, null);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitEnd();

        return charVar;
    }

    private int patchShadowShiftSize(MethodNode node, ClassVisitor visitor, int charVar, int originalShiftVar) {
        String posY = fontRendererClass.getField("posY");

        int shiftVar = -1;

        InsnList insns = node.instructions;
        AbstractInsnNode absInsn;

        // Find shiftVar.
        for (int i = 0; i < insns.size(); i++) {
            absInsn = insns.get(i);
            if (absInsn.getOpcode() != Opcodes.GETFIELD || !posY.equals(((FieldInsnNode) absInsn).name))
                continue;

            absInsn = insns.get(++i);
            if (absInsn.getOpcode() != Opcodes.FLOAD)
                continue;

            if (insns.get(++i).getOpcode() == Opcodes.FSUB) {
                shiftVar = ((VarInsnNode) absInsn).var;
                break;
            }
        }

        // Find the instruction that set the variable.
        VarInsnNode setShiftVarInsn = null;
        for (int i = 0; i < insns.size(); i++) {
            absInsn = insns.get(i);
            if (absInsn.getOpcode() == Opcodes.FSTORE && ((VarInsnNode) absInsn).var == shiftVar) {
                setShiftVarInsn = (VarInsnNode) absInsn;
                break;
            }
        }

        // Intercept it.
        InsnList insertList = new InsnList();
        insertList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insertList.add(new VarInsnNode(Opcodes.ILOAD, charVar));
        insertList.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
        insertList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, fontRendererClass.getObfuscatedName(),
                "getShadowShiftSizeThaiFixes", "(CF)F", false));
        insertList.add(new VarInsnNode(Opcodes.FSTORE, shiftVar));

        setShiftVarInsn.var = originalShiftVar;
        insns.insert(setShiftVarInsn, insertList);

        // Add a new method for interception.
        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PROTECTED, "getShadowShiftSizeThaiFixes", "(CF)F",
                null, null);
        mv.visitVarInsn(Opcodes.FLOAD, 2);
        mv.visitInsn(Opcodes.FRETURN);
        mv.visitEnd();

        return shiftVar;
    }

    private void patchBoldShiftSize(MethodNode node, ClassVisitor visitor, int charVar, int shiftVar,
                                    int originalShiftVar, int boldShiftVar) {
        String posX = fontRendererClass.getField("posX");
        String posY = fontRendererClass.getField("posY");

        InsnList insns = node.instructions;
        AbstractInsnNode absInsn;
        boolean getVarInserted = false;

        // Find posX -= shiftVar that not immediately followed by posY.
        for (int i = 0; i < insns.size(); i++) {
            absInsn = insns.get(i);

            // Basically,
            // 1. ALOAD 0
            // 2. DUP
            // 3. GETFIELD posX/Y
            // 4. FLOAD shiftVar
            // 5. FADD/FSUB
            // 6. PUTFIELD posX/Y
            // Total: 6 instructions

            if (absInsn.getOpcode() != Opcodes.GETFIELD || !posX.equals(((FieldInsnNode) absInsn).name))
                continue;

            absInsn = insns.get(++i);
            if (absInsn.getOpcode() != Opcodes.FLOAD || ((VarInsnNode) absInsn).var != shiftVar)
                continue;
            VarInsnNode floadInsn = (VarInsnNode) absInsn;

            i++; // Skip FADD/FSUB.
            absInsn = insns.get(++i);
            if (absInsn.getOpcode() != Opcodes.PUTFIELD || !posX.equals(((FieldInsnNode) absInsn).name))
                break;

            for (int j = 0; j < 3; ) {
                absInsn = insns.get(++i);
                if (absInsn instanceof LabelNode || absInsn instanceof LineNumberNode)
                    continue;
                j++;
            }

            // Only GETFIELD posY check should be sufficient.
            if (absInsn.getOpcode() == Opcodes.GETFIELD && posY.equals(((FieldInsnNode) absInsn).name))
                continue;

            if (!getVarInserted) {
                InsnList insertList = new InsnList();
                insertList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insertList.add(new VarInsnNode(Opcodes.ILOAD, charVar));
                insertList.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
                insertList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, fontRendererClass.getObfuscatedName(),
                        "getBoldShiftSizeThaiFixes", "(CF)F", false));
                insertList.add(new InsnNode(Opcodes.DUP));
                insertList.add(new VarInsnNode(Opcodes.FSTORE, boldShiftVar));

                insns.insert(floadInsn, insertList);
                insns.remove(floadInsn);

                getVarInserted = true;
            } else {
                floadInsn.var = boldShiftVar;
            }
        }

        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PROTECTED, "getBoldShiftSizeThaiFixes", "(CF)F",
                null, null);
        mv.visitVarInsn(Opcodes.FLOAD, 2);
        mv.visitInsn(Opcodes.FRETURN);
        mv.visitEnd();
    }
}
