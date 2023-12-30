package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.config.Config;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;


public class Components {
    public static void addToggleButton(Screen screen, Module module, int x, int y, int width, boolean mini) {
        //        boolean hasConfig = Config.moduleSettings.getOrDefault(module.getClass(), List.of()).size() > 1;
        ScreenAccessor sa = (ScreenAccessor) screen;
        Util.addChild(screen, new MultiClickButton(x, y, width, 20,
                                                   Text.of(String.format("%s█§r%s", module.enabled ? "§a" : "§c",
                                                                         mini ? "" : String.format(" %s", module.name))), button -> {
            if (button.click == 1) {
                if (Config.moduleSettings.get(module.getClass()).size() <= 1)
                    return;
                module.openConfigScreen(client, screen);
                return;
            }
            if (!module.enabled) module.enable();
            else module.disable();
            sa.invokeClearAndInit();
        }, ((button, matrices, mouseX, mouseY) -> matrices.drawOrderedTooltip(sa.getTextRenderer(), sa.getTextRenderer()
                        .wrapLines(Text.of(module.description + (module.inDevelopment ? " [EXPERIMENTAL]" : "")), 200), mouseX,
                mouseY))));
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

        var button = ButtonWidget.builder(Text.of(String.valueOf(values[setting.index()])), b -> {
            setting.value(setting.values()[(setting.index() + 1) % setting.values().length]);
            sa.invokeClearAndInit();
        }).position(130 + x, y).size(20, 20).tooltip(Util.nullMap(setting.getDescription(), Tooltip::of)).build();
        Util.addChild(screen, button);
    }

    public abstract static class DrawableElement implements Drawable, Element, Selectable {
        protected boolean focused;

        @Override
        public SelectionType getType() {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
        }

        @Override
        public boolean isFocused() {
            return focused;
        }

        @Override
        public void setFocused(boolean focused) {
            this.focused = focused;
        }
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
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            var xStart = startX();
            drawContext.fill(xStart, 0, xStart + entryWidth, height, 0x80000000);
            var drawables = ((ScreenAccessor) this).getDrawables();
            for (var i : drawables)
                i.render(drawContext, mouseX, mouseY, delta);
        }

        protected abstract double maxScroll();

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            scroll -= verticalAmount * (entryHeight + padding);
            refreshScrollConstrains();
            this.clearAndInit();
            return true;
        }

        protected void refreshScrollConstrains() {
            scroll = MathHelper.clamp(scroll, 0, maxScroll());
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
        private final MultiClickButton.PressAction onPress;
        private final TooltipSupplier tooltipSupplier;
        public int click = 0;

        public MultiClickButton(int x, int y, int width, int height, Text message, MultiClickButton.PressAction onPress, TooltipSupplier tooltipSupplier) {
            super(x, y, width, height, message, null, DEFAULT_NARRATION_SUPPLIER);
            this.tooltipSupplier = tooltipSupplier;
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            this.onPress.onPress(this);
        }

        @Override
        protected boolean isValidClickButton(int button) {
            click = button;
            return button == 0 || button == 1;
        }

        @Override
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            super.render(drawContext, mouseX, mouseY, delta);
            if (this.hovered) tooltipSupplier.onTooltip(this, drawContext, mouseX, mouseY);
            super.render(drawContext, mouseX, mouseY, delta);
        }

        public interface PressAction {
            void onPress(MultiClickButton button);
        }

        public interface TooltipSupplier {
            void onTooltip(MultiClickButton button, DrawContext drawContext, int mouseX, int mouseY);
        }
    }

    // TODO: See if this is even needed anymore
    public static abstract class TooltipSlider extends SliderWidget {
        public TooltipSlider(int x, int y, int width, int height, Text text, double value) {
            super(x, y, width, height, text, value);
        }

        protected Text tooltip() {
            return null;
        }

        @Override
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            super.render(drawContext, mouseX, mouseY, delta);
            if (!this.hovered)
                return;
            Text tooltip = this.tooltip();
            if (tooltip == null)
                return;

            drawContext.drawOrderedTooltip(client.textRenderer, client.textRenderer.wrapLines(tooltip, 200), mouseX,
                    mouseY);
        }
    }

    public static class EventCheckbox extends CheckboxWidget {
        PressAction onPress;
        TooltipSupplier tooltipSupplier;

        public EventCheckbox(int x, int y, int width, int height, Text message, boolean checked, TextRenderer renderer, PressAction onPress, TooltipSupplier tooltipSupplier) {
            super(x, y, message, renderer, checked, Callback.EMPTY);
            this.width = width;
            this.height = height;
            this.onPress = onPress;
            this.tooltipSupplier = tooltipSupplier;
        }

        @Override
        public void onPress() {
            super.onPress();
            this.onPress.onPress(this);
        }

        public void renderTooltip(DrawContext drawContext, int mouseX, int mouseY) {
            tooltipSupplier.onTooltip(this, drawContext, mouseX, mouseY);
        }

        @Override
        public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            super.renderWidget(drawContext, mouseX, mouseY, delta);
            if (this.isSelected())
                this.renderTooltip(drawContext, mouseX, mouseY);
        }

        public interface PressAction {
            void onPress(EventCheckbox button);
        }

        public interface TooltipSupplier {
            void onTooltip(EventCheckbox button, DrawContext drawContext, int mouseX, int mouseY);
        }
    }
}
