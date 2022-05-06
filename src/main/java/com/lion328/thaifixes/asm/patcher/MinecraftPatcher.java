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

import com.lion328.thaifixes.asm.mapper.ClassMap;
import com.lion328.thaifixes.asm.util.InstructionFinder;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class MinecraftPatcher extends SingleClassPatcher {

    private ClassMap classMap;

    public MinecraftPatcher(ClassMap classMap) {
        this.classMap = classMap;
    }

    @Override
    public String getClassName() {
        return classMap.getClass("net/minecraft/client/Minecraft").getObfuscatedName().replace('/', '.');
    }

    @Override
    public boolean tryPatch(ClassNode n) throws Exception {
        String orig = "net/minecraft/client/resources/LanguageManager";
        String ours = "com/lion328/thaifixes/ThaiFixesLanguageManager";

        InstructionFinder<?> finder = InstructionFinder.create()
                .withClassMap(classMap)
                .type(Opcodes.NEW).desc(orig).whenMatch(insn -> insn.desc = ours)
                .skip(6)
                .method(Opcodes.INVOKESPECIAL).owner(orig).name("<init>").whenMatch(insn -> insn.owner = ours);

        for (MethodNode mn : n.methods)
            if (finder.findFirst(mn.instructions))
                break;

        return true;
    }
}
