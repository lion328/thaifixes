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

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.coremod.mapper.IClassMapper;
import com.lion328.thaifixes.coremod.mapper.SimpleClassMap;
import com.lion328.thaifixes.coremod.mapper.reader.IJarReader;
import com.lion328.thaifixes.coremod.mapper.reader.MinecraftClassLoaderJarReader;
import com.lion328.thaifixes.coremod.mapper.reader.TransformedJarReader;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Configuration {

    public static final Logger LOGGER = LogManager.getFormatterLogger("ThaiFixes-Coremod");
    public static final String DEFAULT_ORIGINAL_CLASSES_PATH = "/assets/thaifixes/classes/";
    private static IClassMap defaultClassmap;

    public static IClassMap getDefaultClassmap() {
        return defaultClassmap;
    }

    public static void generateClassmap() {
        defaultClassmap = new SimpleClassMap();
        if (Thread.currentThread().getContextClassLoader() instanceof LaunchClassLoader) {
            LaunchClassLoader cl = (LaunchClassLoader) Thread.currentThread().getContextClassLoader();
            boolean deobfuscatedEnvironment;
            try {
                deobfuscatedEnvironment = cl.getClassBytes("net.minecraft.client.gui.FontRenderer") != null;
            } catch (IOException e) {
                deobfuscatedEnvironment = false;
            }
            DeobfuscationTransformer deobfTransformer = new DeobfuscationTransformer();
            MinecraftClassLoaderJarReader mcJarReader = new MinecraftClassLoaderJarReader(cl);
            IJarReader reader;
            if (deobfuscatedEnvironment)
                reader = mcJarReader;
            else
                reader = new TransformedJarReader(mcJarReader, deobfTransformer, deobfTransformer);
            BufferedReader br = new BufferedReader(new InputStreamReader(Configuration.class.getResourceAsStream("/assets/thaifixes/config/classmap/classlist")));
            String s;
            try {
                boolean valid = false;
                while ((s = br.readLine()) != null) {
                    if (s.length() == 0)
                        continue;
                    defaultClassmap = new SimpleClassMap();
                    Class<?> clazz = Class.forName(s);
                    Object o = clazz.newInstance();
                    if (!(o instanceof IClassMapper)) {
                        LOGGER.error(s + " is invalid IClassMapper, skipped");
                        continue;
                    }
                    IClassMapper cm = (IClassMapper) o;
                    if (!cm.getMap(reader, defaultClassmap)) {
                        LOGGER.error(s + " can't complete mapping, skipped");
                        defaultClassmap = new SimpleClassMap();
                        continue;
                    }
                    valid = true;
                    break;
                }
                if (!valid)
                    LOGGER.error("Runtime mapping not working");
            } catch (Exception e) {
                LOGGER.catching(e);
            }
        } else
            LOGGER.error("Can't run runtime mapping (Invalid classloader type)");
    }

    static {
        generateClassmap();
    }
}
