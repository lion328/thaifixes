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

import com.lion328.thaifixes.coremod.mapper.IClassMapper;
import com.lion328.thaifixes.coremod.mapper.reader.IJarReader;
import com.lion328.thaifixes.coremod.mapper.reader.MinecraftClassLoaderJarReader;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    public static final Logger LOGGER = LogManager.getFormatterLogger("ThaiFixes-Coremod");
    private static Map<String, String> defaultClassmap;

    public static Map<String, String> getDefaultClassmap() {
        return Collections.unmodifiableMap(defaultClassmap);
    }

    static {
        defaultClassmap = new HashMap<String, String>();
        if (Thread.currentThread().getContextClassLoader() instanceof LaunchClassLoader) {
            LaunchClassLoader cl = (LaunchClassLoader) Thread.currentThread().getContextClassLoader();
            IJarReader reader = new MinecraftClassLoaderJarReader(cl);
            BufferedReader br = new BufferedReader(new InputStreamReader(Configuration.class.getResourceAsStream("/assets/thaifixes/config/classmap/classlist")));
            String s;
            try {
                while ((s = br.readLine()) != null) {
                    if (s.length() == 0)
                        continue;
                    defaultClassmap.clear();
                    Class<?> clazz = Class.forName(s);
                    Object o = clazz.newInstance();
                    if (!(o instanceof IClassMapper)) {
                        LOGGER.error(s + " is invalid IClassMapper, skipped");
                        continue;
                    }
                    IClassMapper cm = (IClassMapper) o;
                    if (!cm.getMap(reader, defaultClassmap)) {
                        LOGGER.error(s + " can't complete mapping, skipped");
                        defaultClassmap.clear();
                        continue;
                    }
                    for (String k : defaultClassmap.keySet())
                        LOGGER.debug("Classmap: " + k + " = " + defaultClassmap.get(k));
                    break;
                }
            } catch (Exception e) {
                LOGGER.catching(e);
            }
        } else
            LOGGER.error("Can't run runtime mapping (Invalid classloader type)");
        if (defaultClassmap.size() == 0)
            LOGGER.error("Runtime mapping not working");
    }
}
