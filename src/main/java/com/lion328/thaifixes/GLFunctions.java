package com.lion328.thaifixes;

import org.lwjgl.opengl.GL11;

public class GLFunctions
{

    public static void begin(int f)
    {
        GL11.glBegin(f);
    }

    public static void end()
    {
        GL11.glEnd();
    }

    public static void texCoord(float u, float v)
    {
        GL11.glTexCoord2f(u, v);
    }

    public static void vertex(float x, float y, float z)
    {
        GL11.glVertex3f(x, y, z);
    }

    public static void vertex(float x, float y)
    {
        GL11.glVertex2f(x, y);
    }
}
