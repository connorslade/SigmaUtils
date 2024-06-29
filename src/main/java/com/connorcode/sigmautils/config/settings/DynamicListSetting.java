package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

import java.util.*;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;


public class DynamicListSetting<K> extends Setting<DynamicListSetting<K>> {
    ResourceManager<K> manager;
    List<K> resources = new ArrayList<>();

    public DynamicListSetting(Class<? extends Module> module, String name, ResourceManagerCallback<K> callback) {
        super(module, name);
        this.manager = callback.get(this);
    }

    public DynamicListSetting<K> value(K[] resources) {
        this.resources = new ArrayList<>(List.of(resources));
        return this;
    }

    public DynamicListSetting<K> value(List<K> resources) {
        this.resources = new ArrayList<>(resources);
        return this;
    }

    public ResourceManager<K> getManager() {
        return this.manager;
    }

    public List<K> value() {
        return this.resources;
    }

    public void add(K resource) {
        this.resources.add(resource);
    }

    public void remove(K resource) {
        this.resources.remove(resource);
    }

    @Override
    protected DynamicListSetting<K> getThis() {
        return this;
    }

    @Override
    public void serialize(NbtCompound nbt) {
        var data = this.manager.serialize(resources);
        if (data == null) return;
        nbt.put(this.id, data);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        var data = this.manager.deserialize(nbt.get(this.id));
        if (data == null) return;
        this.resources = data;
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        Util.addChild(screen,
                ButtonWidget.builder(Text.of(String.format("Edit %s (%d)", this.name, this.resources.size())),
                                button -> client.setScreen(new ResourceScreen(screen, this.manager)))
                        .position(x, y)
                        .size(width, 20)
                        .tooltip(Util.nullMap(getDescription(), Tooltip::of))
                        .build());

        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {}

    public interface ResourceManagerCallback<K> {
        ResourceManager<K> get(DynamicListSetting<K> setting);
    }

    public interface ResourceManager<T> {
        // Get all selectable resources
        List<T> getAllResources();

        String getDisplay(T resource);

        default String[] getSearch(T resource) {
            return new String[]{getDisplay(resource).toLowerCase(Locale.ROOT)};
        }

        // Render the resource line (main screen)
        void render(T resource, Screen data, int x, int y);

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
        NbtElement serialize(List<T> resources);

        // Load the active resources from NBT
        List<T> deserialize(NbtElement nbt);

        // Type of resource to show in documentation
        default String type() {
            return "";
        }

    }

    public static class ResourceAddScreen<K> extends Components.ScrollableScreen {
        Screen _super;
        ResourceManager<K> renderer;
        TextFieldWidget searchField;
        int count;

        protected ResourceAddScreen(Screen _super, ResourceManager<K> renderer) {
            super(Text.of("Resource Screen"), getPadding(), renderer.height(), renderer.width());
            this.renderer = renderer;
            this._super = _super;
        }

        @Override
        protected void init() {
            this.searchField = new TextFieldWidget(SigmaUtils.client.textRenderer, 0, 10, entryWidth / 2, 20, this.searchField, Text.empty());
            setFocused(this.searchField);
            searchField.setX(width / 2 - searchField.getWidth() / 2);
            var y = padding * 6 + 30 - (int) this.scroll;
            var x = 20 + padding + startX();

            var search = searchField.getText();
            var res = renderer.getAllResources();
            count = res.size();
            for (var i : res.stream().filter(r -> search.isEmpty() || search(r, search))
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
            renderBackground(drawContext, mouseX, mouseY, delta);
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

    public class ResourceScreen extends Components.ScrollableScreen {
        Screen _super;
        ResourceManager<K> renderer;
        int count;

        protected ResourceScreen(Screen _super, ResourceManager<K> renderer) {
            super(Text.of("Resource Screen"), getPadding(), renderer.height(), renderer.width());
            this.renderer = renderer;
            this._super = _super;
        }

        @Override
        protected void init() {
            var x = 20 + padding + startX();
            var y = padding * 4;

            var res = DynamicListSetting.this.resources;
            count = res.size();
            for (var i : res) {
                renderer.render(i, this, x, y);
                y += entryHeight + padding;
            }

            addDrawableChild(ButtonWidget.builder(Text.of("+ Add"),
                            button -> Objects.requireNonNull(client).setScreen(new ResourceAddScreen<>(this, this.renderer)))
                    .position(startX() + entryWidth / 4, y)
                    .size(entryWidth / 2, 20)
                    .build());
        }

        @Override
        public void close() {
            Objects.requireNonNull(client).setScreen(_super);
        }

        @Override
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            renderBackground(drawContext, mouseX, mouseY, delta);
            super.render(drawContext, mouseX, mouseY, delta);
        }

        @Override
        protected double maxScroll() {
            return (entryHeight + padding) * count - Math.max(height * 0.80, 0);
        }
    }
}
