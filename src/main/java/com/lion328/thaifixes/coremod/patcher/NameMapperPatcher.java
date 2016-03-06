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
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class NameMapperPatcher implements IClassPatcher {

    private String className;
    private byte[] originalBytecode;

    public NameMapperPatcher(String className, InputStream classBytecode) throws IOException {
        this(className, new byte[0]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ((b = classBytecode.read()) != -1)
            out.write(b);
        originalBytecode = out.toByteArray();
    }

    public NameMapperPatcher(String className, byte[] classBytecode) {
        this.className = className;
        originalBytecode = classBytecode.clone();
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public byte[] patch(byte[] original) {
        try {
            ClassReader reader = new ClassReader(originalBytecode);
            ClassNode classNode = new ClassNode();
            ClassVisitor mapper = new RemappingClassAdapter(classNode, new NameRemapper(Configuration.getDefaultClassmap()));
            mapper = new NameMapperVisitor(Opcodes.ASM5, mapper, Configuration.getDefaultClassmap());
            reader.accept(mapper, ClassReader.EXPAND_FRAMES);
            ClassWriter w = new ClassWriter(0);
            classNode.accept(w);
            return w.toByteArray();
        } catch (Exception e) {
            Configuration.LOGGER.catching(e);
        }
        return original;
    }

    public static class NameRemapper extends Remapper {

        private final Map<String, String> classMap;

        public NameRemapper(Map<String, String> classMap) {
            this.classMap = classMap;
        }

        private String mapMemberName(String owner, String name, String desc) {
            String ret = map((owner + '.' + name).replace('/', '.') + ':' + desc);
            if (ret == null)
                return name;
            return ret;
        }

        @Override
        public String mapMethodName(String owner, String name, String desc) {
            return mapMemberName(owner, name, desc);
        }

        @Override
        public String mapFieldName(String owner, String name, String desc) {
            return mapMemberName(owner, name, desc);
        }

        @Override
        public String map(String typeName) {
            return classMap.get(typeName.replace('/', '.'));
        }
    }

    public static class NameMapperVisitor extends ClassVisitor {

        private Map<String, String> classMap;

        private String currentSuperclassName;

        public NameMapperVisitor(int api, ClassVisitor cv, Map<String, String> classMap) {
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
            if (currentSuperclassName != null) {
                String key = (currentSuperclassName + '.' + name).replace('/', '.') + ':' + desc;
                if (classMap.containsKey(key))
                    name = classMap.get(key);
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
