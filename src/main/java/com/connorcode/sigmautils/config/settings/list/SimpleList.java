package com.connorcode.sigmautils.config.settings.list;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public abstract class SimpleList<T> implements DynamicListSetting.ResourceManager<T> {
    protected Registry<T> registry;
    protected DynamicListSetting<T> setting;

    public SimpleList(DynamicListSetting<T> setting, Registry<T> registry) {
        this.setting = setting;
        this.registry = registry;
    }

    public static <T> void selector(DynamicListSetting<T> setting, T resource, String display, Screen screen, int x, int y, int gap) {
        var padding = getPadding();
        Util.addChild(screen, new ButtonWidget(x, y, 20, 20, Text.of("+"), button -> {
            setting.add(resource);
            ((ScreenAccessor) screen).invokeClearAndInit();
        }, ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices,
                List.of(Text.of("Add element").asOrderedText()), mouseX, mouseY))));
        // TODO: ^ Fix tooltips being cut off
        Util.addDrawable(screen,
                (matrices, mouseX, mouseY, delta) -> client.textRenderer.draw(matrices, display,
                        x + 20 + padding * 4 + gap, y + padding / 2f + 10 - client.textRenderer.fontHeight / 2f,
                        0xFFFFFF));
    }

    abstract public String getDisplay(T value);

    @Override
    public List<T> getAllResources() {
        return registry.stream().toList();
    }

    @Override
    public void render(T resource, Screen screen, int x, int y) {
        var padding = getPadding();
        Util.addChild(screen, new ButtonWidget(x, y, 20, 20, Text.of("×"), button -> {
            setting.remove(resource);
            ((ScreenAccessor) screen).invokeClearAndInit();
        }, ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices,
                List.of(Text.of("Remove element").asOrderedText()), mouseX, mouseY))));
        Util.addDrawable(screen,
                (matrices, mouseX, mouseY, delta) -> client.textRenderer.draw(matrices, getDisplay(resource),
                        x + 20 + padding * 4, y + padding / 2f + 10 - client.textRenderer.fontHeight / 2f,
                        0xffffff));
    }

    @Override
    public boolean renderSelector(T resource, Screen screen, int x, int y) {
        if (setting.value().contains(resource)) return false;
        selector(setting, resource, getDisplay(resource), screen, x, y, 0);
        return true;
    }

    @Override
    public NbtElement serialize(List<T> resources) {
        return new NbtList() {{
            addAll(resources.stream()
                    .map(e -> NbtString.of(Objects.requireNonNull(registry.getId(e)).toString()))
                    .toList());
        }};
    }

    @Override
    public List<T> deserialize(NbtElement nbt) {
        if (!(nbt instanceof NbtList resourceList) || registry == null) return null;
        var list = resourceList.stream()
                .map(e -> Objects.requireNonNull(registry.get(Identifier.tryParse(e.asString()))))
                .toList();
        return new ArrayList<>(list);
    }
}
