package com.connorcode.sigmautils.modules.hud;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.ModuleList;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.HudModule;
import com.connorcode.sigmautils.module.Module;

import java.util.ArrayList;
import java.util.List;

public class ModuleHud extends HudModule {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(ModuleHud.class)
            .description("Weather the module selector whitelists or blacklists modules.")
            .build();
    static DynamicListSetting<Class<? extends Module>> modules =
            new DynamicListSetting<>(ModuleHud.class, "Modules", ModuleList::new).description(
                    "Modules to display in the HUD.").build();

    public ModuleHud() {
        super("module_hud", "Module Hud", "Shows all enabled modules in the hud", Category.Hud);
        this.defaultOrder = 10;
        this.defaultTextColor = TextStyle.Color.White;
    }

    static boolean shouldShow(Module module) {
        if (!module.enabled) return false;
        var inList = modules.value().contains(module.getClass());
        return filterType.value() == Util.FilterType.Whitelist == inList;
    }

    @Override
    public List<String> lines() {
        return SigmaUtils.modules.values().stream()
                .filter(ModuleHud::shouldShow)
                .map(m -> getTextColor() + m.name)
                .toList();
    }
}
