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

package com.lion328.thaifixes.main;

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.coremod.mapper.IClassMapper;
import com.lion328.thaifixes.coremod.mapper.SimpleClassMap;
import com.lion328.thaifixes.coremod.mapper.reader.FileJarReader;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class Main
{

    public static void main(String[] args) throws IOException
    {
        if (args.length == 3 && args[0].equalsIgnoreCase("generate-classmap"))
        {
            Class<?> c = null;
            try
            {
                c = Class.forName(args[1]);
            }
            catch (ClassNotFoundException e)
            {
                System.out.println("Class not found");
                return;
            }
            Object o = null;
            try
            {
                o = c.newInstance();
            }
            catch (Exception e)
            {
                System.out.println("Can't create class instance");
                return;
            }
            if (!(o instanceof IClassMapper))
            {
                System.out.println("Invalid IClassMapper");
                return;
            }
            IClassMap map = new SimpleClassMap();
            System.out.println("Valid map? : " + (((IClassMapper) o).getMap(new FileJarReader(new JarFile(new File(args[2]))), map) ? "true" : "false"));
        }
    }
}
