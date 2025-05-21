package de.fhdortmund.swt2.utils;

import lombok.Getter;

public class Calculator {
    private static @Getter int precision = 2;

    private Calculator() {

    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int subtract(int a, int b) {
        return a - b;
    }
} 