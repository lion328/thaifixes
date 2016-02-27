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

package com.lion328.thaifixes.coremod.mapper;

import com.lion328.thaifixes.coremod.mapper.reader.IJarReader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class V1_6_2ClassMapper implements IClassMapper {

    private final HashMap<String, String> map = new HashMap<String, String>();

    public static final V1_6_2ClassMapper instance = new V1_6_2ClassMapper();

    @Override
    public boolean getMap(IJarReader jarReader, Map<String, String> map) throws IOException {
        int defaultMapSize = map.size();
        byte[] b = jarReader.getClassBytes("net.minecraft.client.main.Main");
        if (b == null) {
            throw new IOException("Main.class not found!");
        }

        ClassReader reader = new ClassReader(b);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        InsnList insns;
        AbstractInsnNode insn;
        int i, j;
        boolean flag = false;
        L1:
        for (MethodNode method : node.methods) {
            if (method.name.equals("main") && method.desc.equals("([Ljava/lang/String;)V") && (method.access & Opcodes.ACC_PUBLIC) != 0 && (method.access & Opcodes.ACC_STATIC) != 0) {
                insns = method.instructions;
                for (i = insns.size() - 1; i > 0; i--) {
                    insn = insns.get(i);
                    switch (insn.getType()) {
                        case AbstractInsnNode.METHOD_INSN:
                            if (!flag) {
                                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                                if (methodInsn.desc.equals("()V")) {
                                    map.put("net.minecraft.client.Minecraft", methodInsn.owner);
                                    flag = true;
                                }
                            }
                            break;
                        case AbstractInsnNode.LDC_INSN:
                            LdcInsnNode ldcInsn = (LdcInsnNode) insn;
                            if (ldcInsn.cst instanceof String && (ldcInsn.cst.equals("Client thread") || ldcInsn.cst.equals("Minecraft main thread")) && flag) {
                                break L1;
                            }
                            break;
                    }
                }
                flag = false;
            }
        }

        b = jarReader.getClassBytes(map.get("net.minecraft.client.Minecraft").replace('/', '.'));
        if (b == null) {
            throw new IOException("Minecraft.class not found!");
        }

        reader = new ClassReader(b);
        node = new ClassNode();
        reader.accept(node, 0);

        byte bFlag = 0;
        L2:
        for (MethodNode method : node.methods) {
            if (bFlag >= 5) break;
            insns = method.instructions;
            if (method.desc.equals("()L" + map.get("net.minecraft.client.Minecraft") + ";")) {
                map.put("net.minecraft.client.Minecraft.getMinecraft", method.name);
                bFlag++;
                continue;
            }
            for (i = 0; i < insns.size(); i++) {
                insn = insns.get(i);
                if (insn.getType() == AbstractInsnNode.LDC_INSN) {
                    LdcInsnNode ldcInsn = (LdcInsnNode) insn;
                    if (ldcInsn.cst instanceof String) {
                        String cst = (String) ldcInsn.cst;
                        if (cst.equals("textures/font/ascii.png")) {
                            j = i;
                            while ((insn = insns.get(--j)).getOpcode() != Opcodes.NEW) ;
                            TypeInsnNode typeInsn = (TypeInsnNode) insn;
                            map.put("net.minecraft.util.ResourceLocation", typeInsn.desc);
                            while ((insn = insns.get(j++)).getOpcode() != Opcodes.PUTFIELD) ;
                            FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                            map.put("net.minecraft.client.Minecraft.fontRendererObj", fieldInsn.name);
                            map.put("net.minecraft.client.gui.FontRenderer", fieldInsn.desc.substring(1, fieldInsn.desc.length() - 1));
                            j = i;
                            MethodInsnNode methodInsn = null;
                            while (true) {
                                while ((insn = insns.get(++j)).getOpcode() != Opcodes.INVOKESPECIAL) ;
                                methodInsn = (MethodInsnNode) insn;
                                if (methodInsn.owner.equals(map.get("net.minecraft.client.gui.FontRenderer"))) break;
                            }
                            map.put("net.minecraft.client.gui.FontRenderer.FontRenderer@structure", methodInsn.desc);
                            map.put("net.minecraft.client.settings.GameSettings", map.get("net.minecraft.client.gui.FontRenderer.FontRenderer@structure").substring(2, map.get("net.minecraft.client.gui.FontRenderer.FontRenderer@structure").indexOf(';')));
                            map.put("net.minecraft.client.renderer.texture.TextureManager", map.get("net.minecraft.client.gui.FontRenderer.FontRenderer@structure").substring(6 + map.get("net.minecraft.client.settings.GameSettings").length() + map.get("net.minecraft.util.ResourceLocation").length(), map.get("net.minecraft.client.gui.FontRenderer.FontRenderer@structure").lastIndexOf(';')));
                            bFlag++;
                            continue L2;
                        } else if (cst.equals("crash-reports")) {
                            j = i;
                            while ((insn = insns.get(--j)).getOpcode() != Opcodes.GETFIELD) ;
                            FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                            if (fieldInsn.desc.equals("Ljava/io/File;")) {
                                map.put("net.minecraft.client.Minecraft.mcDataDir", fieldInsn.name);
                                bFlag++;
                                continue L2;
                            }
                        } else if (cst.equals("/")) {
                            MethodInsnNode methodInsn = (MethodInsnNode) insns.get(i + 1);
                            map.put("net.minecraft.client.gui.GuiChat", methodInsn.owner);
                            bFlag++;
                            continue;
                        } else if (cst.equals("gui")) {
                            j = i;
                            while ((insn = insns.get(++j)).getOpcode() != Opcodes.GETFIELD) ;
                            while ((insn = insns.get(++j)).getOpcode() != Opcodes.GETFIELD) ;
                            FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                            map.put("net.minecraft.client.gui.GuiIngame", fieldInsn.desc.substring(1, fieldInsn.desc.length() - 1));
                            bFlag++;
                            continue;
                        }
                    }
                }
            }
        }

        b = jarReader.getClassBytes(map.get("net.minecraft.client.renderer.texture.TextureManager").replace('/', '.'));
        if (b == null) {
            throw new IOException("TextureManager.class not found!");
        }

        reader = new ClassReader(b);
        node = new ClassNode();
        reader.accept(node, 0);

        L3:
        for (MethodNode method : node.methods) {
            if (method.desc.equals("(L" + map.get("net.minecraft.util.ResourceLocation") + ";)V")) {
                insns = method.instructions;
                for (i = 0; i < insns.size(); i++) {
                    insn = insns.get(i);
                    if (insn.getOpcode() == Opcodes.GETFIELD) {
                        FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                        if (fieldInsn.desc.equals("Ljava/util/Map;")) {
                            map.put("net.minecraft.client.renderer.texture.TextureManager.bindTexture", method.name);
                            break L3;
                        }
                    }
                }
            }
        }

        b = jarReader.getClassBytes(map.get("net.minecraft.client.gui.FontRenderer").replace('/', '.'));
        if (b == null) {
            throw new IOException("FontRenderer.class not found!");
        }

        reader = new ClassReader(b);
        node = new ClassNode();
        reader.accept(node, 0);

        bFlag = 0;
        L4:
        for (MethodNode method : node.methods) {
            if (bFlag == 63) break;
            if (method.desc.equals("(CZ)F")) {
                insns = method.instructions;
                if ((bFlag & 3) != 3) {
                    for (i = 0; i < insns.size(); i++) {
                        insn = insns.get(i);
                        if ((insn.getOpcode() == Opcodes.GETFIELD)) {
                            FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                            if (fieldInsn.owner.equals(map.get("net.minecraft.client.gui.FontRenderer"))) {
                                if (fieldInsn.desc.equals("[B") && (bFlag & 1) == 0) {
                                    map.put("net.minecraft.client.gui.FontRenderer.glyphWidth", fieldInsn.name);
                                    bFlag |= 1;
                                } else if (fieldInsn.desc.equals("F") && (((bFlag & 8) == 0) | (bFlag & 16) == 0)) {
                                    if ((bFlag & 8) == 0) {
                                        map.put("net.minecraft.client.gui.FontRenderer.posX", fieldInsn.name);
                                        bFlag |= 8;
                                    } else if ((bFlag & 16) == 0) {
                                        map.put("net.minecraft.client.gui.FontRenderer.posY", fieldInsn.name);
                                        bFlag |= 16;
                                    }
                                }
                            }
                        } else if (((insn.getOpcode() == Opcodes.INVOKESPECIAL) || insn.getOpcode() == Opcodes.INVOKEVIRTUAL) && ((bFlag & 3) == 1)) {
                            MethodInsnNode methodInsn = (MethodInsnNode) insn;
                            if (methodInsn.owner.equals(map.get("net.minecraft.client.gui.FontRenderer")) && methodInsn.desc.equals("(I)V")) {
                                map.put("net.minecraft.client.gui.FontRenderer.loadGlyphTexture", methodInsn.name);
                                bFlag |= 2;
                            }
                        }
                    }
                }
                if ((bFlag & 4) == 0) {
                    for (i = 0; i < insns.size(); i++) {
                        insn = insns.get(i);
                        if (insn.getOpcode() == Opcodes.INVOKESTATIC) {
                            MethodInsnNode methodInsn = (MethodInsnNode) insn;
                            if (methodInsn.name.startsWith("gl")) continue L4;
                        }
                    }
                    map.put("net.minecraft.client.gui.FontRenderer.renderCharAtPos", method.name);
                    bFlag |= 4;
                }
            } else if (method.desc.equals("(ICZ)F") && (bFlag & 4) == 0) {
                map.put("net.minecraft.client.gui.FontRenderer.renderCharAtPos", method.name);
                bFlag |= 4;
            } else if (method.desc.equals("(C)I") && (bFlag & 32) == 0) {
                insns = method.instructions;
                for (i = 0; i < insns.size(); i++) {
                    insn = insns.get(i);
                    switch (insn.getOpcode()) {
                        case Opcodes.SIPUSH:
                            IntInsnNode intInsn = (IntInsnNode) insn;
                            if (intInsn.operand == 167) {
                                map.put("net.minecraft.client.gui.FontRenderer.getCharWidth", method.name);
                                bFlag |= 32;
                            }
                            break;
                        case Opcodes.INVOKESPECIAL:
                            MethodInsnNode methodInsn = (MethodInsnNode) insn;
                            if (methodInsn.owner.equals(map.get("net.minecraft.client.gui.FontRenderer")) && methodInsn.name.equals("getCharWidthFloat") && methodInsn.desc.equals("(C)F")) {
                                map.put("net.minecraft.client.gui.FontRenderer.getCharWidth", method.name);
                                bFlag |= 32;
                            }
                            break;
                    }
                }
            }
        }

        b = jarReader.getClassBytes(map.get("net.minecraft.client.gui.GuiChat").replace('/', '.'));
        if (b == null) {
            throw new IOException("GuiChat.class not found!");
        }

        reader = new ClassReader(b);
        node = new ClassNode();
        reader.accept(node, 0);

        L5:
        for (MethodNode method : node.methods) {
            if (method.desc.equals("(IIF)V")) {
                map.put("net.minecraft.client.gui.GuiChat.drawScreen", method.name);
                break;
            }
        }

        b = jarReader.getClassBytes(map.get("net.minecraft.client.gui.GuiIngame").replace('/', '.'));
        if (b == null) {
            throw new IOException("GuiIngame.class not found!");
        }

        reader = new ClassReader(b);
        node = new ClassNode();
        reader.accept(node, 0);

        L5:
        for (MethodNode method : node.methods) {
            if (method.desc.equals("(F)V")) {
                insns = method.instructions;
                for (i = 0; i < insns.size(); i++) {
                    insn = insns.get(i);
                    if (insn.getType() == AbstractInsnNode.LDC_INSN) {
                        LdcInsnNode ldcInsn = (LdcInsnNode) insn;
                        if (ldcInsn.cst instanceof String) {
                            String s = (String) ldcInsn.cst;
                            if (!s.equals("chat")) continue;
                            j = i + 1;
                            while ((insn = insns.get(++j)).getOpcode() != Opcodes.INVOKEVIRTUAL) ;
                            MethodInsnNode methodInsn = (MethodInsnNode) insn;
                            map.put("net.minecraft.client.gui.GuiNewChat", methodInsn.owner);
                            break L5;
                        }
                    }
                }
                break;
            }
        }

        b = jarReader.getClassBytes(map.get("net.minecraft.client.gui.GuiNewChat").replace('/', '.'));
        if (b == null) {
            throw new IOException("GuiNewChat.class not found!");
        }

        reader = new ClassReader(b);
        node = new ClassNode();
        reader.accept(node, 0);

        L6:
        for (MethodNode method : node.methods) {
            if (method.desc.equals("()I")) {
                insns = method.instructions;
                for (i = 0; i < insns.size(); i++) {
                    insn = insns.get(i);
                    if (insn.getOpcode() == Opcodes.BIPUSH) {
                        IntInsnNode intInsn = (IntInsnNode) insn;
                        if (intInsn.operand == 9 && insns.get(i + 1).getOpcode() == Opcodes.IDIV) {
                            map.put("net.minecraft.client.gui.GuiNewChat.getLineCount", method.name);
                            break L6;
                        }
                    }
                }
            }
        }

        return (map.size() - defaultMapSize) >= 21;
    }
}
