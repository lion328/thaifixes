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

import com.lion328.thaifixes.ModInformation;
import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.coremod.patcher.FontRendererPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiChatPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiNewChatPatcher;
import com.lion328.thaifixes.coremod.patcher.IClassPatcher;
import com.lion328.thaifixes.coremod.patcher.MinecraftPatcher;
import com.lion328.thaifixes.coremod.patcher.NameMapperPatcher;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.HashMap;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ModInformation.MCVERSION)
public class CoremodLoader implements IFMLLoadingPlugin, IClassTransformer
{

    private static final Map<String, IClassPatcher> patchers = new HashMap<String, IClassPatcher>();

    static
    {
        initializePatchers();
    }

    public static void addPatcher(IClassPatcher patcher)
    {
        patchers.put(patcher.getClassName(), patcher);
    }

    private static void initializePatchers()
    {
        IClassMap classMap = CoremodSettings.getObfuscatedClassmap();

        try
        {
            addPatcher(new MinecraftPatcher(classMap));
            addPatcher(new FontRendererPatcher(classMap));
            addPatcher(new GuiNewChatPatcher(classMap));
            addPatcher(new GuiChatPatcher(classMap));

            addPatcher(new NameMapperPatcher("com.lion328.thaifixes.FontRendererWrapper", CoremodLoader.class.getResourceAsStream(CoremodSettings.DEFAULT_ORIGINAL_CLASSES_PATH + "com/lion328/thaifixes/FontRendererWrapper"), CoremodSettings.getDefaultClassmap()));
        }
        catch (Exception e)
        {
            CoremodSettings.LOGGER.catching(e);
        }
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {getClass().getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }

    @Override
    public byte[] transform(String untransformedName, String transformedName, byte[] bytes)
    {
        if (patchers.containsKey(untransformedName))
        {
            CoremodSettings.LOGGER.info("Patching " + transformedName);

            try
            {
                return patchers.get(untransformedName).patch(bytes);
            }
            catch (Exception e)
            {
                CoremodSettings.LOGGER.catching(e);
            }
        }

        return bytes;
    }
}
