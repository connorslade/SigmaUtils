package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.settings.list.SimpleList.selector;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;

@ModuleInfo(description = "Lets you control the volume of every sound event.")
public class SoundControl extends Module {
    static DynamicListSetting<SoundSettings> soundEvents =
            new DynamicListSetting<>(SoundControl.class, "Sound Events", SoundEventManager::new).build();

    public static float getVolumeAdjuster(Identifier id) {
        return soundEvents.value().stream()
                .filter(soundSettings -> soundSettings.event.getId().equals(id))
                .findFirst().map(soundSettings -> soundSettings.volume).orElse(1f);
    }

    static class SoundEventManager implements DynamicListSetting.ResourceManager<SoundSettings> {
        protected DynamicListSetting<SoundSettings> setting;

        Registry<SoundEvent> registry = Registries.SOUND_EVENT;

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
            Util.addChild(screen, ButtonWidget.builder(Text.of("×"), button -> {
                setting.remove(resource);
                ((ScreenAccessor) screen).invokeClearAndInit();
            }).position(x, y).size(20, 20).tooltip(Tooltip.of(Text.of("Remove element"))).build());
            Util.addChild(screen,
                    new SliderWidget(x + 20 + padding, y, 100, 20, getSliderText(resource), resource.volume) {
                        @Override
                        protected void updateMessage() {
                            this.setMessage(getSliderText(resource));
                        }

                        @Override
                        protected void applyValue() {
                            resource.volume = (float) this.value;
                        }
                    });
            Util.addDrawable(screen,
                    (matrices, mouseX, mouseY, delta) ->
                            matrices.drawText(client.textRenderer, getDisplay(resource),
                                    x + 120 + padding * 4,
                                    (int) (y + padding / 2f + 10 - client.textRenderer.fontHeight / 2f),
                                    0xffffff, false)
            );
        }

        Text getSliderText(SoundSettings resource) {
            if (resource.volume == 0) return Text.of("Off");
            return Text.of(String.format("%.2f", resource.volume));
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
            return 400;
        }
    }

    static class SoundSettings {
        SoundEvent event;
        float volume;

        SoundSettings(NbtCompound nbt) {
            this.event = Registries.SOUND_EVENT.get(Identifier.tryParse(nbt.getString("event")));
            this.volume = nbt.getFloat("volume");
        }

        SoundSettings(SoundEvent event) {
            this.event = event;
            this.volume = 1f;
        }

        NbtCompound toNbt() {
            var nbt = new NbtCompound();
            nbt.putString("event", Objects.requireNonNull(Registries.SOUND_EVENT.getId(event)).toString());
            nbt.putFloat("volume", volume);
            return nbt;
        }
    }
}
