package com.connorcode.sigmautils.config.settings.list;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

public class ModuleList implements DynamicListSetting.ResourceManager<Class<? extends Module>> {
    DynamicListSetting<Class<? extends Module>> setting;

    public ModuleList(DynamicListSetting<Class<? extends Module>> setting) {
        this.setting = setting;
    }

    @Override
    public int width() {
        return 320;
    }

    @Override
    public List<Class<? extends Module>> getAllResources() {
        return SigmaUtils.modules.keySet().stream().toList();
    }

    @Override
    public String getDisplay(Class<? extends Module> resource) {
        return SigmaUtils.modules.get(resource).name;
    }

    @Override
    public void render(Class<? extends Module> resource, Screen data, int x, int y) {
        SimpleList.render(setting, resource, getDisplay(resource), data, x, y, 0);
    }

    @Override
    public boolean renderSelector(Class<? extends Module> resource, Screen data, int x, int y) {
        if (setting.value().contains(resource)) return false;
        SimpleList.selector(setting, resource, getDisplay(resource), data, x, y, 0);
        return true;
    }

    @Override
    public NbtElement serialize(List<Class<? extends Module>> resources) {
        return new NbtList() {{
            addAll(resources.stream().map(m -> NbtString.of(SigmaUtils.modules.get(m).id)).toList());
        }};
    }

    @Override
    public List<Class<? extends Module>> deserialize(NbtElement nbt) {
        if (!(nbt instanceof NbtList resourceList)) return null;
        var list = resourceList.stream().map(e -> getModule(e.asString()).getClass()).toList();
        return new ArrayList<>(list);
    }

    Module getModule(String id) {
        return SigmaUtils.modules.values().stream().filter(m -> m.id.equals(id)).findFirst().orElse(null);
    }

    @Override
    public String type() {
        return "Module";
    }
}
