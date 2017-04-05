package com.lion328.thaifixes.gui;

import com.lion328.thaifixes.Config;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiThaiFixesConfig extends GuiConfig
{

    public GuiThaiFixesConfig(GuiScreen parent)
    {
        super(parent, new ConfigElement(Config.getConfiguration().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                "thaifixes", false, false, I18n.format("thaifixes.config.title"));
    }
}
