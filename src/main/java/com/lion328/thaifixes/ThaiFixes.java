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

package com.lion328.thaifixes;

import com.lion328.thaifixes.asm.ClassMapManager;
import com.lion328.thaifixes.asm.mapper.IClassMap;
import com.lion328.thaifixes.config.ThaiFixesConfiguration;
import com.lion328.thaifixes.rendering.FakeFontRenderer;
import com.lion328.thaifixes.rendering.ThaiFixesFontRenderer;
import com.lion328.thaifixes.rendering.font.Font;
import com.lion328.thaifixes.rendering.font.FontStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
import java.lang.reflect.Modifier;

@Mod(name = ModInformation.NAME, modid = ModInformation.MODID, version = ModInformation.VERSION,
        acceptedMinecraftVersions = ModInformation.MCVERSION, guiFactory = "com.lion328.thaifixes.config.gui.ThaiFixesGuiFactory")
public class ThaiFixes {

    private static Logger logger;

    private FontRenderer fontRenderer;
    private ThaiFixesFontRenderer fontRendererWrapper;
    private Font currentRenderer;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ThaiFixesConfiguration.init(event.getSuggestedConfigurationFile());
        ThaiFixesConfiguration.syncConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        try {
            IClassMap map = ClassMapManager.getDefaultClassmap();

            String fontRendererObjFieldName = map.getClass("net/minecraft/client/Minecraft").getField("fontRendererObj");
            Field fontRendererObjField = Minecraft.class.getDeclaredField(fontRendererObjFieldName);
            fontRendererObjField.setAccessible(true);

            Object fontRenderer = Minecraft.getMinecraft().fontRenderer;

            if (!(fontRenderer instanceof ThaiFixesFontRenderer)) {
                getLogger().error("Current global FontRenderer object is not FontRendererWrapper (maybe another mod changed)");

                return;
            }

            if (!((ThaiFixesFontRenderer) fontRenderer).isSuperclassPatched()) {
                getLogger().error("FontRenderer is not patched!");
                return;
            }

            if (ThaiFixesFontRenderer.class.getSuperclass() == FakeFontRenderer.class) {
                getLogger().error("Unpatched FontRendererWrapper, converting to default");

                Field[] fields = FontRenderer.class.getDeclaredFields();

                Constructor<?> constructor = FontRenderer.class.getDeclaredConstructor();
                constructor.setAccessible(true);

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);

                Object newFontRenderer = constructor.newInstance();

                for (Field field : fields) {
                    field.setAccessible(true);
                    modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
                    field.set(newFontRenderer, field.get(fontRenderer));
                }

                Minecraft.getMinecraft().fontRenderer = (FontRenderer) newFontRenderer;

                return;
            }

            getLogger().info("FontRendererWrapper is successfully patched");

            fontRendererWrapper = (ThaiFixesFontRenderer) fontRenderer;
            this.fontRenderer = (FontRenderer) fontRenderer;

            reloadRenderer();
        } catch (Exception e) {
            getLogger().catching(e);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ModInformation.MODID)) {
            ThaiFixesConfiguration.syncConfig();
            reloadRenderer();
            fontRenderer.setUnicodeFlag(Minecraft.getMinecraft().isUnicode());
        }
    }

    public void reloadRenderer() {
        if (fontRendererWrapper == null) {
            return;
        }

        FontStyle fontStyle = ThaiFixesConfiguration.getFontStyle();

        currentRenderer = fontStyle.newInstance();
        fontRendererWrapper.setFont(currentRenderer);

        getLogger().info("Using " + currentRenderer.getClass().getName() + " as font renderer");
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = LogManager.getLogger("ThaiFixes");
        }

        return logger;
    }
}
