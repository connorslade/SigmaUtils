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
import java.util.Comparator;
import java.util.List;

public class ModuleHud extends HudModule {
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(ModuleHud.class)
            .description("Weather the module selector whitelists or blacklists modules.")
            .build();
    static DynamicListSetting<Class<? extends Module>> modules =
            new DynamicListSetting<>(ModuleHud.class, "Modules", ModuleList::new).description(
                    "Modules to display in the HUD.").build();
    static EnumSetting<Sort> sort =
            new EnumSetting<>(ModuleHud.class, "Sort", Sort.class).description("The way to sort modules in the list.")
                    .value(Sort.None)
                    .build();

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
        var list = new ArrayList<>(SigmaUtils.modules.values().stream()
                .filter(ModuleHud::shouldShow)
                .map(m -> getTextColor() + m.name)
                .toList());

        switch (sort.value()) {
            case None -> {}
            case SmallToLarge -> list.sort(Comparator.comparing(String::length));
            case LargeToSmall -> list.sort(Comparator.comparing(String::length).reversed());
            case Alphabetical -> list.sort(Comparator.naturalOrder());
        }

        return list;
    }

    enum Sort {
        None,
        SmallToLarge,
        LargeToSmall,
        Alphabetical
    }
}
