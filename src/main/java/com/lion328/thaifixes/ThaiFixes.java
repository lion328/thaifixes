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

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(name = ModInformation.NAME, modid = ModInformation.MODID, version = ModInformation.VERSION, acceptedMinecraftVersions = ModInformation.MCVERSION)
public class ThaiFixes
{

    public static final Logger LOGGER = Settings.LOGGER;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        try
        {
            Minecraft mc = Minecraft.getMinecraft();

            if (mc.fontRendererObj instanceof FontRendererWrapper)
            {
                {
                    LOGGER.info("FontRendererWrapper is successfully patched");

                    Settings.loadConfig(new File(FontRendererWrapper.getMinecraftDirectory(), "config/thaifixes.cfg"));
                    String rendererClass = Settings.config.getProperty("font.rendererclass", "disable");

                    if (!rendererClass.equalsIgnoreCase("disable"))
                    {
                        Class<?> customRendererClass = Class.forName(rendererClass);
                        IFontRenderer customRenderer = (IFontRenderer) customRendererClass.newInstance();

                        ((FontRendererWrapper) mc.fontRendererObj).addRenderer(customRenderer);

                        LOGGER.info("Added " + rendererClass + " as font renderer");
                    }
                    else
                    {
                        LOGGER.info("ThaiFixes is disabled");
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