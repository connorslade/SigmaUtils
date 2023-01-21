package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
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
        var data = this.manager.deserialize(nbt.getCompound(this.id));
        if (data == null) return;
        this.resources = data;
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        Util.addChild(screen, new ButtonWidget(x, y, width, 20, Text.of("Edit " + this.id), button -> {
            client.setScreen(new ResourceScreen<>(screen, this.manager));
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
        int render(T resource, Screen data, int x, int y);

        // Render the add resource line
        int renderSelector(T resource, Screen data, int x, int y);

        // Save the active resources to NBT
        NbtCompound serialize(List<T> resources);

        // Load the active resources from NBT
        List<T> deserialize(NbtCompound nbt);
    }

    public static class ResourceScreen<T> extends Screen {
        Screen _super;
        ResourceManager<T> renderer;

        int padding;

        protected ResourceScreen(Screen _super, ResourceManager<T> renderer) {
            super(Text.of("Resource Screen"));
            this.renderer = renderer;
            this._super = _super;
            this.padding = getPadding();
        }

        @Override
        protected void init() {
            int y = padding * 4;

            var text = "+ Add";
            var textWidth = Objects.requireNonNull(client).textRenderer.getWidth(text);
            addDrawableChild(new ButtonWidget(20 + padding, y, textWidth + padding * 8, 20, Text.of(text),
                    button -> Objects.requireNonNull(client).setScreen(new ResourceAddScreen<>(this, this.renderer))));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void close() {
            Objects.requireNonNull(client).setScreen(_super);
        }
    }

    public static class ResourceAddScreen<T> extends ResourceScreen<T> {
        protected ResourceAddScreen(Screen _super, ResourceManager<T> renderer) {
            super(_super, renderer);
        }

        @Override
        protected void init() {
            int y = padding * 4;

            for (var i : renderer.getAllResources()) {
                y += renderer.renderSelector(i, this, 20 + padding, y);
            }
        }
    }
}
