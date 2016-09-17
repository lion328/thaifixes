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

package com.lion328.thaifixes.coremod.mapper;

import java.util.HashMap;
import java.util.Map;

public class SimpleClassDetail implements IClassDetail
{

    private Map<String, String> fields;
    private Map<String, Map<String, String>> method;
    private String name, obfuscatedName;
    private IClassDetail superClassMap;

    public SimpleClassDetail(String name, String obfuscatedName)
    {
        this(name, obfuscatedName, null);
    }

    public SimpleClassDetail(String name, String obfuscatedName, IClassDetail superClassMap)
    {
        fields = new HashMap<String, String>();
        method = new HashMap<String, Map<String, String>>();
        this.name = name;
        this.obfuscatedName = obfuscatedName;
        this.superClassMap = superClassMap;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getObfuscatedName()
    {
        return obfuscatedName;
    }

    @Override
    public String getField(String name)
    {
        if (!fields.containsKey(name))
        {
            if (superClassMap != null)
            {
                return superClassMap.getField(name);
            }
            return name;
        }
        return fields.get(name);
    }

    @Override
    public String getMethod(String name, String desc)
    {
        if (!method.containsKey(name))
        {
            if (superClassMap != null)
            {
                return superClassMap.getMethod(name, desc);
            }
            return name;
        }
        Map<String, String> map = method.get(name);
        if (!map.containsKey(desc))
        {
            return name;
        }
        return map.get(desc);
    }

    @Override
    public void addField(String name, String obfuscatedName)
    {
        fields.put(name, obfuscatedName);
    }

    @Override
    public void addMethod(String name, String obfuscatedName, String desc)
    {
        Map<String, String> map = method.get(name);
        if (!method.containsKey(name))
        {
            method.put(name, map = new HashMap<String, String>());
        }
        map.put(desc, obfuscatedName);
    }

    @Override
    public IClassDetail getSuperclassMap()
    {
        return superClassMap;
    }
}
