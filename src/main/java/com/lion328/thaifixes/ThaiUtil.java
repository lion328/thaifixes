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

public class ThaiUtil {

    public static final char THAI_CHAR_RANGE_MIN = 3585;
    public static final char THAI_CHAR_RANGE_MAX = 3675;
    public static final String LOWER_CHARS = "ฺุู";
    public static final String SPECIAL_UPPER_CHARS = "่้๊๋";
    public static final String UPPER_CHARS = "ัิีึื็์ํ๎" + SPECIAL_UPPER_CHARS;
    public static final String LONG_TAIL_CHARS = "ปฟฝฬ";
    public static final char SARA_UM = 'ำ';

    public static boolean isThaiChar(char c) {
        return c >= THAI_CHAR_RANGE_MIN && c <= THAI_CHAR_RANGE_MAX;
    }

    public static boolean isSpecialThaiChar(char c) {
        return isUpperThaiChar(c) || isLowerThaiChar(c);
    }

    public static boolean isUpperThaiChar(char c) {
        return UPPER_CHARS.indexOf(c) != -1;
    }

    public static boolean isLowerThaiChar(char c) {
        return LOWER_CHARS.indexOf(c) != -1;
    }

    public static boolean isSpecialUpperThaiChar(char c) {
        return SPECIAL_UPPER_CHARS.indexOf(c) != -1;
    }

    public static boolean isVeryLongTailThaiChar(char c) {
        return LONG_TAIL_CHARS.indexOf(c) != -1;
    }
}