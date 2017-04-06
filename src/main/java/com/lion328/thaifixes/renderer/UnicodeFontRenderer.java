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
import com.lion328.thaifixes.GLFunctions;
import com.lion328.thaifixes.ThaiUtil;
import org.lwjgl.opengl.GL11;

public class UnicodeFontRenderer implements IFontRenderer
{

    private FontRendererWrapper wrapper;

    @Override
    public void setFontRendererWrapper(FontRendererWrapper wrapper)
    {
        this.wrapper = wrapper;
    }

    @Override
    public boolean isSupportedCharacter(char c)
    {
        return ThaiUtil.isSpecialThaiChar(c);
    }

    @Override
    public float renderCharacter(char c, boolean italic)
    {
        wrapper.loadUnicodeTexture(0x0E);

        float posYShift = 0.0F;
        float height = 2.99F;

        if (ThaiUtil.isLowerThaiChar(c))
        {
            height = 1.99F;
            posYShift = 6.0F;
        }

        float heightX2 = height * 2;

        int rawWidth = wrapper.getRawUnicodeWidth(c) & 0xFF;

        float startTexcoordX = (float) (rawWidth >>> 4);
        float charWidth = (float) ((rawWidth & 15) + 1);
        float texcoordX = (float) (c % 16 * 16) + startTexcoordX;
        float texcoordY = (float) ((c & 255) / 16 * 16) + (posYShift * 2);
        float texcoordXEnd = charWidth - startTexcoordX - 0.02F;
        float skew = italic ? 1.0F : 0.0F;

        float posX = wrapper.getX() - ((charWidth - startTexcoordX) / 2.0F + 0.5F);
        float posY = wrapper.getY() + posYShift;

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
        return 0.0F;
    }

    @Override
    public int getCharacterWidth(char c)
    {
        return 0;
    }
}
