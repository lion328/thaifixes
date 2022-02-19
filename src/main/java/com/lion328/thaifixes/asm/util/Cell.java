package com.lion328.thaifixes.asm.util;

public class Cell<T> {

    private T value;

    public T get() {
        return value;
    }

    public void set(T newValue) {
        value = newValue;
    }
}
