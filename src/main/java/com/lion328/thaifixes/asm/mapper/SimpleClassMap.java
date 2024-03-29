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

package com.lion328.thaifixes.asm.mapper;

import java.util.HashMap;
import java.util.Map;

public class SimpleClassMap implements ClassMap {

    private final Map<String, ClassDetail> classesDetail;

    public SimpleClassMap() {
        classesDetail = new HashMap<>();
    }

    @Override
    public ClassDetail getClassFromInternalName(String internalName) {
        return classesDetail.getOrDefault(internalName, IdentityClassMap.INSTANCE.getClassFromInternalName(internalName));
    }

    @Override
    public ClassDetail addClass(ClassDetail classDetail) {
        if (classesDetail.containsKey(classDetail.getName())) {
            return classesDetail.get(classDetail.getName());
        }
        classesDetail.put(classDetail.getName(), classDetail);
        return classDetail;
    }
}
