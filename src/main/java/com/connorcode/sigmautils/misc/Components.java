package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.connorcode.sigmautils.modules.meta.Scale.getScale;

public class Components {
    public static void addToggleButton(Screen screen, Module module, int x, int y, int width, boolean mini) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        Util.addDrawable(screen, new MultiClickButton(x, y, width, 20,
                Text.of(String.format("%s█§r%s", module.enabled ? "§a" : "§c",
                        mini ? "" : String.format(" %s", module.name))), button -> {
            if (button.click == 1) {
                if (!Config.moduleSettings.containsKey(module.getClass())) return;
                module.openConfigScreen(MinecraftClient.getInstance(), screen);
                return;
            }
            module.enabled ^= true;
            MinecraftClient client = MinecraftClient.getInstance();
            if (module.enabled) module.enable(client);
            else module.disable(client);
            sa.invokeClearAndInit();
        }, ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices, sa.getTextRenderer()
                .wrapLines(Text.of(module.description), 200), mouseX, mouseY))));
    }

    public static void sliderConfig(MinecraftClient client, Screen screen, int x, int y, Module module, NumberSetting setting) {
        Components.addToggleButton(screen, module, x, y, 20, true);
        setting.initRender(screen, () -> Text.of(String.format("%s: %." + setting.getPrecision() + "f", module.name, setting.value())), x, y, 130, 20);
    }

    // A button that detects let and right clicks (and is scalable)
    public static class MultiClickButton extends ButtonWidget {
        private final PressAction onPress;
        public int click = 0;

        public MultiClickButton(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier) {
            super(x, y, width, height, message, null, tooltipSupplier);
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            this.onPress.onPress(this);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            float scale = getScale();
            super.onClick(mouseX / scale, mouseY / scale);
        }

        @Override
        protected boolean clicked(double mouseX, double mouseY) {
            float scale = getScale();
            return super.clicked(mouseX / scale, mouseY / scale);
        }

        @Override
        protected boolean isValidClickButton(int button) {
            click = button;
            return button == 0 || button == 1;
        }

        public interface PressAction {
            void onPress(MultiClickButton button);
        }
    }

    public static abstract class TooltipSlider extends SliderWidget {
        public TooltipSlider(int x, int y, int width, int height, Text text, double value) {
            super(x, y, width, height, text, value);
        }

        protected Text getTooltip() {
            return null;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            super.render(matrices, mouseX, mouseY, delta);
            if (!this.hovered) return;
            Text tooltip = this.getTooltip();
            if (tooltip == null) return;

            MinecraftClient client = MinecraftClient.getInstance();
            Objects.requireNonNull(client.currentScreen)
                    .renderOrderedTooltip(matrices, client.textRenderer.wrapLines(tooltip, 200), mouseX,
                            mouseY);
        }
    }
}
