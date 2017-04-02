package com.lion328.thaifixes;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config
{

    public static final FontStyle DEFAULT_FONT_STYLE = FontStyle.UNICODE;

    private static Configuration configuration;

    private static String[] avaliableFontStyle;

    private static FontStyle fontStyle;

    static
    {
        avaliableFontStyle = FontStyle.asStringArray();
    }

    public static void init(File configFile)
    {
        configuration = new Configuration(configFile);
    }

    public static Configuration getConfiguration()
    {
        return configuration;
    }

    public static FontStyle getFontStyle()
    {
        return fontStyle;
    }

    public static void syncConfig()
    {
        fontStyle = FontStyle.fromString(configuration.getString("fontStyle", Configuration.CATEGORY_GENERAL, "Unicode",
                I18n.format("thaifixes.config.fontStyle.desc"),
                avaliableFontStyle, "thaifixes.config.fontStyle"));

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }
}
