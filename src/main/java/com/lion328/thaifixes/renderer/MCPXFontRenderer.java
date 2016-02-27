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

package com.lion328.thaifixes.renderer;

import com.lion328.thaifixes.Configuration;
import com.lion328.thaifixes.FontRendererWrapper;
import com.lion328.thaifixes.IFontRenderer;
import com.lion328.thaifixes.ThaiUtil;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MCPXFontRenderer implements IFontRenderer {

    private FontRendererWrapper wrapper;
    private int[] thaiCharWidth;
    private String mcpxFontLocation;

    public MCPXFontRenderer() {
        thaiCharWidth = new int[256];
        mcpxFontLocation = "thaifixes:textures/font/mcpx.png";

        BufferedImage bufferedimage = null;
        try {
            bufferedimage = ImageIO.read(this.getClass().getResourceAsStream("/assets/thaifixes/textures/font/mcpx.png"));
        } catch (IOException e) {
            Configuration.LOGGER.catching(e);
        }

        int width = bufferedimage.getWidth();
        int height = bufferedimage.getHeight();
        int[] texture = new int[width * height];
        bufferedimage.getRGB(0, 0, width, height, texture, 0, width);
        int xSize = width / 16;
        int ySize = height / 16;
        byte space = 1;
        float f = 8.0F / (float)xSize;
        int charPos = 0;

        while(charPos < 256) {
            int col = charPos % 16;
            int row = charPos / 16;
            int l1 = xSize - 1;
            while(true) {
                if(l1 >= 0) {
                    boolean end = true;
                    for(int j2 = 0; j2 < ySize && end; ++j2)
                        if((texture[(col * xSize + l1) + (row * xSize + j2) * width] >> 24 & 0xFF) != 0)
                            end = false;
                    if(end) {
                        --l1;
                        continue;
                    }
                }
                ++l1;
                thaiCharWidth[charPos] = (int)(0.5D + (double)((float)l1 * f)) + space;
                ++charPos;
                break;
            }
        }
    }

    @Override
    public void setFontRendererWrapper(FontRendererWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public boolean isSupportedCharacter(char c) {
        return ThaiUtil.isThaiChar(c);
    }

    @Override
    public float renderCharacter(char c, boolean italic) {
        int offset = c - ThaiUtil.THAI_CHAR_RANGE_MIN + 1;

        float cPosX = wrapper.getX();
        float cPosY = wrapper.getY();

        if(ThaiUtil.isSpecialThaiChar(c)) {
            cPosX -= thaiCharWidth[offset];
            if(ThaiUtil.isUpperThaiChar(c))
                cPosY -= 7.0F;
            else
                cPosY += 2.0F;
            if(ThaiUtil.isVeryLongTailThaiChar(wrapper.getLastCharacterRenderered()))
                cPosY -= 1.0F;
            if(ThaiUtil.isSpecialThaiChar(wrapper.getLastCharacterRenderered()))
                cPosY -= 3.0F;
        }

        float texcoordX = (float)(offset % 16 * 8);
        float texcoordY = (float)(offset / 16 * 8);
        float italicSize = italic ? 1.0F : 0.0F;
        float f3 = (float)thaiCharWidth[offset] - 0.01F;

        wrapper.bindTexture(mcpxFontLocation);

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(texcoordX / 128.0F, texcoordY / 128.0F);
        GL11.glVertex2f(cPosX + italicSize, cPosY);
        GL11.glTexCoord2f(texcoordX / 128.0F, (texcoordY + 7.99F) / 128.0F);
        GL11.glVertex2f(cPosX - italicSize, cPosY + 7.99F);
        GL11.glTexCoord2f((texcoordX + f3 - 1.0F) / 128.0F, texcoordY / 128.0F);
        GL11.glVertex2f(cPosX + f3 - 1.0F + italicSize, cPosY);
        GL11.glTexCoord2f((texcoordX + f3 - 1.0F) / 128.0F, (texcoordY + 7.99F) / 128.0F);
        GL11.glVertex2f(cPosX + f3 - 1.0F - italicSize, cPosY + 7.99F);
        GL11.glEnd();
        return ThaiUtil.isSpecialThaiChar(c) ? 0 : (float)thaiCharWidth[offset];
    }

    @Override
    public int getCharacterWidth(char c) {
        if(ThaiUtil.isSpecialThaiChar(c)) return 0;
        return thaiCharWidth[c - ThaiUtil.THAI_CHAR_RANGE_MIN + 1];
    }
}
