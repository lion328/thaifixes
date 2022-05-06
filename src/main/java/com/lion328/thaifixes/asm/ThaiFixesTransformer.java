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

package com.lion328.thaifixes.asm;

import com.lion328.thaifixes.asm.mapper.ClassMap;
import com.lion328.thaifixes.asm.patcher.ClassPatcher;
import com.lion328.thaifixes.asm.patcher.FontRendererPatcher;
import com.lion328.thaifixes.asm.patcher.GuiChatPatcher;
import com.lion328.thaifixes.asm.patcher.GuiNewChatPatcher;
import com.lion328.thaifixes.asm.patcher.MinecraftPatcher;
import com.lion328.thaifixes.coremod.ThaiFixesCoremod;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

public class ThaiFixesTransformer implements IClassTransformer {
    private List<ClassPatcher> patchers = new ArrayList<>();

    public ThaiFixesTransformer() {
        initializePatchers();
    }

    private void initializePatchers() {
        ClassMap classMap = ClassMapManager.getObfuscatedClassmap();

        try {
            patchers.add(new MinecraftPatcher(classMap));
            patchers.add(new FontRendererPatcher(classMap));
            patchers.add(new GuiNewChatPatcher(classMap));
            patchers.add(new GuiChatPatcher(classMap));
        } catch (Exception e) {
            ThaiFixesCoremod.LOGGER.catching(e);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        List<ClassPatcher> supportedPatchers = new ArrayList<>();
        for (ClassPatcher patcher : patchers) {
            if (patcher.isSupported(name))
                supportedPatchers.add(patcher);
        }

        if (supportedPatchers.isEmpty())
            return original;

        ClassReader classReader = new ClassReader(original);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        for (ClassPatcher patcher : supportedPatchers) {
            try {
                if (patcher.tryPatch(classNode))
                    ThaiFixesCoremod.LOGGER.info("{} is patched by {}", transformedName,
                            patcher.getClass().getSimpleName());
            } catch (Exception e) {
                ThaiFixesCoremod.LOGGER.error("Error during patching", e);
                ThaiFixesCoremod.LOGGER.info("Rolling back {} to the original state", transformedName);
                return original;
            }
        }

        ClassWriter classWriter = new ClassWriter(classReader,
                ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }
}
