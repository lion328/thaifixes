package com.lion328.thaifixes.renderer;

import com.lion328.thaifixes.FontRendererWrapper;
import com.lion328.thaifixes.IFontRenderer;
import com.lion328.thaifixes.ThaiUtil;
import net.minecraft.client.renderer.GlStateManager;
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

        byte rawWidth = wrapper.getRawUnicodeWidth(c);

        float startTexcoordX = (float) (rawWidth >>> 4);
        float charWidth = (float) ((rawWidth & 15) + 1);
        float texcoordX = (float) (c % 16 * 16) + startTexcoordX;
        float texcoordY = (float) ((c & 255) / 16 * 16) + (posYShift * 2);
        float texcoordXEnd = charWidth - startTexcoordX - 0.02F;
        float skew = italic ? 1.0F : 0.0F;

        float posX = wrapper.getX() - ((charWidth - startTexcoordX) / 2.0F + 0.5F);
        float posY = wrapper.getY() + posYShift;

        GlStateManager.glBegin(GL11.GL_TRIANGLE_STRIP);
        GlStateManager.glTexCoord2f(texcoordX / 256.0F, texcoordY / 256.0F);
        GlStateManager.glVertex3f(posX + skew, posY, 0.0F);
        GlStateManager.glTexCoord2f(texcoordX / 256.0F, (texcoordY + heightX2) / 256.0F);
        GlStateManager.glVertex3f(posX - skew, posY + height, 0.0F);
        GlStateManager.glTexCoord2f((texcoordX + texcoordXEnd) / 256.0F, texcoordY / 256.0F);
        GlStateManager.glVertex3f(posX + texcoordXEnd / 2.0F + skew, posY, 0.0F);
        GlStateManager.glTexCoord2f((texcoordX + texcoordXEnd) / 256.0F, (texcoordY + heightX2) / 256.0F);
        GlStateManager.glVertex3f(posX + texcoordXEnd / 2.0F - skew, posY + height, 0.0F);
        GlStateManager.glEnd();
        return 0.0F;
    }

    @Override
    public int getCharacterWidth(char c)
    {
        return 0;
    }
}
