/*
 * Copyright (c) 2022 Waritnan Sookbuntherng
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

public final class IdentityClassMap implements ClassMap {
    public static final IdentityClassMap INSTANCE = new IdentityClassMap();

    @Override
    public ClassDetail getClassFromInternalName(String internalName) {
        return new IdentityClassDetail(internalName);
    }

    @Override
    public ClassDetail addClass(ClassDetail classDetail) {
        throw new UnsupportedOperationException();
    }

    private static class IdentityClassDetail implements ClassDetail {
        private final String className;

        public IdentityClassDetail(String className) {
            this.className = className;
        }

        @Override
        public String getName() {
            return className;
        }

        @Override
        public String getObfuscatedInternalName() {
            return className;
        }

        @Override
        public String getField(String name) {
            return name;
        }

        @Override
        public String getMethod(String name, String desc) {
            return name;
        }

        @Override
        public void addField(String name, String obfuscatedName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addMethod(String name, String obfuscatedName, String desc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ClassDetail getSuperclassMap() {
            return null;
        }
    }
}
