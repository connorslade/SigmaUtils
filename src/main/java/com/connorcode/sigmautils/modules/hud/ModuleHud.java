package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;

import java.util.List;

public class ModuleHud extends HudModule {
    private static final BoolSetting hudModules = new BoolSetting(ModuleHud.class, "Hud Modules").value(false)
            .description("Show hud modules in the list")
            .build();

    public ModuleHud() {
        super("module_hud", "Module Hud", "Shows all enabled modules in the hud", Category.Hud);
        this.defaultOrder = 10;
        this.defaultTextColor = TextStyle.Color.White;
    }

    @Override
    public List<String> lines() {
        return SigmaUtils.modules.values().stream()
                .filter(m -> m.enabled && (hudModules.value() || !(m instanceof HudModule)))
                .map(m -> getTextColor() + m.name)
                .toList();
    }
}
