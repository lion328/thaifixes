package com.lion328.thaifixes.config.gui;

import com.lion328.thaifixes.ModInformation;
import com.lion328.thaifixes.config.ThaiFixesConfiguration;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiThaiFixesConfig extends GuiConfig
{

    public GuiThaiFixesConfig(GuiScreen parent)
    {
        super(parent, getConfigElements(), ModInformation.MODID, false, false, I18n.format("thaifixes.config.title"));
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> elements = new ArrayList<>();

        elements.addAll(new ConfigElement(ThaiFixesConfiguration.getConfiguration().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
        elements.add(new DummyConfigElement.DummyCategoryElement("mcpxCfg", "thaifixes.config.mcpx", MCPXEntry.class));

        return elements;
    }

    public static class MCPXEntry extends GuiConfigEntries.CategoryEntry
    {

        public MCPXEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
        {
            super(owningScreen, owningEntryList, configElement);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return new GuiConfig(owningScreen, new ConfigElement(ThaiFixesConfiguration.getConfiguration().getCategory(ThaiFixesConfiguration.CATEGORY_MCPX)).getChildElements(),
                    ModInformation.MODID, false, false, I18n.format("thaifixes.config.mcpx.title"));
        }
    }
}
