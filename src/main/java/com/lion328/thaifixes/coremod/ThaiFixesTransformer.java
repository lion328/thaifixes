package com.lion328.thaifixes.coremod;

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.coremod.patcher.FontRendererPatcher;
import com.lion328.thaifixes.coremod.patcher.FontRendererWrapperPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiChatPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiNewChatPatcher;
import com.lion328.thaifixes.coremod.patcher.IClassPatcher;
import com.lion328.thaifixes.coremod.patcher.MinecraftPatcher;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

public class ThaiFixesTransformer implements IClassTransformer {
    private List<IClassPatcher> patchers = new ArrayList<>();

    public ThaiFixesTransformer() {
        initializePatchers();
    }

    private void initializePatchers() {
        IClassMap classMap = CoremodSettings.getObfuscatedClassmap();

        try {
            patchers.add(new MinecraftPatcher(classMap));
            patchers.add(new FontRendererPatcher(classMap));
            patchers.add(new GuiNewChatPatcher(classMap));
            patchers.add(new GuiChatPatcher(classMap));
            patchers.add(new FontRendererWrapperPatcher(classMap));
        } catch (Exception e) {
            CoremodSettings.LOGGER.catching(e);
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        byte[] result = original;

        for (IClassPatcher patcher : patchers) {
            if (patcher.isSupported(name)) {
                CoremodSettings.LOGGER.info("Patching {} by {}", transformedName, patcher.getClass().getName());

                try {
                    result = patcher.patch(result);
                } catch (Exception e) {
                    CoremodSettings.LOGGER.catching(e);
                }
            }
        }

        return result;
    }
}
