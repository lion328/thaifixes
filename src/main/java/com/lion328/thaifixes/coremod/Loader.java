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

package com.lion328.thaifixes.coremod;

import com.lion328.thaifixes.coremod.patcher.FontRendererPatcher;
import com.lion328.thaifixes.coremod.patcher.FontRendererWrapperPatcher;
import com.lion328.thaifixes.coremod.patcher.IClassPatcher;
import com.lion328.thaifixes.coremod.patcher.MinecraftPatcher;
import com.typesafe.config.Config;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Loader implements IFMLLoadingPlugin, IClassTransformer {

    private static final Map<String, IClassPatcher> patchers = new HashMap<String, IClassPatcher>();

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {getClass().getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public byte[] transform(String s, String s1, byte[] bytes) {
        if(!s.equals(s1))
            Configuration.LOGGER.debug("Test 1: " + s + ", " + s1);
        if(patchers.containsKey(s)) {
            Configuration.LOGGER.info("Patching " + s);
            return patchers.get(s).patch(bytes);
        }
        return bytes;
    }

    public static void addPatcher(IClassPatcher patcher) {
        patchers.put(patcher.getClassName(), patcher);
    }

    static {
        addPatcher(new FontRendererPatcher());
        addPatcher(new FontRendererWrapperPatcher());
        addPatcher(new MinecraftPatcher());
    }
}
