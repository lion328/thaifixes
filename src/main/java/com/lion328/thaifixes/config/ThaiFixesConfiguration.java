package com.lion328.thaifixes.config;

import com.lion328.thaifixes.FontStyle;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ThaiFixesConfiguration
{

    public static final String CATEGORY_MCPX = "mcpx";

    private static Configuration configuration;

    private static String[] avaliableFontStyle;

    private static FontStyle fontStyle;
    private static boolean enableMCPXChatResize;

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

    public static boolean isMCPXChatResizeEnable()
    {
        return fontStyle == FontStyle.MCPX && enableMCPXChatResize;
    }

    public static void syncConfig()
    {
        fontStyle = FontStyle.fromString(configuration.getString("fontStyle", Configuration.CATEGORY_GENERAL, "Unicode",
                I18n.format("thaifixes.config.fontStyle.desc"),
                avaliableFontStyle, "thaifixes.config.fontStyle"));

        enableMCPXChatResize = configuration.getBoolean("enableChatResize", CATEGORY_MCPX, true,
                I18n.format("thaifixes.config.mcpx.enableChatResize.desc"),
                "thaifixes.config.mcpx.enableChatResize");

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }
}
