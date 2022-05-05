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

package com.lion328.thaifixes.rendering.font;

import com.lion328.thaifixes.ThaiUtil;
import com.lion328.thaifixes.rendering.FontManager;
import com.lion328.thaifixes.rendering.GLFunctions;
import org.lwjgl.opengl.GL11;

public class UnicodeFont extends ThaiFont {

    private float saraAmDisplayWidth = Float.NaN;
    private float saraAmDisplayShift = Float.NaN;

    @Override
    public void onManagerChanged() {
        if (getManager() == null)
            return;

        int aaWidth = (getManager().getRawUnicodeWidth(ThaiUtil.SARA_AA) & 15) + 1;
        int amWIdth = (getManager().getRawUnicodeWidth(ThaiUtil.SARA_AM) & 15) + 1;
        saraAmDisplayShift = -(amWIdth - aaWidth);
        saraAmDisplayWidth = (aaWidth + saraAmDisplayShift) / 2.0F + 1.0F;
    }

    @Override
    public boolean isSupportedCharacter(char c) {
        return ThaiUtil.isFloating(c) || c == ThaiUtil.SARA_AM;
    }

    @Override
    public float renderCharacter(char c, boolean italic) {
        FontManager renderer = getManager();

        int rawWidth = renderer.getRawUnicodeWidth(c) & 0xFF;
        float startTexcoordX = (float) (rawWidth >>> 4);
        float charWidth = (float) ((rawWidth & 15) + 1);

        float posXShift = -((charWidth - startTexcoordX) / 2.0F + 1.0F);
        float posYShift = 0.0F;
        float height = 2.99F;

        if (c == ThaiUtil.SARA_AM) {
            posXShift = saraAmDisplayShift;
            height = 8.0F;
        } else if (ThaiUtil.isFloatingBelow(c)) {
            height = 1.99F;
            posYShift = 6.0F;
        }

        float heightX2 = height * 2;

        float texcoordX = (float) (c % 16 * 16) + startTexcoordX;
        float texcoordY = (float) ((c & 255) / 16 * 16) + (posYShift * 2);
        float texcoordXEnd = charWidth - startTexcoordX - 0.02F;
        float skew = italic ? 1.0F : 0.0F;

        float posX = renderer.getX() + posXShift;
        float posY = renderer.getY() + posYShift;

        renderer.loadUnicodeTexture(0x0E);

        GLFunctions.begin(GL11.GL_TRIANGLE_STRIP);
        GLFunctions.texCoord(texcoordX / 256.0F, texcoordY / 256.0F);
        GLFunctions.vertex(posX + skew, posY, 0.0F);
        GLFunctions.texCoord(texcoordX / 256.0F, (texcoordY + heightX2) / 256.0F);
        GLFunctions.vertex(posX - skew, posY + height, 0.0F);
        GLFunctions.texCoord((texcoordX + texcoordXEnd) / 256.0F, texcoordY / 256.0F);
        GLFunctions.vertex(posX + texcoordXEnd / 2.0F + skew, posY, 0.0F);
        GLFunctions.texCoord((texcoordX + texcoordXEnd) / 256.0F, (texcoordY + heightX2) / 256.0F);
        GLFunctions.vertex(posX + texcoordXEnd / 2.0F - skew, posY + height, 0.0F);
        GLFunctions.end();

        if (c == ThaiUtil.SARA_AM)
            return saraAmDisplayWidth;

        return 0.0F;
    }

    @Override
    public int getCharacterWidth(char c) {
        if (c == ThaiUtil.SARA_AM)
            return (int) saraAmDisplayWidth;
        return 0;
    }
}
