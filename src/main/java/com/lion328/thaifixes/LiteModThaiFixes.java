package com.lion328.thaifixes;

import com.mumfrey.liteloader.LiteMod;

import java.io.File;

public class LiteModThaiFixes implements LiteMod
{

    @Override
    public String getVersion()
    {
        return "0.0"; // FIXME: load from json
    }

    @Override
    public void init(File configPath)
    {

    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {

    }

    @Override
    public String getName()
    {
        return "ThaiFixes";
    }
}
