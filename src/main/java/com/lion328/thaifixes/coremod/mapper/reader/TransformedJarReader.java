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

package com.lion328.thaifixes.coremod.mapper.reader;

import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

import java.io.IOException;

public class TransformedJarReader implements IJarReader
{

    private IJarReader parent;
    private IClassTransformer classTransformer;
    private IClassNameTransformer nameTransformer;

    public TransformedJarReader(IJarReader parent, IClassTransformer classTransformer, IClassNameTransformer nameTransformer)
    {
        this.parent = parent;
        this.classTransformer = classTransformer;
        this.nameTransformer = nameTransformer;
    }

    @Override
    public byte[] getClassBytes(String name) throws IOException
    {
        String untransformedName = name;
        if (nameTransformer != null)
        {
            untransformedName = nameTransformer.unmapClassName(name);
        }
        return classTransformer.transform(untransformedName, name, parent.getClassBytes(nameTransformer.unmapClassName(name)));
    }
}
