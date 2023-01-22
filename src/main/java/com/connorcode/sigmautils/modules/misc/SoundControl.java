package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;
import static com.connorcode.sigmautils.config.settings.list.SimpleList.selector;

public class SoundControl extends Module {
    static DynamicListSetting<SoundSettings> soundEvents =
            new DynamicListSetting<>(SoundControl.class, "Sound Events", SoundEventManager::new).build();

    public SoundControl() {
        super("sound_control", "Sound Control",
                "Lets you control the volume of every sound event. Ground Control To Major Tom.", Category.Misc);
    }

    static class SoundEventManager implements DynamicListSetting.ResourceManager<SoundSettings> {
        protected DynamicListSetting<SoundSettings> setting;

        Registry<SoundEvent> registry = Registry.SOUND_EVENT;

        SoundEventManager(DynamicListSetting<SoundSettings> setting) {
            this.setting = setting;
        }

        public String getDisplay(SoundSettings value) {
            return Objects.requireNonNull(registry.getId(value.event)).toString();
        }

        @Override
        public List<SoundSettings> getAllResources() {
            return registry.stream().map(SoundSettings::new).toList();
        }

        @Override
        public void render(SoundSettings resource, Screen screen, int x, int y) {
            var padding = getPadding();
            Util.addChild(screen, new ButtonWidget(x, y, 20, 20, Text.of("×"), button -> {
                setting.remove(resource);
                ((ScreenAccessor) screen).invokeClearAndInit();
            }, ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices,
                    List.of(Text.of("Remove element").asOrderedText()), mouseX, mouseY))));
//                Util.addChild(screen, new SliderWidget(x + 20 + padding, y, 100, 20, ) {
//                    @Override
//                    protected void updateMessage() {
//                        this.setMessage(Text.of(String.format("%.2f", this.value)));
//                    }
//
//                    @Override
//                    protected void applyValue() {
//
//                    }
//                });
            Util.addDrawable(screen,
                    (matrices, mouseX, mouseY, delta) -> client.textRenderer.draw(matrices, getDisplay(resource),
                            x + 20 + padding * 4, y + padding / 2f + 10 - client.textRenderer.fontHeight / 2f,
                            0xffffff));
        }

        @Override
        public boolean renderSelector(SoundSettings resource, Screen screen, int x, int y) {
            if (setting.value().stream().anyMatch(s -> s.event.equals(resource.event))) return false;
            selector(setting, resource, getDisplay(resource), screen, x, y, 0);
            return true;
        }

        @Override
        public NbtElement serialize(List<SoundSettings> resources) {
            return new NbtList() {{
                addAll(resources.stream().map(SoundSettings::toNbt).toList());
            }};
        }

        @Override
        public List<SoundSettings> deserialize(NbtElement nbt) {
            if (!(nbt instanceof NbtList resourceList)) return null;
            var list = resourceList.stream().map(e -> (NbtCompound) e).map(SoundSettings::new).toList();
            return new ArrayList<>(list);
        }

        @Override
        public int width() {
            return 300;
        }
    }

    static class SoundSettings {
        SoundEvent event;
        float volume;

        SoundSettings(NbtCompound nbt) {
            this.event = Registry.SOUND_EVENT.get(Identifier.tryParse(nbt.getString("event")));
            this.volume = nbt.getFloat("volume");
        }

        SoundSettings(SoundEvent event) {
            this.event = event;
            this.volume = 1f;
        }

        NbtCompound toNbt() {
            var nbt = new NbtCompound();
            nbt.putString("event", Objects.requireNonNull(Registry.SOUND_EVENT.getId(event)).toString());
            nbt.putFloat("volume", volume);
            return nbt;
        }
    }
}
