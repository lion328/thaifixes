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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings
{

    public static final Logger LOGGER = LogManager.getFormatterLogger("ThaiFixes");

    public static final int DEFAULT_FONT_HEIGHT = 9;
    public static final Properties config = new Properties();
    public static int fontHeight = DEFAULT_FONT_HEIGHT;

    public static void loadConfig(File configFile) throws IOException
    {
        if (!configFile.exists())
        {
            InputStream in = Settings.class.getResourceAsStream("/assets/thaifixes/config/config_default.cfg");
            FileOutputStream out = new FileOutputStream(configFile);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, count);
            }
            out.close();
        }
        FileInputStream in = new FileInputStream(configFile);
        config.load(in);
        in.close();
    }
}
