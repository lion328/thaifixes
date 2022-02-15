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
import com.lion328.thaifixes.coremod.mapper.V1_6_2ClassMapper;
import com.lion328.thaifixes.coremod.mapper.reader.IJarReader;
import com.lion328.thaifixes.coremod.mapper.reader.MinecraftClassLoaderJarReader;
import com.lion328.thaifixes.coremod.mapper.reader.TransformedJarReader;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class CoremodSettings {
    private static IClassMap obfuscatedClassmap, defaultClassmap;

    public static IClassMap getObfuscatedClassmap() {
        if (obfuscatedClassmap == null) {
            obfuscatedClassmap = generateClassmap(false);
        }

        return obfuscatedClassmap;
    }

    public static IClassMap getDefaultClassmap() {
        if (defaultClassmap == null) {
            defaultClassmap = generateClassmap();
        }

        return defaultClassmap;
    }

    private static IClassMap generateClassmap() {
        return generateClassmap(true);
    }

    private static IClassMap generateClassmap(boolean transfromedReading) {
        getLogger().info("Generating class map (transformedReading = " + transfromedReading + ")");

        IClassMap classMap = new SimpleClassMap();

        if (Thread.currentThread().getContextClassLoader() instanceof LaunchClassLoader) {
            LaunchClassLoader cl = (LaunchClassLoader) Thread.currentThread().getContextClassLoader();

            boolean deobfuscatedEnvironment;

            try {
                deobfuscatedEnvironment = cl.getClassBytes("net.minecraft.client.gui.FontRenderer") != null;
            } catch (IOException e) {
                deobfuscatedEnvironment = false;
            }

            MinecraftClassLoaderJarReader mcJarReader = new MinecraftClassLoaderJarReader(cl);
            IJarReader reader = mcJarReader;

            if (deobfuscatedEnvironment || !transfromedReading) {
                reader = mcJarReader;
            } else {
                DeobfuscationTransformer deobfTransformer = new DeobfuscationTransformer();
                reader = new TransformedJarReader(mcJarReader, deobfTransformer, deobfTransformer);
            }

            try {
                IClassMapper cm = new V1_6_2ClassMapper();

                if (!cm.getMap(reader, classMap)) {
                    getLogger().error("Runtime mapping is not working.");
                }
            } catch (Exception e) {
                getLogger().catching(e);
            }
        } else {
            getLogger().error("Can't run runtime mapping (Invalid classloader type)");
        }

        return classMap;
    }

    private static Logger getLogger() {
        return ThaiFixesCoremod.LOGGER;
    }
}
