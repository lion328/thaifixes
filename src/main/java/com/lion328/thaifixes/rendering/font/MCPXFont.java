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

import com.lion328.thaifixes.ThaiFixes;
import com.lion328.thaifixes.ThaiUtil;
import com.lion328.thaifixes.config.ThaiFixesConfiguration;
import com.lion328.thaifixes.rendering.GLFunctions;
import com.lion328.thaifixes.rendering.ThaiFixesFontRenderer;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MCPXFont extends ThaiFont {

    public static final String MCPX_TEXTURE_LOCATION_RESOURCE = "thaifixes:textures/font/mcpx.png";
    public static final String MCPX_TEXTURE_LOCATION_JAR = "/assets/thaifixes/textures/font/mcpx.png";

    private int[] thaiCharWidth;
    private float lastPosX = Float.NaN;

    public MCPXFont() {
        thaiCharWidth = new int[256];

        BufferedImage bufferedimage = null;
        try {
            bufferedimage = ImageIO.read(this.getClass().getResourceAsStream(MCPX_TEXTURE_LOCATION_JAR));
        } catch (IOException e) {
            ThaiFixes.getLogger().catching(e);
        }

        int width = bufferedimage.getWidth();
        int height = bufferedimage.getHeight();
        int[] texture = new int[width * height];

        int xSize = width / 16;
        int ySize = height / 16;

        byte space = 1;
        float f = 8.0F / (float) xSize;
        int charPos = 0;

        bufferedimage.getRGB(0, 0, width, height, texture, 0, width);

        while (charPos < 256) {
            int col = charPos % 16;
            int row = charPos / 16;
            int l1 = xSize - 1;

            while (true) {
                if (l1 >= 0) {
                    boolean end = true;

                    for (int j2 = 0; j2 < ySize && end; ++j2) {
                        if ((texture[(col * xSize + l1) + (row * xSize + j2) * width] >> 24 & 0xFF) != 0) {
                            end = false;
                        }
                    }
                    if (end) {
                        --l1;
                        continue;
                    }
                }

                ++l1;
                thaiCharWidth[charPos] = (int) (0.5D + (double) ((float) l1 * f)) + space;
                ++charPos;
                break;
            }
        }
    }

    @Override
    public boolean isSupportedCharacter(char c) {
        return ThaiUtil.isThai(c);
    }

    @Override
    public String preStringRendered(String text) {
        return ThaiUtil.expandSaraAm(text);
    }

    @Override
    public float renderCharacter(char c, boolean italic) {
        ThaiFixesFontRenderer renderer = getRenderer();
        char lastChar = renderer.getLastCharacterRendered();

        int offset = c - ThaiUtil.THAI_RANGE_MIN + 1;

        float posX = renderer.getX();

        float cPosX = posX;
        float cPosY = renderer.getY();

        if (ThaiUtil.isFloating(c)) {
            cPosX -= thaiCharWidth[offset];

            if (ThaiUtil.isFloatingAbove(c)) {
                cPosY -= 7.0F;
            } else {
                cPosY += 2.0F;
            }

            if (ThaiUtil.isTallAlphabet(lastChar)) {
                cPosY -= 1.0F;
            }

            if (ThaiUtil.isFloatingAbove(lastChar)) {
                cPosY -= 2.25F;
            }
        } else if (c == ThaiUtil.SARA_AM) {
            cPosX -= 2.0F;
        }

        float texcoordX = (float) (offset % 16 * 8);
        float texcoordY = (float) (offset / 16 * 8);
        float italicSize = italic ? 1.0F : 0.0F;
        float f3 = (float) thaiCharWidth[offset] - 0.01F;

        renderer.bindTexture(MCPX_TEXTURE_LOCATION_RESOURCE);

        GLFunctions.begin(GL11.GL_TRIANGLE_STRIP);
        GLFunctions.texCoord(texcoordX / 128.0F, texcoordY / 128.0F);
        GLFunctions.vertex(cPosX + italicSize, cPosY);
        GLFunctions.texCoord(texcoordX / 128.0F, (texcoordY + 7.99F) / 128.0F);
        GLFunctions.vertex(cPosX - italicSize, cPosY + 7.99F);
        GLFunctions.texCoord((texcoordX + f3 - 1.0F) / 128.0F, texcoordY / 128.0F);
        GLFunctions.vertex(cPosX + f3 - 1.0F + italicSize, cPosY);
        GLFunctions.texCoord((texcoordX + f3 - 1.0F) / 128.0F, (texcoordY + 7.99F) / 128.0F);
        GLFunctions.vertex(cPosX + f3 - 1.0F - italicSize, cPosY + 7.99F);
        GLFunctions.end();

        return ThaiUtil.isFloating(c) ? 0 : (float) thaiCharWidth[offset] - posX + cPosX;
    }

    @Override
    public int getCharacterWidth(char c) {
        if (ThaiUtil.isFloating(c)) {
            return 0;
        }

        int ret = thaiCharWidth[c - ThaiUtil.THAI_RANGE_MIN + 1];

        if (c == ThaiUtil.SARA_AM) {
            return ret - 2;
        }

        return ret;
    }

    @Override
    public float getShadowShiftSize(char c, float shift) {
        return ThaiFixesConfiguration.isLargeMCPXShadowEanbled() ? 0 : super.getShadowShiftSize(c, shift);
    }
}
