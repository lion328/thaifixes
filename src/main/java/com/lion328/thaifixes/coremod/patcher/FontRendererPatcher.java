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

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;

public class FontRendererPatcher implements IClassPatcher
{

    private IClassMap classMap;

    public FontRendererPatcher(IClassMap classMap)
    {
        this.classMap = classMap;
    }

    @Override
    public String getClassName()
    {
        return classMap.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName().replace('/', '.');
    }

    @Override
    public byte[] patch(byte[] original)
    {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        for (MethodNode mn : n.methods)
        {
            if ((mn.access & Opcodes.ACC_PRIVATE) != 0)
            {
                mn.access |= Opcodes.ACC_PROTECTED;
                mn.access &= ~Opcodes.ACC_PRIVATE;
            }
            InsnList insns = mn.instructions;
            for (int i = 0; i < insns.size(); i++)
            {
                AbstractInsnNode insn = insns.get(i);
                if (insn.getOpcode() == Opcodes.INVOKESPECIAL)
                {
                    MethodInsnNode methodInsn = (MethodInsnNode) insn;
                    if (methodInsn.owner.equals(classMap.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName()))
                    {
                        methodInsn.setOpcode(Opcodes.INVOKEVIRTUAL);
                    }
                }
            }
        }

        ArrayList<FieldNode> allObjectField = new ArrayList<FieldNode>();
        for (FieldNode fn : n.fields)
        {
            if ((fn.access & Opcodes.ACC_STATIC) == 0)
            {
                allObjectField.add(fn);
            }
            if ((fn.access & Opcodes.ACC_PRIVATE) != 0)
            {
                fn.access |= Opcodes.ACC_PROTECTED;
                fn.access &= ~Opcodes.ACC_PRIVATE;
            }
        }

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        MethodVisitor mv = w.visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null);
        mv.visitCode();
        for (FieldNode fn : allObjectField)
        {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            if (fn.desc.length() == 1)
            { // primitives
                switch (fn.desc)
                {
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
            }
            else
            {
                mv.visitInsn(Opcodes.ACONST_NULL);
            }
            mv.visitFieldInsn(Opcodes.PUTFIELD, classMap.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName(), fn.name, fn.desc);
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        n.accept(w);
        return w.toByteArray();
    }
}
