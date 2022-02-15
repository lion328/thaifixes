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

package com.lion328.thaifixes.coremod;

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.coremod.patcher.FontRendererPatcher;
import com.lion328.thaifixes.coremod.patcher.FontRendererWrapperPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiChatPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiNewChatPatcher;
import com.lion328.thaifixes.coremod.patcher.IClassPatcher;
import com.lion328.thaifixes.coremod.patcher.MinecraftPatcher;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

public class ThaiFixesTransformer implements IClassTransformer {
    private List<IClassPatcher> patchers = new ArrayList<>();

    public ThaiFixesTransformer() {
        initializePatchers();
    }

    private void initializePatchers() {
        IClassMap classMap = ClassMapManager.getObfuscatedClassmap();

        try {
            patchers.add(new MinecraftPatcher(classMap));
            patchers.add(new FontRendererPatcher(classMap));
            patchers.add(new GuiNewChatPatcher(classMap));
            patchers.add(new GuiChatPatcher(classMap));
            patchers.add(new FontRendererWrapperPatcher(classMap));
        } catch (Exception e) {
            ThaiFixesCoremod.LOGGER.catching(e);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        byte[] result = original;

        for (IClassPatcher patcher : patchers) {
            if (patcher.isSupported(name)) {
                ThaiFixesCoremod.LOGGER.info("Patching {} by {}", transformedName, patcher.getClass().getName());

                try {
                    result = patcher.patch(result);
                } catch (Exception e) {
                    ThaiFixesCoremod.LOGGER.catching(e);
                }
            }
        }

        return result;
    }
}
