package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Locale;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;
import static net.minecraft.client.util.InputUtil.*;


public class KeyBindSetting extends Setting<KeyBindSetting> {
    InputUtil.Key key;
    boolean shift, ctrl, alt, strict;
    boolean editing, pressed, showTitle;

    public <T extends Module> KeyBindSetting(Class<T> module, String name) {
        super(module, name);
    }

    @Override
    protected KeyBindSetting getThis() {
        return this;
    }

    public KeyBindSetting showTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    private boolean nonStrict() {
        if (this.shift && !Screen.hasShiftDown()) return false;
        if (this.ctrl && !Screen.hasControlDown()) return false;
        return !this.alt || Screen.hasAltDown();
    }

    public boolean pressed() {
        if (this.key == null || editing) return false;
        boolean nowPressed =
                InputUtil.isKeyPressed(client.getWindow().getHandle(), key.getCode()) &&
                        (strict ? (shift == Screen.hasShiftDown() && ctrl == Screen.hasControlDown() &&
                                alt == Screen.hasAltDown()) : nonStrict());

        if (nowPressed && !pressed) {
            pressed = true;
            return true;
        }
        if (!nowPressed) pressed = false;
        return false;
    }

    public Text getBind() {
        if (key == null) return Text.of("Not bound");
        String bind = key.getLocalizedText().getString().toUpperCase(Locale.ROOT);
        if (this.shift) bind += " + Shift";
        if (this.ctrl) bind += " + Ctrl";
        if (this.alt) bind += " + Alt";
        if (this.strict) bind += " (Strict)";
        return Text.of(bind);
    }

    @Override
    public void serialize(NbtCompound nbt) {
        if (this.key == null) return;
        NbtCompound keybind = new NbtCompound();
        keybind.putInt("key", this.key.getCode());
        if (this.strict) keybind.putBoolean("strict", true);
        if (this.shift) keybind.putBoolean("shift", true);
        if (this.ctrl) keybind.putBoolean("ctrl", true);
        if (this.alt) keybind.putBoolean("alt", true);
        nbt.put(this.id, keybind);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains(this.id)) return;
        NbtCompound keybind = nbt.getCompound(this.id);
        this.key = InputUtil.fromKeyCode(keybind.getInt("key"), 0);
        this.strict = keybind.getBoolean("strict");
        this.shift = keybind.getBoolean("shift");
        this.ctrl = keybind.getBoolean("ctrl");
        this.alt = keybind.getBoolean("alt");
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        var padding = getPadding();
        Util.addChild(screen,
                new Components.MultiClickButton(x, y + (showTitle ? padding + client.textRenderer.fontHeight : 0),
                        width, 20, this.getBind(),
                        (button) -> {
                            if (button.click == 1 && key != null) KeyBindSetting.this.strict ^= true;
                            else editing = true;
                        }, (((button, matrices, mouseX, mouseY) -> {
                    if (this.description == null) return;
                    matrices.drawOrderedTooltip(client.textRenderer,
                            client.textRenderer.wrapLines(getDescription(), 200), mouseX,
                            mouseY);
                }))) {
                    @Override
                    public Text getMessage() {
                        if (KeyBindSetting.this.editing) return Text.of("...");
                        return KeyBindSetting.this.getBind();
                    }
                });
        return 20 + (showTitle ? 2 * padding + client.textRenderer.fontHeight : 0);
    }

    @Override
    public void render(RenderData data, int x, int y) {
        if (!showTitle) return;
        data.drawContext().drawText(client.textRenderer, String.format("§f%s:", this.name), x, y, 0, false);
    }

    @Override
    public boolean onKeypress(int key, int scanCode, int modifiers) {
        // Define ignore keys (modifiers)
        final int[] ignored = new int[]{
                GLFW_KEY_LEFT_SHIFT,
                GLFW_KEY_RIGHT_SHIFT,
                GLFW_KEY_LEFT_CONTROL,
                GLFW_KEY_RIGHT_CONTROL,
                GLFW_KEY_LEFT_ALT,
                GLFW_KEY_RIGHT_ALT,
                GLFW_KEY_LEFT_SUPER,
                GLFW_KEY_RIGHT_SUPER,
        };

        if (!this.editing) return false;
        if (Arrays.stream(ignored).anyMatch(d -> d == key)) return true;
        if (key == GLFW_KEY_ESCAPE) {
            this.editing = false;
            this.key = null;
            this.shift = false;
            this.ctrl = false;
            this.alt = false;
            return true;
        }

        // Get and set the key data
        this.key = InputUtil.fromKeyCode(key, scanCode);
        this.shift = Screen.hasShiftDown();
        this.ctrl = Screen.hasControlDown();
        this.alt = Screen.hasAltDown();
        this.editing = false;
        return true;
    }

    @Override
    public void onClose() {
        this.editing = false;
    }
}
