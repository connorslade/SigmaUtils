package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;


public class DynamicListSetting<K> extends Setting<DynamicListSetting<K>> {
    ResourceManager<K> manager;
    List<K> resources = new ArrayList<>();

    public DynamicListSetting(Class<? extends Module> module, String name, ResourceManager<K> manager) {
        super(module, name);
        this.manager = manager;
    }

    DynamicListSetting<K> value(K[] resources) {
        this.resources = List.of(resources);
        return this;
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
        Util.addChild(screen, new ButtonWidget(x, y, width, 20, Text.of("Edit " + this.name), button -> {
            client.setScreen(new ResourceScreen(screen, this.manager));
        }, ((button, matrices, mouseX, mouseY) -> {
            if (this.description == null) return;
            screen.renderOrderedTooltip(matrices, client.textRenderer.wrapLines(getDescription(), 200), mouseX, mouseY);
        })));

        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {}

    public interface ResourceManager<T> {
        // Get all selectable resources
        List<T> getAllResources();

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
    }

    public static class ResourceAddScreen<K> extends Components.ScrollableScreen {
        Screen _super;
        ResourceManager<K> renderer;

        protected ResourceAddScreen(Screen _super, ResourceManager<K> renderer) {
            super(Text.of("Resource Screen"), getPadding(), renderer.height(), renderer.width());
            this.renderer = renderer;
            this._super = _super;
        }

        @Override
        protected void init() {
            var y = padding * 4;
            var x = 20 + padding + startX();

            for (var i : renderer.getAllResources()) {
                if (!renderer.renderSelector(i, this, x, y)) continue;
                y += entryHeight + padding;
            }
        }

        @Override
        public void close() {
            Objects.requireNonNull(client).setScreen(_super);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    public class ResourceScreen extends Components.ScrollableScreen {
        Screen _super;
        ResourceManager<K> renderer;

        protected ResourceScreen(Screen _super, ResourceManager<K> renderer) {
            super(Text.of("Resource Screen"), getPadding(), renderer.height(), renderer.width());
            this.renderer = renderer;
            this._super = _super;
        }

        @Override
        protected void init() {
            var x = 20 + padding + startX();
            var y = padding * 4;

            for (var i : DynamicListSetting.this.resources) {
                renderer.render(i, this, x, y);
                y += entryHeight + padding;
            }

            addDrawableChild(new ButtonWidget(startX() + entryWidth / 4, y, entryWidth / 2, 20, Text.of("+ Add"),
                    button -> Objects.requireNonNull(client).setScreen(new ResourceAddScreen<>(this, this.renderer))));
        }

        @Override
        public void close() {
            Objects.requireNonNull(client).setScreen(_super);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
