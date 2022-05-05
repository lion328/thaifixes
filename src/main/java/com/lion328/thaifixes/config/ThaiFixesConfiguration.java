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

package com.lion328.thaifixes.config;

import com.lion328.thaifixes.ThaiFixes;
import com.lion328.thaifixes.rendering.font.FontStyle;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ThaiFixesConfiguration {

    public static final String CATEGORY_MCPX = "mcpx";

    private static Configuration configuration;

    private static String[] avaliableFontStyle;

    private static FontStyle fontStyle;
    private static boolean enableMCPXChatResize;
    private static boolean largeMCPXShadow;
    private static boolean asciiFontWithMCPX;

    static {
        avaliableFontStyle = FontStyle.asStringArray();
    }

    public static void init(File configFile) {
        configuration = new Configuration(configFile);
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static FontStyle getFontStyle() {
        return fontStyle;
    }

    public static boolean isMCPXChatResizeEnable() {
        return ThaiFixes.isFontRendererPatched() && fontStyle == FontStyle.MCPX && enableMCPXChatResize;
    }

    public static boolean isLargeMCPXShadowEanbled() {
        return ThaiFixes.isFontRendererPatched() && fontStyle == FontStyle.MCPX && largeMCPXShadow;
    }

    public static boolean isASCIIFontWithMCPXEnabled() {
        return ThaiFixes.isFontRendererPatched() && fontStyle == FontStyle.MCPX && asciiFontWithMCPX;
    }

    public static void syncConfig() {
        fontStyle = FontStyle.fromString(configuration.getString("fontStyle", Configuration.CATEGORY_GENERAL, "Unicode",
                I18n.format("thaifixes.config.fontStyle.desc"),
                avaliableFontStyle, "thaifixes.config.fontStyle"));

        enableMCPXChatResize = configuration.getBoolean("enableChatResize", CATEGORY_MCPX, true,
                I18n.format("thaifixes.config.mcpx.enableChatResize.desc"),
                "thaifixes.config.mcpx.enableChatResize");

        largeMCPXShadow = configuration.getBoolean("largeMCPXShadow", CATEGORY_MCPX, true,
                I18n.format("thaifixes.config.mcpx.largeMCPXShadow.desc"),
                "thaifixes.config.mcpx.largeMCPXShadow");

        asciiFontWithMCPX = configuration.getBoolean("asciiFontWithMCPX", CATEGORY_MCPX, true,
                I18n.format("thaifixes.config.mcpx.asciiFontWithMCPX.desc"),
                "thaifixes.config.mcpx.asciiFontWithMCPX");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
