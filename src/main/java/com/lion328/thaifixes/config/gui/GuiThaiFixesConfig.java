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

public class GuiThaiFixesConfig extends GuiConfig {

    public GuiThaiFixesConfig(GuiScreen parent) {
        super(parent, getConfigElements(), ModInformation.MODID, false, false, I18n.format("thaifixes.config.title"));
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> elements = new ArrayList<>();

        elements.addAll(new ConfigElement(ThaiFixesConfiguration.getConfiguration().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
        elements.add(new DummyConfigElement.DummyCategoryElement("mcpxCfg", "thaifixes.config.mcpx", MCPXEntry.class));

        return elements;
    }

    public static class MCPXEntry extends GuiConfigEntries.CategoryEntry {

        public MCPXEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(owningScreen, new ConfigElement(ThaiFixesConfiguration.getConfiguration().getCategory(ThaiFixesConfiguration.CATEGORY_MCPX)).getChildElements(),
                    ModInformation.MODID, false, false, I18n.format("thaifixes.config.mcpx.title"));
        }
    }
}
