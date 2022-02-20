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

import com.lion328.thaifixes.asm.mapper.ClassDetail;
import com.lion328.thaifixes.asm.mapper.ClassMap;
import com.lion328.thaifixes.asm.util.Cell;
import com.lion328.thaifixes.asm.util.InstructionFinder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;

public class FontRendererPatcher extends SingleClassPatcher {

    private static final String fontRendererInternalName = "net/minecraft/client/gui/FontRenderer";

    private final ClassMap classMap;
    private final ClassDetail fontRendererClass;
    private final InstructionFinder<Void> finder;

    public FontRendererPatcher(ClassMap classMap) {
        this.classMap = classMap;
        fontRendererClass = classMap.getClass(fontRendererInternalName);
        finder = InstructionFinder.create()
                .withClassMap(classMap)
                .withSelfInternalName(fontRendererInternalName);
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

            finder
                    .method(Opcodes.INVOKESPECIAL)
                    .ownerSelf()
                    .whenMatch(node -> node.setOpcode(Opcodes.INVOKEVIRTUAL))
                    .find(mn.instructions);
        }

        ArrayList<FieldNode> allObjectField = new ArrayList<>();
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
                switch (fn.desc) {
                    case "F":
                        mv.visitInsn(Opcodes.FCONST_0);
                        break;
                    case "D":
                        mv.visitInsn(Opcodes.DCONST_0);
                        break;
                    case "J":
                        mv.visitInsn(Opcodes.LCONST_0);
                        break;
                    default:
                        mv.visitInsn(Opcodes.ICONST_0);
                        break;
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

    private int patchOnCharRendered(MethodNode methodNode, ClassVisitor visitor) {
        InsnList insns = methodNode.instructions;

        // Find the character variable inside the loop.
        Cell<Integer> charVarCell = new Cell<>();
        finder
                .method().owner("java/lang/String").name("charAt")
                .var(Opcodes.ISTORE).whenMatch(node -> charVarCell.set(node.var))
                .findFirst(insns);

        // Find index where the loop condition belong.
        Cell<JumpInsnNode> conditionInsnCell = new Cell<>();
        finder
                .method().owner("java/lang/String").name("length")
                .jump().whenMatch(conditionInsnCell::set)
                .findFirst(insns);

        // Find jump destination point back from the end of the loop.
        Cell<LabelNode> labelCell = new Cell<>();
        finder
                .reversed()
                .label().whenMatch(labelCell::set)
                .findFirstStartFrom(insns, conditionInsnCell.get());

        // Find a jump where its destination = firstLabelNode and insert instructions.
        finder.jump().label(labelCell.get()).whenMatch(insn -> {
            InsnList list = new InsnList();
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new VarInsnNode(Opcodes.ILOAD, charVarCell.get()));
            list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, fontRendererClass.getObfuscatedName(),
                    "onCharRenderedThaiFixes", "(C)V", false));
            insns.insertBefore(insn, list);
        }).findFirst(insns);

        // Create the method.
        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PROTECTED, "onCharRenderedThaiFixes", "(C)V",
                null, null);
        mv.visitInsn(Opcodes.RETURN);

        return charVarCell.get();
    }

    private int patchShadowShiftSize(MethodNode methodNode, ClassVisitor visitor, int charVar, int originalShiftVar) {
        InsnList insns = methodNode.instructions;

        // Find shiftVar.
        Cell<Integer> shiftVarCell = new Cell<>();
        finder
                .field(Opcodes.GETFIELD).ownerSelf().name("posY")
                .var(Opcodes.FLOAD).whenMatch(node -> shiftVarCell.set(node.var))
                .insn(Opcodes.FSUB)
                .findFirst(insns);

        // Find the instruction that set the variable and add instructions.
        finder.var(Opcodes.FSTORE).number(shiftVarCell.get()).whenMatch(node -> {
            InsnList insertList = new InsnList();
            insertList.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insertList.add(new VarInsnNode(Opcodes.ILOAD, charVar));
            insertList.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
            insertList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, fontRendererClass.getObfuscatedName(),
                    "getShadowShiftSizeThaiFixes", "(CF)F", false));
            insertList.add(new VarInsnNode(Opcodes.FSTORE, shiftVarCell.get()));
            insns.insert(node, insertList);

            node.var = originalShiftVar;
        }).findFirst(insns);

        // Add a new method for interception.
        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PROTECTED, "getShadowShiftSizeThaiFixes", "(CF)F",
                null, null);
        mv.visitVarInsn(Opcodes.FLOAD, 2);
        mv.visitInsn(Opcodes.FRETURN);
        mv.visitEnd();

        return shiftVarCell.get();
    }

    private void patchBoldShiftSize(MethodNode methodNode, ClassVisitor visitor, int charVar, int shiftVar,
                                    int originalShiftVar, int boldShiftVar) {
        InsnList insns = methodNode.instructions;

        // Find posX -= shiftVar that not immediately followed by posY.

        // 1. ALOAD 0
        // 2. DUP
        // 3. GETFIELD posX/Y
        // 4. FLOAD shiftVar
        // 5. FADD/FSUB
        // 6. PUTFIELD posX/Y
        // Total: 6 instructions
        finder
                .field(Opcodes.GETFIELD).ownerSelf().name("posX")
                .var(Opcodes.FLOAD).number(shiftVar)
                .whenMatchOnce(node -> {
                    InsnList insertList = new InsnList();
                    insertList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insertList.add(new VarInsnNode(Opcodes.ILOAD, charVar));
                    insertList.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
                    insertList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, fontRendererClass.getObfuscatedName(),
                            "getBoldShiftSizeThaiFixes", "(CF)F", false));
                    insertList.add(new VarInsnNode(Opcodes.FSTORE, boldShiftVar));

                    insns.insertBefore(node, insertList);
                })
                .whenMatch(node -> node.var = boldShiftVar)
                .skip()
                .field(Opcodes.PUTFIELD).ownerSelf().name("posX")
                .skipCountedWithCondition(2, node ->
                        !(node instanceof LabelNode || node instanceof LineNumberNode))
                .not(f -> f.field(Opcodes.GETFIELD).ownerSelf().name("posY"))
                .find(insns);

        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PROTECTED, "getBoldShiftSizeThaiFixes", "(CF)F",
                null, null);
        mv.visitVarInsn(Opcodes.FLOAD, 2);
        mv.visitInsn(Opcodes.FRETURN);
        mv.visitEnd();
    }
}
