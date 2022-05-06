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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.function.Consumer;

public class FontRendererPatcher extends SingleClassPatcher {
    private static final String fontRendererClassInternalName = "net/minecraft/client/gui/FontRenderer";
    private static final String renderingPackagePath = "com/lion328/thaifixes/rendering";
    private static final String fontPackagePath = renderingPackagePath + "/font";
    private static final String fontClassInternalName = fontPackagePath + "/Font";
    private static final String fontClassDescriptor = Type.getObjectType(fontClassInternalName).getDescriptor();
    private static final String stubFontClassInternalName = fontPackagePath + "/StubFont";

    private final ClassMap classMap;
    private final ClassDetail fontRendererClass;
    private final String fontRendererClassInternalNameObfuscated;
    private final InstructionFinder<Void> finder;

    public FontRendererPatcher(ClassMap classMap) {
        this.classMap = classMap;
        fontRendererClass = classMap.getClassFromInternalName(fontRendererClassInternalName);
        fontRendererClassInternalNameObfuscated = fontRendererClass.getObfuscatedInternalName();
        finder = InstructionFinder.create()
                .withClassMap(classMap)
                .withSelfInternalName(fontRendererClassInternalName);
    }

    @Override
    public String getClassName() {
        return fontRendererClass.getObfuscatedName();
    }

    @Override
    public boolean tryPatch(ClassNode n) throws Exception {
        // Set ExtendedFontRenderer as a parent.
        n.interfaces.add(renderingPackagePath + "/ExtendedFontRenderer");

        // Patch FontRenderer methods.
        for (MethodNode mn : n.methods) {
            if (mn.name.equals("<init>"))
                patchInit(mn);
            if (checkMethodNodeObfuscated(mn, "renderCharAtPos", "(CZ)F"))
                patchRenderCharAtPos(mn);
            if (checkMethodNodeObfuscated(mn, "renderStringAtPos", "(Ljava/lang/String;Z)V"))
                patchRenderStringAtPos(mn);
            if (checkMethodNodeObfuscated(mn, "getCharWidth", "(C)I"))
                patchGetCharWidth(mn);
            if (checkMethodNode(mn, "getCharWidthFloat", "(C)F"))
                patchGetCharWidthFloat(mn);
        }

        // Add new fields.
        PatcherUtil.addPrivateField(n, "fontThaiFixes", fontClassDescriptor);
        PatcherUtil.addPrivateField(n, "lastCharThaiFixes", "C");
        PatcherUtil.addPrivateField(n, "lastCharShiftThaiFixes", "F");

        // Implement getters.
        implGetterObfuscated(n, "glyphWidth", "[B", "getGlyphWidthThaiFixes");
        implGetterObfuscated(n, "posX", "F", "getXThaiFixes");
        implGetterObfuscated(n, "posY", "F", "getYThaiFixes");
        implGetterObfuscated(n, "renderEngine",
                classMap.getClass("net.minecraft.client.renderer.texture.TextureManager").getObfuscatedType()
                        .getDescriptor(),
                "getTextureManagerThaiFixes");
        implGetter(n, "fontThaiFixes", fontClassDescriptor, "getFontThaiFixes");
        implGetter(n, "lastCharThaiFixes", "C", "getLastCharacterRenderedThaiFixes");
        implGetter(n, "lastCharShiftThaiFixes", "F", "getLastCharacterShiftOriginalThaiFixes");

        // Implement setters.
        implSetterObfuscated(n, "posX", "F", "setXThaiFixes");
        implSetter(n, "fontThaiFixes", fontClassDescriptor, "setFontThaiFixes");

        // Implement proxy methods.
        proxyMethodObfuscated(n, "loadGlyphTexture", "loadGlyphTextureThaiFixes", "(I)V");
        proxyMethodObfuscated(n, "setUnicodeFlag", "setUnicodeFlagThaiFixes", "(Z)V");

        return true;
    }

    private void implGetterObfuscated(ClassVisitor visitor, String field, String desc, String methodName) {
        implGetter(visitor, fontRendererClass.getField(field), desc, methodName);
    }

    private void implGetter(ClassVisitor visitor, String field, String desc, String methodName) {
        PatcherUtil.implGetterSelf(visitor, fontRendererClassInternalNameObfuscated, field, desc, methodName);
    }

    private void implSetterObfuscated(ClassVisitor visitor, String field, String desc, String methodName) {
        implSetter(visitor, fontRendererClass.getField(field), desc, methodName);
    }

    private void implSetter(ClassVisitor visitor, String field, String desc, String methodName) {
        PatcherUtil.implSetterSelf(visitor, fontRendererClassInternalNameObfuscated, field, desc, methodName);
    }

    private void proxyMethodObfuscated(ClassVisitor visitor, String originalName, String newName, String desc) {
        PatcherUtil.proxyMethodSelf(visitor, fontRendererClassInternalNameObfuscated,
                fontRendererClass.getMethod(originalName, desc), newName, desc);
    }

    private void callFontMethod(InsnList i, String name, String desc) {
        callFontMethod(i, name, desc, x -> {
        });
    }

