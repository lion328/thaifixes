package com.lion328.thaifixes;

import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import net.minecraft.client.resources.I18n;

public class ThaiFixesConfigPanel extends AbstractConfigPanel
{

    @Override
    protected void addOptions(ConfigPanelHost host)
    {

    }

    @Override
    public String getPanelTitle()
    {
        return I18n.format("thaifixes.config.title");
    }

    @Override
    public void onPanelHidden()
    {

    }
}
