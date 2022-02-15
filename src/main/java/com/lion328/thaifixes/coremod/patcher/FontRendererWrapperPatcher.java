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

import com.lion328.thaifixes.coremod.ClassMapManager;
import com.lion328.thaifixes.coremod.mapper.IClassMap;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FontRendererWrapperPatcher extends SingleClassPatcher {

    private IClassMap classMap;

    public FontRendererWrapperPatcher(IClassMap classMap) {
        this.classMap = classMap;
    }

    @Override
    public String getClassName() {
        return "com.lion328.thaifixes.FontRendererWrapper";
    }

    @Override
    public byte[] patch(byte[] original) throws Exception {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        String target = "com/lion328/thaifixes/FakeFontRenderer";
        String replace = "net/minecraft/client/gui/FontRenderer";

        n.superName = replace;

        for (MethodNode mn : n.methods) {
            InsnList insns = mn.instructions;
            for (int i = 0; i < insns.size(); i++) {
                AbstractInsnNode abstractInsn = insns.get(i);
                if (abstractInsn instanceof MethodInsnNode) {
                    MethodInsnNode insn = (MethodInsnNode) abstractInsn;
                    if (target.equals(insn.owner)) {
                        insn.owner = replace;
                    }
                } else if (abstractInsn instanceof FieldInsnNode) {
                    FieldInsnNode insn = (FieldInsnNode) abstractInsn;
                    if (target.equals(insn.owner)) {
                        insn.owner = replace;
                    }
                }
            }
        }

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        n.accept(w);
        byte[] stage0 = w.toByteArray();

        return new NameMapperPatcher(w.toByteArray(), ClassMapManager.getDefaultClassmap()).patch(stage0);
    }
}
