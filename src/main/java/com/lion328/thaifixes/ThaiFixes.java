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

import com.lion328.thaifixes.coremod.CoremodSettings;
import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.renderer.IFontRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Mod(name = ModInformation.NAME, modid = ModInformation.MODID, version = ModInformation.VERSION,
        acceptedMinecraftVersions = ModInformation.MCVERSION, guiFactory = "com.lion328.thaifixes.gui.ThaiFixesGuiFactory")
public class ThaiFixes
{

    private static Logger logger;

    private FontRendererWrapper fontRendererWrapper;
    private IFontRenderer currentRenderer;
    private boolean disabled;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Config.init(event.getSuggestedConfigurationFile());
        Config.syncConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);

        try
        {
            IClassMap map = CoremodSettings.getDefaultClassmap();

            Class<?> mcClass = Class.forName(map.getClass("net/minecraft/client/Minecraft").getObfuscatedName().replace('/', '.'));
            Method getMc = mcClass.getMethod(map.getClass("net/minecraft/client/Minecraft").getMethod("getMinecraft", "()Lnet/minecraft/client/Minecraft;"));

            Field fontRendererObjField = mcClass.getDeclaredField(map.getClass("net/minecraft/client/Minecraft").getField("fontRendererObj"));
            fontRendererObjField.setAccessible(true);

            Object mc = getMc.invoke(null);
            Object fontRenderer = fontRendererObjField.get(mc);

            if (!(fontRenderer instanceof FontRendererWrapper))
            {
                getLogger().error("Current global FontRenderer object is not FontRendererWrapper (maybe another mod changed)");

                return;
            }

            if (!FontRendererWrapper.class.getDeclaredField("PATCHED").getBoolean(null))
            {
                getLogger().error("Unpatched FontRendererWrapper, converting to default");

                Class<?> fontRendererClass = Class.forName(map.getClass("net/minecraft/client/gui/FontRenderer").getObfuscatedName().replace('/', '.'));
                Field[] fields = fontRendererClass.getDeclaredFields();

                Constructor<?> constructor = fontRendererClass.getDeclaredConstructor();
                constructor.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);

                Object newFontRenderer = constructor.newInstance();

                for (Field field : fields)
                {
                    field.setAccessible(true);
                    modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
                    field.set(newFontRenderer, field.get(fontRenderer));
                }

                fontRendererObjField.set(mc, newFontRenderer);

                return;
            }

            getLogger().info("FontRendererWrapper is successfully patched");

            fontRendererWrapper = (FontRendererWrapper) fontRenderer;

            reloadRenderer();
        }
        catch (Exception e)
        {
            getLogger().catching(e);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(ModInformation.MODID))
        {
            Config.syncConfig();

            try
            {
                reloadRenderer();
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                getLogger().catching(e);
            }
        }
    }

    public void reloadRenderer() throws InstantiationException, IllegalAccessException
    {
        if (fontRendererWrapper == null)
        {
            return;
        }

        FontStyle fontStyle = Config.getFontStyle();

        if (currentRenderer != null)
        {
            fontRendererWrapper.removeRenderer(currentRenderer);
        }

        if (!disabled && fontStyle != FontStyle.DISABLE)
        {
            currentRenderer = fontStyle.newInstance();
            fontRendererWrapper.addRenderer(currentRenderer);

            getLogger().info("Using " + fontStyle.getRendererClass().toString() + " as font renderer");
        }
        else
        {
            getLogger().info("ThaiFixes is disabled");
        }
    }

    public static Logger getLogger()
    {
        if (logger == null)
        {
            logger = LogManager.getLogger("ThaiFixes");
        }

        return logger;
    }
}