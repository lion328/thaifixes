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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class MinecraftPatcher implements IClassPatcher
{

    private IClassMap classMap;

    public MinecraftPatcher(IClassMap classMap)
    {
        this.classMap = classMap;
    }

    @Override
    public String getClassName()
    {
        return classMap.getClass("net/minecraft/client/Minecraft").getObfuscatedName().replace('/', '.');
    }

    @Override
    public byte[] patch(byte[] original) throws Exception
    {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        OUT:
        for (MethodNode mn : n.methods)
        {
            InsnList insns = mn.instructions;
            for (int i = 0; i < insns.size(); i++)
            {
                AbstractInsnNode insn = insns.get(i);
                if (insn.getOpcode() != Opcodes.LDC)
                {
                    continue;
                }
                LdcInsnNode ldc = (LdcInsnNode) insn;
                if (!ldc.cst.equals("textures/font/ascii.png"))
                {
                    continue;
                }
                for (i--; i < insns.size(); i--)
                {
                    if (insns.get(i).getOpcode() != Opcodes.NEW)
                    {
                        continue;
                    }
                    TypeInsnNode type = (TypeInsnNode) insns.get(i);
                    if (type.desc.equals(classMap.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName()))
                    {
                        type.desc = "com/lion328/thaifixes/FontRendererWrapper";
                        break;
                    }
                }
                for (; i < insns.size(); i++)
                {
                    if (insns.get(i).getOpcode() != Opcodes.INVOKESPECIAL)
                    {
                        continue;
                    }
                    if (((MethodInsnNode) insns.get(i)).owner.equals(classMap.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName()))
                    {
                        MethodInsnNode method = (MethodInsnNode) insns.get(i);
                        method.owner = "com/lion328/thaifixes/FontRendererWrapper";
                        break;
                    }
                }
                break OUT;
            }
        }

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        n.accept(w);
        return w.toByteArray();
    }
}
