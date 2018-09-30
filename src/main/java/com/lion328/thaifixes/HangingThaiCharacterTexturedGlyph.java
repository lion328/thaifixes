package com.lion328.thaifixes;

import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class HangingThaiCharacterTexturedGlyph extends TexturedGlyph
{

    private static Field[] parentFields;

    private final char character;

    private HangingThaiCharacterTexturedGlyph(char c)
    {
        super(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        character = c;
    }

    @Override
    public float func_211232_a()
    {
        return 0.0f;
    }

    @Override
    public void func_211234_a(TextureManager p_211234_1_, boolean p_211234_2_, float p_211234_3_, float p_211234_4_, BufferBuilder p_211234_5_, float p_211234_6_, float p_211234_7_, float p_211234_8_, float p_211234_9_)
    {
        p_211234_3_ -= super.func_211232_a();

        super.func_211234_a(p_211234_1_, p_211234_2_, p_211234_3_, p_211234_4_, p_211234_5_, p_211234_6_, p_211234_7_, p_211234_8_, p_211234_9_);
    }

    public static HangingThaiCharacterTexturedGlyph fromParent(TexturedGlyph glyph, char c)
    {
        if (parentFields == null) {
            return null;
        }

        HangingThaiCharacterTexturedGlyph ret = new HangingThaiCharacterTexturedGlyph(c);

        for (Field field : parentFields) {
            try
            {
                field.set(ret, field.get(glyph));
            }
            catch (IllegalAccessException e)
            {
                ThaiFixes.getLogger().info("Failed to create cloned glyph", e);
                return null;
            }
        }

        return ret;
    }

    public static void initialize() throws NoSuchFieldException, IllegalAccessException
    {
        if (parentFields != null) {
            return;
        }

        Field modiferField = Field.class.getDeclaredField("modifiers");
        modiferField.setAccessible(true);

        List<Field> filteredFields = new ArrayList<>();

        for (Field field : TexturedGlyph.class.getDeclaredFields()) {
            int modifier = modiferField.getInt(field);

            if ((modifier & Modifier.STATIC) == Modifier.STATIC) {
                continue;
            }

            if ((modifier & Modifier.FINAL) == Modifier.FINAL) {
                modiferField.set(field, modifier & ~Modifier.FINAL);
            }

            field.setAccessible(true);
            filteredFields.add(field);
        }

        parentFields = filteredFields.toArray(new Field[] {});
    }
}
