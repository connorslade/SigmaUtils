package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public abstract class Setting<T extends Setting<T>> {
    protected final Class<? extends Module> module;
    protected String name;
    protected String id;
    protected String category = "General";
    protected String description;

    public Setting(Class<? extends Module> module, String name) {
        this.id = Util.toSnakeCase(name);
        this.module = module;
        this.name = name;
    }

    public T id(String id) {
        this.id = id;
        return (T) this;
    }

    public T category(String category) {
        this.category = category;
        return (T) this;
    }

    public T description(String description) {
        this.description = description;
        return (T) this;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    @Nullable
    public Text getDescription() {
        return this.description == null ? null : Text.of(this.description);
    }

    public abstract void serialize(NbtCompound nbt);

    public abstract void deserialize(NbtCompound nbt);

    public abstract void initRender(Screen screen, int x, int y, int width, int height);

    public abstract void render(RenderData data, int x, int y);

    public record RenderData(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }
}