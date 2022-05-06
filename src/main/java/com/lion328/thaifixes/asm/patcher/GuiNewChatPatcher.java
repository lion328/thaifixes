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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.function.Supplier;

public class GuiNewChatPatcher extends SingleClassPatcher {

    private ClassMap classMap;

    public GuiNewChatPatcher(ClassMap classMap) {
        this.classMap = classMap;
    }

    @Override
    public String getClassName() {
        return classMap.getClass("net.minecraft.client.gui.GuiNewChat").getObfuscatedName();
    }

    @Override
    public boolean tryPatch(ClassNode n) throws Exception {
        Supplier<MethodInsnNode> getFontHeightNode =
                () -> PatcherUtil.invokeInjectedConstantsMethodInt("getFontHeight");

        n.methods.forEach(mn -> {
            InsnList list = mn.instructions;
            InstructionFinder<Void> finder = InstructionFinder.create().withClassMap(classMap);

            // Replace font height.
            finder
                    .var(Opcodes.ALOAD).number(0).whenMatch(list::remove)
                    .field(Opcodes.GETFIELD).whenMatch(list::remove)
                    .field(Opcodes.GETFIELD).whenMatch(list::remove)
                    .field(Opcodes.GETFIELD).owner("net/minecraft/client/gui/FontRenderer").desc("I")
                    .whenMatch(node -> list.insert(node, getFontHeightNode.get()))
                    .whenMatch(list::remove)
                    .find(list);

            // Replace chat line height.
            finder
                    .integer(Opcodes.BIPUSH)
                    .value(9)
                    .whenMatch(node -> list.insert(node, getFontHeightNode.get()))
                    .whenMatch(list::remove)
                    .find(list);

            // Replace text vertical offset.
            finder
                    .integer(Opcodes.BIPUSH)
                    .value(8)
                    .whenMatch(node -> list.insert(node,
                            PatcherUtil.invokeInjectedConstantsMethodInt("getChatLineTextYOffset")))
                    .whenMatch(list::remove)
                    .find(list);
        });

        return true;
    }
}