    private void callFontMethod(InsnList i, String name, String desc, Consumer<InsnList> lambda) {
        i.add(new VarInsnNode(Opcodes.ALOAD, 0));
        i.add(new FieldInsnNode(Opcodes.GETFIELD, fontRendererClassInternalNameObfuscated, "fontThaiFixes",
                fontClassDescriptor));
        lambda.accept(i);
        i.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, fontClassInternalName, name, desc, true));
    }

    private void ifCharSupported(InsnList i, int charVar, Consumer<InsnList> lambda) {
        LabelNode labelNode = new LabelNode();
        callFontMethod(i, "isSupportedCharacter", "(C)Z",
                j -> j.add(new VarInsnNode(Opcodes.ILOAD, charVar)));
        i.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        lambda.accept(i);
        i.add(labelNode);
    }

    private boolean checkMethodNodeObfuscated(MethodNode mn, String name, String desc) {
        return checkMethodNode(mn, fontRendererClass.getMethod(name, desc), desc);
    }

    private boolean checkMethodNode(MethodNode mn, String name, String desc) {
        return mn.name.equals(name) && mn.desc.equals(desc);
    }

    private InsnList putField(InsnList insns, String name, String desc, AbstractInsnNode load) {
        return putField(insns, name, desc, i -> i.add(load));
    }

    private InsnList putField(InsnList insns, String name, String desc, Consumer<InsnList> lambda) {
        return PatcherUtil.putFieldSelf(insns, fontRendererClassInternalNameObfuscated, name, desc, lambda);
    }

    private void patchInit(MethodNode mn) {
        finder.insn(Opcodes.RETURN).whenMatch(node -> {
            InsnList patch = new InsnList();

            putField(patch, "fontThaiFixes", fontClassDescriptor, i -> {
                i.add(new TypeInsnNode(Opcodes.NEW, stubFontClassInternalName));
                i.add(new InsnNode(Opcodes.DUP));
                i.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, stubFontClassInternalName, "<init>", "()V",
                        false));
            });
            putField(patch, "lastCharThaiFixes", "C", new InsnNode(Opcodes.ICONST_0));
            putField(patch, "lastCharShiftThaiFixes", "F", new LdcInsnNode(Float.NaN));

            mn.instructions.insertBefore(node, patch);
        }).find(mn.instructions);
    }

    private void patchRenderCharAtPos(MethodNode mn) {
        InsnList instructions = mn.instructions;

        InsnList fontRenderPatch = new InsnList();

        ifCharSupported(fontRenderPatch, 1, j -> {
            j.add(new VarInsnNode(Opcodes.ILOAD, 1)); // character
            callFontMethod(j, "renderCharacter", "(CZ)F", i -> {
                i.add(new VarInsnNode(Opcodes.ILOAD, 1));
                i.add(new VarInsnNode(Opcodes.ILOAD, 2)); // italic
            });
            j.add(new InsnNode(Opcodes.FRETURN));
        });

        instructions.insertBefore(instructions.getFirst(), fontRenderPatch);

        finder.insn(Opcodes.FRETURN).whenMatch(node -> {
            InsnList lastCharShift = new InsnList();
            lastCharShift.add(new InsnNode(Opcodes.DUP));
            lastCharShift.add(new VarInsnNode(Opcodes.ALOAD, 0));
            lastCharShift.add(new InsnNode(Opcodes.SWAP));
            lastCharShift.add(new FieldInsnNode(Opcodes.PUTFIELD, fontRendererClassInternalNameObfuscated,
                    "lastCharShiftThaiFixes", "F"));
            instructions.insertBefore(node, lastCharShift);
        }).find(instructions);
    }

    private void patchGetCharWidth(MethodNode mn) {
        InsnList instructions = mn.instructions;

        InsnList patch = new InsnList();
        ifCharSupported(patch, 1, i -> {
            callFontMethod(i, "getCharacterWidth", "(C)I",
                    j -> j.add(new VarInsnNode(Opcodes.ILOAD, 1)));
            i.add(new InsnNode(Opcodes.IRETURN));
        });

        instructions.insertBefore(instructions.getFirst(), patch);
    }

    private void patchGetCharWidthFloat(MethodNode mn) {
        InsnList instructions = mn.instructions;

        InsnList patch = new InsnList();
        ifCharSupported(patch, 1, i -> {
            callFontMethod(i, "getCharacterWidth", "(C)I",
                    j -> j.add(new VarInsnNode(Opcodes.ILOAD, 1)));
            i.add(new InsnNode(Opcodes.I2F));
            i.add(new InsnNode(Opcodes.FRETURN));
        });

        instructions.insertBefore(instructions.getFirst(), patch);
    }

    private void patchRenderStringAtPos(MethodNode mn) {
        int charVar = findRenderStringAtPosCharVar(mn);
        int shiftVar = findRenderStringAtPosShiftVar(mn);

        patchRenderStringAtPosPrePostString(mn);
        patchRenderStringAtPosPostChar(mn, charVar);
        patchRenderStringAtPosShift(mn, charVar, shiftVar);
    }

    private int findRenderStringAtPosCharVar(MethodNode mn) {
        // Find the character variable inside the loop.
        Cell<Integer> charVarCell = new Cell<>();
        finder
                .method().owner("java/lang/String").name("charAt")
                .var(Opcodes.ISTORE).whenMatch(node -> charVarCell.set(node.var))
                .findFirst(mn.instructions);
        return charVarCell.get();
    }

    private int findRenderStringAtPosShiftVar(MethodNode mn) {
        Cell<Integer> shiftVarCell = new Cell<>();
        finder
                .field(Opcodes.GETFIELD).ownerSelf().name("posY")
                .var(Opcodes.FLOAD).whenMatch(node -> shiftVarCell.set(node.var))
                .insn(Opcodes.FSUB)
                .findFirst(mn.instructions);
        return shiftVarCell.get();
    }

    private void patchRenderStringAtPosPrePostString(MethodNode mn) {
        InsnList instructions = mn.instructions;

        InsnList lastCharReset = new InsnList();
        lastCharReset.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lastCharReset.add(new InsnNode(Opcodes.ICONST_0));
        lastCharReset.add(new FieldInsnNode(Opcodes.PUTFIELD, fontRendererClassInternalNameObfuscated,
                "lastCharThaiFixes", "C"));

        InsnList patchStart = new InsnList();
        patchStart.add(lastCharReset);
        callFontMethod(patchStart, "preStringRendered", "(Ljava/lang/String;)Ljava/lang/String;", i -> {
            i.add(new VarInsnNode(Opcodes.ALOAD, 1)); // text
        });
        patchStart.add(new VarInsnNode(Opcodes.ASTORE, 1));

        instructions.insertBefore(instructions.getFirst(), patchStart);

        finder.insn(Opcodes.RETURN).whenMatch(insn -> {
            InsnList patchEnd = new InsnList();
            callFontMethod(patchEnd, "postStringRendered", "()V");
            patchEnd.add(lastCharReset);
            instructions.insertBefore(insn, patchEnd);
        }).find(instructions);
    }

    private void patchRenderStringAtPosPostChar(MethodNode mn, int charVar) {
        InsnList insns = mn.instructions;

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
            InsnList patch = new InsnList();
            patch.add(new VarInsnNode(Opcodes.ALOAD, 0));
            patch.add(new VarInsnNode(Opcodes.ILOAD, charVar));
            patch.add(new FieldInsnNode(Opcodes.PUTFIELD, fontRendererClassInternalNameObfuscated,
                    "lastCharThaiFixes", "C"));

            callFontMethod(patch, "postCharacterRendered", "(C)V",
                    i -> i.add(new VarInsnNode(Opcodes.ILOAD, charVar)));

            insns.insertBefore(insn, patch);
        }).findFirst(insns);
    }

    private void patchRenderStringAtPosShift(MethodNode mn, int charVar, int shiftVar) {
        InsnList insns = mn.instructions;

        // A new variable storing the original content of the shift variable.
        int originalShiftVar = mn.maxLocals;
        int boldShiftVar = originalShiftVar + 1;

        // Find the instruction that set the variable and add instructions.
        finder.var(Opcodes.FSTORE).number(shiftVar).whenMatch(node -> {
            InsnList patch = new InsnList();

            callFontMethod(patch, "preCharacterRendered", "(C)V", i ->
                    i.add(new VarInsnNode(Opcodes.ILOAD, charVar)));

            LabelNode outside = new LabelNode();

            ifCharSupported(patch, charVar, i -> {
                callFontMethod(i, "getShadowShiftSize", "(CF)F", j -> {
                    j.add(new VarInsnNode(Opcodes.ILOAD, charVar));
                    j.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
                });
                i.add(new JumpInsnNode(Opcodes.GOTO, outside));
            });

            patch.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
            patch.add(outside);
            patch.add(new VarInsnNode(Opcodes.FSTORE, shiftVar));

            insns.insert(node, patch);
            node.var = originalShiftVar;
        }).findFirst(insns);

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
                    InsnList patch = new InsnList();

                    LabelNode outside = new LabelNode();

                    ifCharSupported(patch, charVar, i -> {
                        callFontMethod(i, "getBoldShiftSize", "(CF)F", j -> {
                            j.add(new VarInsnNode(Opcodes.ILOAD, charVar));
                            j.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
                        });
                        i.add(new JumpInsnNode(Opcodes.GOTO, outside));
                    });

                    patch.add(new VarInsnNode(Opcodes.FLOAD, originalShiftVar));
                    patch.add(outside);
                    patch.add(new VarInsnNode(Opcodes.FSTORE, boldShiftVar));

                    insns.insertBefore(node, patch);
                })
                .whenMatch(node -> node.var = boldShiftVar)
                .skip()
                .field(Opcodes.PUTFIELD).ownerSelf().name("posX")
                .skipCountedWithCondition(2, node ->
                        !(node instanceof LabelNode || node instanceof LineNumberNode))
                .not(f -> f.field(Opcodes.GETFIELD).ownerSelf().name("posY"))
                .find(insns);
    }
}
