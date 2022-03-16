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

import com.lion328.thaifixes.asm.ClassMapManager;
import com.lion328.thaifixes.asm.mapper.ClassMap;
import com.lion328.thaifixes.asm.util.InstructionFinder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ThaiFixesFontRendererPatcher extends SingleClassPatcher {

    private ClassMap classMap;

    public ThaiFixesFontRendererPatcher(ClassMap classMap) {
        this.classMap = classMap;
    }

    @Override
    public String getClassName() {
        return "com.lion328.thaifixes.rendering.ThaiFixesFontRenderer";
    }

    @Override
    public byte[] patch(byte[] original) throws Exception {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        String target = "com/lion328/thaifixes/rendering/FakeFontRenderer";
        String replace = "net/minecraft/client/gui/FontRenderer";

        n.superName = replace;

        n.methods.forEach(method -> {
            InstructionFinder.create()
                    .method().owner(target).whenMatch(node -> node.owner = replace)
                    .find(method.instructions);

            InstructionFinder.create()
                    .field().owner(target).whenMatch(node -> node.owner = replace)
                    .find(method.instructions);
        });

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        n.accept(w);
        byte[] stage0 = w.toByteArray();

        return new NameMapperPatcher(w.toByteArray(), ClassMapManager.getDefaultClassmap()).patch(stage0);
    }
}
