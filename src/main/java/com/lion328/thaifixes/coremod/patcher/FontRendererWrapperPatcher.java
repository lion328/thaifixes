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
import org.objectweb.asm.*;

import java.util.Map;

public class FontRendererWrapperPatcher implements IClassPatcher, Opcodes {

    @Override
    public String getClassName() {
        return "com.lion328.thaifixes.FontRendererWrapper";
    }

    @Override
    public byte[] patch(byte[] original) {
        try {
            return generate(Configuration.getDefaultClassmap());
        } catch (Exception e) {
            Configuration.LOGGER.catching(e);
        }
        return original;
    }

    public static byte[] generate(Map<String, String> map) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "com/lion328/thaifixes/FontRendererWrapper", null, map.get("net.minecraft.client.gui.FontRenderer"), null);

        {
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, "PATCHED", "Z", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "resourceLocationPool", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;L" + map.get("net.minecraft.util.ResourceLocation") + ";>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "renderers", "Ljava/util/List;", "Ljava/util/List<Lcom/lion328/thaifixes/IFontRenderer;>;", null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "renderEngine", "L" + map.get("net.minecraft.client.renderer.texture.TextureManager") + ";", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "lastChar", "C", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            mv.visitInsn(ICONST_1);
            mv.visitFieldInsn(PUTSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "PATCHED", "Z");
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "resourceLocationPool", "Ljava/util/Map;");
            mv.visitTypeInsn(NEW, "java/util/ArrayList");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "renderers", "Ljava/util/List;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "getMinecraftDirectory", "()Ljava/io/File;", null, null);
            mv.visitCode();
            mv.visitMethodInsn(INVOKESTATIC, map.get("net.minecraft.client.Minecraft"), map.get("net.minecraft.client.Minecraft.getMinecraft"), "()L" + map.get("net.minecraft.client.Minecraft") + ";", false);
            mv.visitFieldInsn(GETFIELD, map.get("net.minecraft.client.Minecraft"), map.get("net.minecraft.client.Minecraft.mcDataDir"), "Ljava/io/File;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", map.get("net.minecraft.client.gui.FontRenderer.FontRenderer@structure"), null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESPECIAL, map.get("net.minecraft.client.gui.FontRenderer"), "<init>", map.get("net.minecraft.client.gui.FontRenderer.FontRenderer@structure"), false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitFieldInsn(PUTFIELD, "com/lion328/thaifixes/FontRendererWrapper", "renderEngine", "L" + map.get("net.minecraft.client.renderer.texture.TextureManager") + ";");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "addRenderer", "(Lcom/lion328/thaifixes/IFontRenderer;)V", null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "renderers", "Ljava/util/List;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "contains", "(Ljava/lang/Object;)Z", true);
            Label l0 = new Label();
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitInsn(RETURN);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "setFontRendererWrapper", "(Lcom/lion328/thaifixes/FontRendererWrapper;)V", true);
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "renderers", "Ljava/util/List;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "loadUnicodeTexture", "(I)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/lion328/thaifixes/FontRendererWrapper", map.get("net.minecraft.client.gui.FontRenderer.loadGlyphTexture"), "(I)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getRawUnicodeWidth", "(C)B", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/lion328/thaifixes/FontRendererWrapper", map.get("net.minecraft.client.gui.FontRenderer.glyphWidth"), "[B");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(BALOAD);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "bindTexture", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "resourceLocationPool", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "containsKey", "(Ljava/lang/Object;)Z", true);
            Label l0 = new Label();
            mv.visitJumpInsn(IFNE, l0);
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "resourceLocationPool", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(NEW, map.get("net.minecraft.util.ResourceLocation"));
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, map.get("net.minecraft.util.ResourceLocation"), "<init>", "(Ljava/lang/String;)V", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/lion328/thaifixes/FontRendererWrapper", "renderEngine", "L" + map.get("net.minecraft.client.renderer.texture.TextureManager") + ";");
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "resourceLocationPool", "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, map.get("net.minecraft.util.ResourceLocation"));
            mv.visitMethodInsn(INVOKEVIRTUAL, map.get("net.minecraft.client.renderer.texture.TextureManager"), map.get("net.minecraft.client.renderer.texture.TextureManager.bindTexture"), "(L" + map.get("net.minecraft.util.ResourceLocation") + ";)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(5, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getDefaultCharacterWidth", "(C)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, map.get("net.minecraft.client.gui.FontRenderer"), map.get("net.minecraft.client.gui.FontRenderer.getCharWidth"), "(C)I", false);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getX", "()F", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/lion328/thaifixes/FontRendererWrapper", map.get("net.minecraft.client.gui.FontRenderer.posX"), "F");
            mv.visitInsn(FRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getY", "()F", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/lion328/thaifixes/FontRendererWrapper", map.get("net.minecraft.client.gui.FontRenderer.posY"), "F");
            mv.visitInsn(FRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, map.get("net.minecraft.client.gui.FontRenderer.renderCharAtPos"), "(ICZ)F", null, null);
            mv.visitCode();
            mv.visitLdcInsn(new Float("NaN"));
            mv.visitVarInsn(FSTORE, 4);
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "renderers", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            mv.visitVarInsn(ASTORE, 6);
            Label l0 = new Label();
            mv.visitJumpInsn(GOTO, l0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_FULL, 7, new Object[]{"com/lion328/thaifixes/FontRendererWrapper", Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.FLOAT, Opcodes.TOP, "java/util/Iterator"}, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 6);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "com/lion328/thaifixes/IFontRenderer");
            mv.visitVarInsn(ASTORE, 5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "isSupportedCharacter", "(C)Z", true);
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "renderCharacter", "(CZ)F", true);
            mv.visitVarInsn(FSTORE, 4);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            mv.visitJumpInsn(IFNE, l1);
            mv.visitVarInsn(FLOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "isNaN", "(F)Z", false);
            Label l2 = new Label();
            mv.visitJumpInsn(IFEQ, l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, map.get("net.minecraft.client.gui.FontRenderer"), map.get("net.minecraft.client.gui.FontRenderer.renderCharAtPos"), "(ICZ)F", false);
            mv.visitVarInsn(FSTORE, 4);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_FULL, 5, new Object[]{"com/lion328/thaifixes/FontRendererWrapper", Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.FLOAT}, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitFieldInsn(PUTFIELD, "com/lion328/thaifixes/FontRendererWrapper", "lastChar", "C");
            mv.visitVarInsn(FLOAD, 4);
            mv.visitInsn(FRETURN);
            mv.visitMaxs(4, 7);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, map.get("net.minecraft.client.gui.FontRenderer.renderCharAtPos"), "(CZ)F", null, null);
            mv.visitCode();
            mv.visitLdcInsn(new Float("NaN"));
            mv.visitVarInsn(FSTORE, 3);
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "renderers", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            mv.visitVarInsn(ASTORE, 5);
            Label l0 = new Label();
            mv.visitJumpInsn(GOTO, l0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_FULL, 6, new Object[]{"com/lion328/thaifixes/FontRendererWrapper", Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.FLOAT, Opcodes.TOP, "java/util/Iterator"}, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "com/lion328/thaifixes/IFontRenderer");
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "isSupportedCharacter", "(C)Z", true);
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "renderCharacter", "(CZ)F", true);
            mv.visitVarInsn(FSTORE, 3);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            mv.visitJumpInsn(IFNE, l1);
            mv.visitVarInsn(FLOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "isNaN", "(F)Z", false);
            Label l2 = new Label();
            mv.visitJumpInsn(IFEQ, l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, map.get("net.minecraft.client.gui.FontRenderer"), map.get("net.minecraft.client.gui.FontRenderer.renderCharAtPos"), "(CZ)F", false);
            mv.visitVarInsn(FSTORE, 3);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_FULL, 4, new Object[]{"com/lion328/thaifixes/FontRendererWrapper", Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.FLOAT}, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "com/lion328/thaifixes/FontRendererWrapper", "lastChar", "C");
            mv.visitVarInsn(FLOAD, 3);
            mv.visitInsn(FRETURN);
            mv.visitMaxs(4, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, map.get("net.minecraft.client.gui.FontRenderer.getCharWidth"), "(C)I", null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "renderers", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            mv.visitVarInsn(ASTORE, 3);
            Label l0 = new Label();
            mv.visitJumpInsn(GOTO, l0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_FULL, 4, new Object[]{
                    "com/lion328/thaifixes/FontRendererWrapper", Opcodes.INTEGER, Opcodes.TOP, "java/util/Iterator"
            }, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "com/lion328/thaifixes/IFontRenderer");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "isSupportedCharacter", "(C)Z", true);
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "getCharacterWidth", "(C)I", true);
            mv.visitInsn(IRETURN);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            mv.visitJumpInsn(IFNE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, map.get("net.minecraft.client.gui.FontRenderer"), map.get("net.minecraft.client.gui.FontRenderer.getCharWidth"), "(C)I", false);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getCharWidthFloat", "(C)F", null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "com/lion328/thaifixes/FontRendererWrapper", "renderers", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            mv.visitVarInsn(ASTORE, 3);
            Label l0 = new Label();
            mv.visitJumpInsn(GOTO, l0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_FULL, 4, new Object[]{
                    "com/lion328/thaifixes/FontRendererWrapper", Opcodes.INTEGER, Opcodes.TOP, "java/util/Iterator"
            }, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "com/lion328/thaifixes/IFontRenderer");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "isSupportedCharacter", "(C)Z", true);
            mv.visitJumpInsn(IFEQ, l0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/lion328/thaifixes/IFontRenderer", "getCharacterWidth", "(C)I", true);
            mv.visitInsn(I2F);
            mv.visitInsn(FRETURN);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            mv.visitJumpInsn(IFNE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, map.get("net.minecraft.client.gui.FontRenderer"), "getCharWidthFloat", "(C)F", false);
            mv.visitInsn(FRETURN);
            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getLastCharacterRenderered", "()C", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/lion328/thaifixes/FontRendererWrapper", "lastChar", "C");
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
