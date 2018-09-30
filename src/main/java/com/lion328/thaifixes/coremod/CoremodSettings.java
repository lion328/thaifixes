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

package com.lion328.thaifixes.coremod;

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.coremod.mapper.IClassMapper;
import com.lion328.thaifixes.coremod.mapper.SimpleClassMap;
import com.lion328.thaifixes.coremod.mapper.reader.IJarReader;
import com.lion328.thaifixes.coremod.mapper.reader.MinecraftClassLoaderJarReader;
import com.lion328.thaifixes.coremod.mapper.reader.TransformedJarReader;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLRelaunchLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CoremodSettings
{

    public static final Logger LOGGER = LogManager.getFormatterLogger("ThaiFixes-Coremod");
    public static final String DEFAULT_ORIGINAL_CLASSES_PATH = "/assets/thaifixes/classes/";
    public static final String BLACKBOARD_DEOBFENV_KEY = "fml.deobfuscatedEnvironment";

    private static IClassMap obfuscatedClassmap, defaultClassmap;

    static
    {
        initializeRemapper();
    }

    public static IClassMap getObfuscatedClassmap()
    {
        if (obfuscatedClassmap == null)
        {
            obfuscatedClassmap = generateClassmap(false);
        }

        return obfuscatedClassmap;
    }

    public static IClassMap getDefaultClassmap()
    {
        if (defaultClassmap == null)
        {
            defaultClassmap = generateClassmap();
        }

        return defaultClassmap;
    }

    private static IClassMap generateClassmap()
    {
        return generateClassmap(true);
    }

    private static IClassMap generateClassmap(boolean transfromedReading)
    {
        LOGGER.info("Generating class map (transformedReading = " + transfromedReading + ")");

        IClassMap classMap = new SimpleClassMap();

        if (Thread.currentThread().getContextClassLoader() instanceof LaunchClassLoader)
        {
            LaunchClassLoader cl = (LaunchClassLoader) Thread.currentThread().getContextClassLoader();

            boolean deobfuscatedEnvironment;

            try
            {
                deobfuscatedEnvironment = cl.getClassBytes("net.minecraft.client.gui.FontRenderer") != null;
            }
            catch (IOException e)
            {
                deobfuscatedEnvironment = false;
            }

            MinecraftClassLoaderJarReader mcJarReader = new MinecraftClassLoaderJarReader(cl);
            IJarReader reader;

            if (deobfuscatedEnvironment || !transfromedReading)
            {
                reader = mcJarReader;
            }
            else
            {
                // HACK! We will rewrite the mod in 1.13 anyway.
                boolean isKeyNull = Launch.blackboard.get(BLACKBOARD_DEOBFENV_KEY) == null;

                if (isKeyNull)
                {
                    Launch.blackboard.put(BLACKBOARD_DEOBFENV_KEY, false);
                }

                DeobfuscationTransformer deobfTransformer = new DeobfuscationTransformer();

                if (isKeyNull)
                {
                    Launch.blackboard.remove(BLACKBOARD_DEOBFENV_KEY);
                }

                reader = new TransformedJarReader(mcJarReader, deobfTransformer, deobfTransformer);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(CoremodSettings.class.getResourceAsStream("/assets/thaifixes/config/classmap/classlist")));
            String s;

            try
            {
                boolean valid = false;

                while ((s = br.readLine()) != null)
                {
                    if (s.length() == 0)
                    {
                        continue;
                    }

                    classMap = new SimpleClassMap();

                    Class<?> clazz = Class.forName(s);
                    Object o = clazz.newInstance();

                    if (!(o instanceof IClassMapper))
                    {
                        LOGGER.error(s + " is invalid IClassMapper, skipped");
                        continue;
                    }

                    IClassMapper cm = (IClassMapper) o;

                    if (!cm.getMap(reader, classMap))
                    {
                        LOGGER.error(s + " can't complete mapping, skipped");
                        classMap = new SimpleClassMap();
                        continue;
                    }

                    valid = true;
                    break;
                }

                if (!valid)
                {
                    LOGGER.error("Runtime mapping not working");
                }
            }
            catch (Exception e)
            {
                LOGGER.catching(e);
            }
        }
        else
        {
            LOGGER.error("Can't run runtime mapping (Invalid classloader type)");
        }

        return classMap;
    }

    private static void initializeRemapper()
    {
        try
        {
            Field mcDirField = FMLInjectionData.class.getDeclaredField("minecraftHome");
            mcDirField.setAccessible(true);

            File mcDir = (File) mcDirField.get(null);

            Method deobfuscationDataName = FMLInjectionData.class.getDeclaredMethod("debfuscationDataName");
            deobfuscationDataName.setAccessible(true);

            String deobfuscationFileName = (String) deobfuscationDataName.invoke(null);

            FMLDeobfuscatingRemapper.INSTANCE.setup(mcDir, (LaunchClassLoader) Thread.currentThread().getContextClassLoader(), deobfuscationFileName);
        }
        catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException e)
        {
            CoremodSettings.LOGGER.catching(e);
        }
    }
}
