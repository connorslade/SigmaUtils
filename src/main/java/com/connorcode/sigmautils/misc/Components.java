package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

import static com.connorcode.sigmautils.config.ConfigGui.getPadding;
import static com.connorcode.sigmautils.modules.meta.Scale.getScale;

public class Components {
    public static void addToggleButton(Screen screen, Module module, int x, int y, int width, boolean mini) {
//        boolean hasConfig = Config.moduleSettings.getOrDefault(module.getClass(), List.of()).size() > 1;
        ScreenAccessor sa = (ScreenAccessor) screen;
        Util.addChild(screen, new MultiClickButton(x, y, width, 20,
                Text.of(String.format("%s█§r%s", module.enabled ? "§a" : "§c",
                        mini ? "" : String.format(" %s", module.name))), button -> {
            if (button.click == 1) {
                if (Config.moduleSettings.get(module.getClass())
                        .size() <= 1) return;
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

    public static void sliderConfig(Screen screen, int x, int y, Module module, NumberSetting setting) {
        int padding = getPadding();
        Components.addToggleButton(screen, module, x, y, 20, true);
        setting.initRender(screen,
                () -> Text.of(String.format("%s: %." + setting.getPrecision() + "f", module.name, setting.value())),
                x + 20 + padding, y, 130 - padding);
    }

    public static <K extends Enum<?>> void enumConfig(Screen screen, int x, int y, EnumSetting<K> setting, char[] values) {
        ScreenAccessor sa = (ScreenAccessor) screen;

        Util.addChild(screen,
                new ButtonWidget(x + 130, y, 20, 20, Text.of(String.valueOf(values[setting.index()])), button -> {
                    setting.value(setting.values()[(setting.index() + 1) % setting.values().length]);
                    sa.invokeClearAndInit();
                }, (((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices, sa.getTextRenderer()
                        .wrapLines(setting.getDescription(), 200), mouseX, mouseY)))));
    }

    public abstract static class DrawableElement implements Drawable, Element, Selectable {
        @Override
        public SelectionType getType() {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}
    }

    public abstract static class ScrollableScreen extends Screen {
        protected int padding;
        protected int entryHeight;
        protected int entryWidth;
        protected double scroll;

        protected ScrollableScreen(Text title, int padding, int entryHeight, int entryWidth) {
            super(title);
            this.padding = padding;
            this.entryHeight = entryHeight;
            this.entryWidth = entryWidth;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            var xStart = startX();
            fill(matrices, xStart, 0, xStart + entryWidth, height, 0x80000000);
            var drawables = ((ScreenAccessor) this).getDrawables();
            for (var i : drawables) i.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            scroll -= amount * (entryHeight + padding);
            refreshScrollConstrains();
            this.clearAndInit();
            return true;
        }

        protected void refreshScrollConstrains() {
            scroll = MathHelper.clamp(scroll, 0,
                    Math.max(0, ((ScreenAccessor) this).getDrawables().size() * (entryHeight + padding) - height));
            // TODO: Fix this. Allow for scrolling to the bottom.
        }

        protected int startX() {
            return (width - entryWidth) / 2;
        }

        protected int scrollOffset() {
            return 0;
        }
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
                    .renderOrderedTooltip(matrices, client.textRenderer.wrapLines(tooltip, 200), mouseX, mouseY);
        }
    }

    public static class EventCheckbox extends CheckboxWidget {
        PressAction onPress;
        TooltipSupplier tooltipSupplier;

        public EventCheckbox(int x, int y, int width, int height, Text message, boolean checked, PressAction onPress, TooltipSupplier tooltipSupplier) {
            super(x, y, width, height, message, checked);
            this.onPress = onPress;
            this.tooltipSupplier = tooltipSupplier;
        }

        @Override
        public void onPress() {
            super.onPress();
            this.onPress.onPress(this);
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            super.renderTooltip(matrices, mouseX, mouseY);
            tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            super.renderButton(matrices, mouseX, mouseY, delta);
            if (this.isHovered()) this.renderTooltip(matrices, mouseX, mouseY);
        }

        public interface PressAction {
            void onPress(EventCheckbox button);
        }

        public interface TooltipSupplier {
            void onTooltip(EventCheckbox button, MatrixStack matrices, int mouseX, int mouseY);
        }
    }
}
