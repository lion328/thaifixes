package com.lion328.thaifixes;

import com.lion328.thaifixes.config.ThaiFixesConfiguration;

public class InjectedConstants
{

    public static int getFontHeight()
    {
        return ThaiFixesConfiguration.isMCPXChatResizeEnable() ? 16 : 9;
    }

    public static int getChatLineTextYOffset()
    {
        return ThaiFixesConfiguration.isMCPXChatResizeEnable() ? 11 : 8;
    }

    public static int getChatTextFieldHeight()
    {
        return ThaiFixesConfiguration.isMCPXChatResizeEnable() ? 13 : 12;
    }

    public static int getChatTextFieldBoxHeight()
    {
        return ThaiFixesConfiguration.isMCPXChatResizeEnable() ? 18 : 14;
    }
}
