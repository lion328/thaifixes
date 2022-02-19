package com.lion328.thaifixes.asm.mapper;

public final class IdentityClassMap implements IClassMap {
    public static final IdentityClassMap INSTANCE = new IdentityClassMap();

    @Override
    public IClassDetail getClass(String name) {
        return new IdentityClassDetail(name);
    }

    @Override
    public IClassDetail addClass(IClassDetail classDetail) {
        throw new UnsupportedOperationException();
    }

    private static class IdentityClassDetail implements IClassDetail {
        private final String className;

        public IdentityClassDetail(String className) {
            this.className = className;
        }

        @Override
        public String getName() {
            return className;
        }

        @Override
        public String getObfuscatedName() {
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
        public IClassDetail getSuperclassMap() {
            return null;
        }
    }
}
