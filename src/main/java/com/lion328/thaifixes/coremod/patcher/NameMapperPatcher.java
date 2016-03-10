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

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NameMapperPatcher implements IClassPatcher {

    private String className;
    private byte[] originalBytecode;
    private IClassMap classMap;

    public NameMapperPatcher(String className, InputStream classBytecode, IClassMap classMap) throws IOException {
        this(className, new byte[0], classMap);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ((b = classBytecode.read()) != -1)
            out.write(b);
        originalBytecode = out.toByteArray();
    }

    public NameMapperPatcher(String className, byte[] classBytecode, IClassMap classMap) {
        this.className = className;
        originalBytecode = classBytecode.clone();
        this.classMap = classMap;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public byte[] patch(byte[] original) throws Exception {
         ClassReader reader = new ClassReader(originalBytecode);
         ClassNode classNode = new ClassNode();
         ClassVisitor mapper = new RemappingClassAdapter(classNode, new NameRemapper(classMap));
         mapper = new NameMapperVisitor(Opcodes.ASM5, mapper, classMap);
         reader.accept(mapper, ClassReader.EXPAND_FRAMES);
         ClassWriter w = new ClassWriter(0);
         classNode.accept(w);
         return w.toByteArray();
    }

    public static class NameRemapper extends Remapper {

        private final IClassMap classMap;

        public NameRemapper(IClassMap classMap) {
            this.classMap = classMap;
        }

        @Override
        public String mapMethodName(String owner, String name, String desc) {
            if(classMap.getClass(owner) != null)
                return classMap.getClass(owner).getMethod(name, desc);
            return name;
        }

        @Override
        public String mapFieldName(String owner, String name, String desc) {
            if(classMap.getClass(owner) != null)
                return classMap.getClass(owner).getField(name);
            return name;
        }

        @Override
        public String map(String typeName) {
            if(classMap.getClass(typeName) != null)
                return classMap.getClass(typeName).getObfuscatedName();
            return typeName;
        }
    }

    public static class NameMapperVisitor extends ClassVisitor {

        private IClassMap classMap;

        private String currentSuperclassName;

        public NameMapperVisitor(int api, ClassVisitor cv, IClassMap classMap) {
            super(api, cv);
            this.classMap = classMap;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            currentSuperclassName = superName;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (currentSuperclassName != null)
                if(classMap.getClass(currentSuperclassName) != null)
                    name = classMap.getClass(currentSuperclassName).getMethod(name, desc);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
