package com.lion328.thaifixes;

import net.minecraft.client.gui.fonts.IGlyphInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThaiFixes implements InitializationListener
{

    private static Logger logger;
    public static Map<IGlyphInfo, Character> processingThaiChars = new ConcurrentHashMap<>();

    @Override
    public void onInitialization()
    {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.thaifixes.json");

        try
        {
            HangingThaiCharacterTexturedGlyph.initialize();
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            getLogger().error("Failed to initialize HangingThaiCharacterTexturedGlyph clone methods", e);
        }
    }

    public static Logger getLogger()
    {
        if (logger == null)
        {
            logger = LogManager.getLogger("ThaiFixes");
        }

        return logger;
    }
}
