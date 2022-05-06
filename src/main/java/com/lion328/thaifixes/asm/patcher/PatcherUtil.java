/*
 * Copyright (c) 2022 Waritnan Sookbuntherng
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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.function.Consumer;

class PatcherUtil {
    public static void addPrivateField(ClassVisitor visitor, String name, String desc) {
        visitor.visitField(Opcodes.ACC_PRIVATE, name, desc, null, null).visitEnd();
    }

    public static void proxyMethodSelf(ClassVisitor visitor, String owner, String originalName, String newName,
                                       String desc) {
        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, newName, desc, null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);

        Type type = Type.getType(desc);
        Type[] argTypes = type.getArgumentTypes();
        for (int i = 0; i < argTypes.length; i++)
            mv.visitVarInsn(argTypes[i].getOpcode(Opcodes.ILOAD), i + 1);

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, originalName, desc, false);

        if (type.getReturnType() == Type.VOID_TYPE)
            mv.visitInsn(Opcodes.RETURN);
        else
            mv.visitInsn(type.getOpcode(Opcodes.IRETURN));

        mv.visitEnd();
    }

    public static void implGetterSelf(ClassVisitor visitor, String owner, String field, String desc,
                                      String methodName) {
        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, methodName, "()" + desc, null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, owner, field, desc);
        mv.visitInsn(Type.getType(desc).getOpcode(Opcodes.IRETURN));
        mv.visitEnd();
    }

    public static void implSetterSelf(ClassVisitor visitor, String owner, String field, String desc,
                                      String methodName) {
        String methodDesc = "(" + desc + ")V";

        MethodVisitor mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, methodName, methodDesc, null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Type.getType(desc).getOpcode(Opcodes.ILOAD), 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, owner, field, desc);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitEnd();
    }

    public static InsnList putFieldSelf(InsnList insns, String owner, String name, String desc,
                                        Consumer<InsnList> lambda) {
        insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        lambda.accept(insns);
        insns.add(new FieldInsnNode(Opcodes.PUTFIELD, owner, name, desc));
        return insns;
    }

    public static MethodInsnNode invokeInjectedConstantsMethodInt(String methodName) {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/lion328/thaifixes/InjectedConstants", methodName,
                "()I", false);
    }
}
