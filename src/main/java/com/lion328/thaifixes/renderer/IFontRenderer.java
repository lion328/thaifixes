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

package com.lion328.thaifixes.renderer;

import com.lion328.thaifixes.FontRendererWrapper;

public interface IFontRenderer {

    /**
     * Set FontRendererWrapper object.
     *
     * @param wrapper FontRendererWrapper object.
     */
    void setFontRendererWrapper(FontRendererWrapper wrapper);

    /**
     * Check for supported character.
     *
     * @param c Character.
     * @return Return true if support.
     */
    boolean isSupportedCharacter(char c);

    /**
     * Render a character.
     *
     * @param c      Character to render.
     * @param italic Italic style.
     * @return Return character width.
     */
    float renderCharacter(char c, boolean italic);

    /**
     * Get character width.
     *
     * @param c Character.
     * @return Width of character.
     */
    int getCharacterWidth(char c);
}
