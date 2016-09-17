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

package com.lion328.thaifixes;

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Mod(name = Constant.NAME, modid = Constant.MODID, version = Constant.VERSION, acceptedMinecraftVersions = Constant.MCVERSION)
public class Core
{

    public static final Logger LOGGER = Configuration.LOGGER;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        try
        {
            com.lion328.thaifixes.coremod.Configuration.generateClassmap();
            IClassMap map = com.lion328.thaifixes.coremod.Configuration.getDefaultClassmap();
            Class<?> mcClass = Class.forName(map.getClass("net/minecraft/client/Minecraft").getObfuscatedName().replace('/', '.'));
            Method getMc = mcClass.getMethod(map.getClass("net/minecraft/client/Minecraft").getMethod("getMinecraft", "()Lnet/minecraft/client/Minecraft;"));
            Object mc = getMc.invoke(null);
            Field fontRendererObjField = mcClass.getDeclaredField(map.getClass("net/minecraft/client/Minecraft").getField("fontRendererObj"));
            fontRendererObjField.setAccessible(true);
            Object fontRenderer = fontRendererObjField.get(mc);
            if (fontRenderer instanceof FontRendererWrapper)
            {
                Field patchedField = FontRendererWrapper.class.getDeclaredField("PATCHED");
                if (!patchedField.getBoolean(null))
                {
                    LOGGER.error("Unpatched FontRendererWrapper, converting to default");
                    Class<?> fontRendererClass = Class.forName(map.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName().replace('/', '.'));
                    Field[] fields = fontRendererClass.getDeclaredFields();
                    Constructor<?> constructor = fontRendererClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Object newFontRenderer = constructor.newInstance();
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    for (Field field : fields)
                    {
                        field.setAccessible(true);
                        modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
                        field.set(newFontRenderer, field.get(fontRenderer));
                    }
                    fontRendererObjField.set(mc, newFontRenderer);
                }
                else
                {
                    LOGGER.info("FontRendererWrapper is successfully patched");
                    Configuration.loadConfig(new File(FontRendererWrapper.getMinecraftDirectory(), "config/thaifixes.cfg"));
                    String rendererClass = Configuration.config.getProperty("font.rendererclass", "disable");
                    if (!rendererClass.equalsIgnoreCase("disable"))
                    {
                        Class<?> customRendererClass = Class.forName(rendererClass);
                        IFontRenderer customRenderer = (IFontRenderer) customRendererClass.newInstance();
                        ((FontRendererWrapper) fontRenderer).addRenderer(customRenderer);
                        LOGGER.info("Added " + rendererClass + " as font renderer");
                    }
                }
            }
            else
            {
                LOGGER.error("Current global FontRenderer object is not FontRendererWrapper (maybe another mod changed)");
            }
        }
        catch (Exception e)
        {
            LOGGER.catching(e);
        }
    }
}