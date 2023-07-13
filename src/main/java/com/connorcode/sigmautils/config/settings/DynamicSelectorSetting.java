package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;

public class DynamicSelectorSetting<K> extends Setting<DynamicSelectorSetting<K>> {
    ResourceManager<K> manager;
    K value;

    public DynamicSelectorSetting(Class<? extends Module> module, String name, ResourceManagerCallback<K> callback) {
        super(module, name);
        this.manager = callback.get(this);
    }

    public K value() {
        return this.value;
    }

    public void setValue(K value) {
        this.value = value;
        this.manager.onChange(value);
    }

    @Override
    protected DynamicSelectorSetting<K> getThis() {
        return this;
    }

    @Override
    public void serialize(NbtCompound nbt) {
        var data = this.manager.serialize(value);
        if (data == null) return;
        nbt.put(this.id, data);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        var data = this.manager.deserialize(nbt.get(this.id));
        if (data == null) return;
        this.value = data;
        this.manager.onChange(data);
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        var list = new ArrayList<OrderedText>();
        list.add(Text.of("Left Click: Select").asOrderedText());
        list.add(Text.of("Right Click: Clear").asOrderedText());

        Util.addChild(screen,
                new Components.MultiClickButton(x, y, width, 20,
                        Text.of(String.format("%s: %s", this.name, this.manager.getDisplay(value))), button -> {
                    if (button.click == 0) client.setScreen(new ResourceSelectScreen<>(screen, manager));
                    else if (button.click == 1) {
                        this.value = null;
                        ((ScreenAccessor) screen).invokeClearAndInit();
                    }
                }, (button, drawContext, mouseX, mouseY) -> drawContext.drawOrderedTooltip(client.textRenderer,
                        list, mouseX, mouseY))
        );

        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {}

    public interface ResourceManagerCallback<K> {
        ResourceManager<K> get(DynamicSelectorSetting<K> setting);
    }

    public interface ResourceManager<T> {
        // Get all selectable resources
        List<T> getAllResources();

        String getDisplay(@Nullable T resource);

        default String[] getSearch(T resource) {
            return new String[]{getDisplay(resource).toLowerCase(Locale.ROOT)};
        }

        // Render the add resource line
        boolean renderSelector(T resource, Screen data, int x, int y);

        // Get the width of the resource line
        default int width() {
            return 220;
        }

        default int height() {
            return 20;
        }

        // Save the active resources to NBT
        NbtElement serialize(@Nullable T resources);

        // Load the active resources from NBT
        @Nullable T deserialize(NbtElement nbt);

        default void onChange(@Nullable T resource) {}
    }

    static class ResourceSelectScreen<K> extends Components.ScrollableScreen {
        Screen _super;
        ResourceManager<K> renderer;
        TextFieldWidget searchField;
        int count;

        protected ResourceSelectScreen(Screen _super, ResourceManager<K> renderer) {
            super(Text.of("Resource Screen"), getPadding(), renderer.height(), renderer.width());
            this.renderer = renderer;
            this._super = _super;
        }

        @Override
        protected void init() {
            this.searchField =
                    new TextFieldWidget(SigmaUtils.client.textRenderer, 0, 10, entryWidth / 2, 20, this.searchField,
                            Text.empty());
            focusOn(this.searchField);
            searchField.setX(width / 2 - searchField.getWidth() / 2);
            var y = padding * 6 + 30 - (int) this.scroll;
            var x = 20 + padding + startX();

            var search = searchField.getText();
            var res = renderer.getAllResources();
            count = res.size();
            for (var i : res
                    .stream()
                    .filter(r -> search.isEmpty() || search(r, search))
                    .toList()) {
                if (y < -entryHeight - padding) {
                    y += entryHeight + padding;
                    continue;
                }
                if (y > height + scroll) break;
                if (!renderer.renderSelector(i, this, x, y)) continue;
                y += entryHeight + padding;
            }
        }

        @Override
        public void tick() {
            this.searchField.tick();
        }

        private boolean search(K resource, String search) {
            var finalSearch = search.toLowerCase(Locale.ROOT);
            return Arrays.stream(renderer.getSearch(resource)).anyMatch(s -> s.contains(finalSearch));
        }

        @Override
        public void close() {
            Objects.requireNonNull(client).setScreen(_super);
        }

        @Override
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            renderBackground(drawContext);
            super.render(drawContext, mouseX, mouseY, delta);
            this.searchField.render(drawContext, mouseX, mouseY, delta);
        }

        @Override
        protected double maxScroll() {
            return (entryHeight + padding) * count - Math.max(height * 0.80, 0);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.searchField.mouseClicked(mouseX, mouseY, button)) return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
                clearAndInit();
                refreshScrollConstrains();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (this.searchField.charTyped(chr, modifiers)) {
                clearAndInit();
                refreshScrollConstrains();
                return true;
            }
            return super.charTyped(chr, modifiers);
        }

        @Override
        protected int scrollOffset() {
            return 20;
        }
    }
}
