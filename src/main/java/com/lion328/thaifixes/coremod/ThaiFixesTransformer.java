package com.lion328.thaifixes.coremod;

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import com.lion328.thaifixes.coremod.patcher.FontRendererPatcher;
import com.lion328.thaifixes.coremod.patcher.FontRendererWrapperPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiChatPatcher;
import com.lion328.thaifixes.coremod.patcher.GuiNewChatPatcher;
import com.lion328.thaifixes.coremod.patcher.IClassPatcher;
import com.lion328.thaifixes.coremod.patcher.MinecraftPatcher;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.HashMap;
import java.util.Map;

public class ThaiFixesTransformer implements IClassTransformer {
    private Map<String, IClassPatcher> patchers = new HashMap<String, IClassPatcher>();

    public ThaiFixesTransformer() {
        initializePatchers();
    }

    private void initializePatchers() {
        IClassMap classMap = CoremodSettings.getObfuscatedClassmap();

        try {
            addPatcher(new MinecraftPatcher(classMap));
            addPatcher(new FontRendererPatcher(classMap));
            addPatcher(new GuiNewChatPatcher(classMap));
            addPatcher(new GuiChatPatcher(classMap));
            addPatcher(new FontRendererWrapperPatcher(classMap));
        } catch (Exception e) {
            CoremodSettings.LOGGER.catching(e);
        }
    }

    private void addPatcher(IClassPatcher patcher) {
        patchers.put(patcher.getClassName(), patcher);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (patchers.containsKey(name)) {
            CoremodSettings.LOGGER.info("Patching " + transformedName);

            try {
                return patchers.get(name).patch(original);
            } catch (Exception e) {
                CoremodSettings.LOGGER.catching(e);
            }
        }

        return original;
    }
}
