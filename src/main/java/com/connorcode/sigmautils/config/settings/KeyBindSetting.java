package com.connorcode.sigmautils.config.settings;

import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class KeyBindSetting extends Setting<KeyBindSetting> {
    InputUtil.Key key;
    boolean shift, ctrl, alt;
    boolean editing = false;
    ButtonWidget buttonWidget = null;

    public <T extends Module>  KeyBindSetting(Class<T> module, String name) {
        super(module, name);
    }

    public KeyBindSetting value(InputUtil.Key key) {
        this.key = key;
        return this;
    }

    public KeyBindSetting shift() {
        this.shift = true;
        return this;
    }

    public KeyBindSetting ctrl() {
        this.ctrl = true;
        return this;
    }

    public KeyBindSetting alt() {
        this.alt = true;
        return this;
    }

    Text getBind() {
        if (key == null) return Text.of("Not bound");
        MutableText bind = (MutableText) key.getLocalizedText();
        if (shift) bind.append(Text.of(" + Shift"));
        if (ctrl) bind.append(Text.of(" + Ctrl"));
        if (alt) bind.append(Text.of(" + Alt"));
        return bind;
    }

    @Override
    public void serialize(NbtCompound nbt) {
        if (key == null) return;
        NbtCompound keybind = new NbtCompound();
        keybind.putInt("key", key.getCode());
        if (shift) keybind.putBoolean("shift", true);
        if (ctrl) keybind.putBoolean("ctrl", true);
        if (alt) keybind.putBoolean("alt", true);
        nbt.put("keybind", keybind);
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        if (!nbt.contains("keybind")) return;
        NbtCompound keybind = nbt.getCompound("keybind");
        key = InputUtil.fromKeyCode(keybind.getInt("key"), keybind.getBoolean("shift") ? 1 : 0);
        shift = keybind.getBoolean("shift");
        ctrl = keybind.getBoolean("ctrl");
        alt = keybind.getBoolean("alt");
    }

    @Override
    public int initRender(Screen screen, int x, int y, int width) {
        buttonWidget = new ButtonWidget(x, y, width, 20, this.getBind(), (button) -> {
            button.setMessage(Text.of("..."));
            editing = true;
        });

        Util.addDrawable(screen, buttonWidget);
        return 20;
    }

    @Override
    public void render(RenderData data, int x, int y) {

    }

    @Override
    // TODO: Fix this- some wacky caching issue??? (aka i need s;eep)
    public boolean onKeypress(int key, int scanCode, int modifiers) {
        if (!editing) return false;
        editing = false;
        System.out.printf("Key: %d, ScanCode: %d, Modifiers: %d\n", key, scanCode, modifiers);
        if (key == 340 || key == 341 || key == 342) return true;
        this.key = InputUtil.fromKeyCode(key, scanCode);
        System.out.println(this.key);
        this.shift = (modifiers & 1) == 1;
        this.ctrl = (modifiers >> 1 & 1) == 1;
        this.alt = (modifiers >> 2 & 1) == 1;
        System.out.printf("Shift: %b, Ctrl: %b, Alt: %b\n", shift, ctrl, alt);
        buttonWidget.setMessage(this.getBind());
        ((ScreenAccessor)MinecraftClient.getInstance().currentScreen).invokeClearAndInit();;
        return true;
    }

    @Override
    public void onClose() {
        buttonWidget = null;
    }
}
