/*
 * Copyright (c) 2016 Waritnan Sookbuntherng
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

import com.lion328.thaifixes.coremod.Configuration;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;

public class MinecraftPatcher implements IClassPatcher {

    @Override
    public String getClassName() {
        return Configuration.getDefaultClassmap().get("net.minecraft.client.Minecraft").replace('/', '.');
    }

    @Override
    public byte[] patch(byte[] original) {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        OUT: for(MethodNode mn : n.methods) {
            InsnList insns = mn.instructions;
            for(int i = 0; i < insns.size(); i++) {
                AbstractInsnNode insn = insns.get(i);
                if(insn.getOpcode() != Opcodes.LDC) continue;
                LdcInsnNode ldc = (LdcInsnNode)insn;
                if(!ldc.cst.equals("textures/font/ascii.png")) continue;
                for(i--; i < insns.size(); i--) {
                    if(insns.get(i).getOpcode() != Opcodes.NEW) continue;
                    TypeInsnNode type = (TypeInsnNode)insns.get(i);
                    if(type.desc.equals(Configuration.getDefaultClassmap().get("net.minecraft.client.gui.FontRenderer"))) {
                        type.desc = "com/lion328/thaifixes/FontRendererWrapper";
                        break;
                    }
                }
                for(; i < insns.size(); i++) {
                    if(insns.get(i).getOpcode() != Opcodes.INVOKESPECIAL) continue;
                    if(((MethodInsnNode)insns.get(i)).owner.equals(Configuration.getDefaultClassmap().get("net.minecraft.client.gui.FontRenderer"))) {
                        MethodInsnNode method = (MethodInsnNode)insns.get(i);
                        method.owner = "com/lion328/thaifixes/FontRendererWrapper";
                        break;
                    }
                }
                break OUT;
            }
        }

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        n.accept(w);
        try {
            File f = new File("f.class");
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            out.write(w.toByteArray());
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return w.toByteArray();
    }
}
