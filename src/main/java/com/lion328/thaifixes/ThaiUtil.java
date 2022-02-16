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

package com.lion328.thaifixes;

public class ThaiUtil {

    public static final char THAI_RANGE_MIN = '\u0E01';
    public static final char THAI_RANGE_MAX = '\u0E5B';
    public static final String FLOATING_BELOW_CHARS = "\u0E38\u0E39\u0E3A";
    public static final String TONE_MARKS = "\u0E48\u0E49\u0E4A\u0E4B";
    public static final String FLOATING_ABOVE_CHARS = "\u0E31\u0E34\u0E35\u0E36\u0E37\u0E47\u0E4C\u0E4D\u0E4E" + TONE_MARKS;
    public static final String TALL_CHARS = "\u0E1B\u0E1F\u0E1D\u0E2C";
    public static final char SARA_AM = '\u0E33';
    public static final char SARA_AA = '\u0E32';
    public static final char NIKHAHIT = '\u0E4D';

    public static boolean isThai(char c) {
        return c >= THAI_RANGE_MIN && c <= THAI_RANGE_MAX;
    }

    public static boolean isFloating(char c) {
        return isFloatingAbove(c) || isFloatingBelow(c);
    }

    public static boolean isFloatingAbove(char c) {
        return FLOATING_ABOVE_CHARS.indexOf(c) != -1;
    }

    public static boolean isFloatingBelow(char c) {
        return FLOATING_BELOW_CHARS.indexOf(c) != -1;
    }

    public static boolean isToneMark(char c) {
        return TONE_MARKS.indexOf(c) != -1;
    }

    public static boolean isTallAlphabet(char c) {
        return TALL_CHARS.indexOf(c) != -1;
    }
}
