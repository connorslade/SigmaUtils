package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import static com.connorcode.sigmautils.modules.meta.Scale.getScale;

public class Components {
    public static void addToggleButton(Screen screen, Module module, int x, int y, int width, boolean mini) {
        ScreenAccessor sa = (ScreenAccessor) screen;
        Util.addDrawable(screen, new ScalableButton(x, y, width, 20,
                Text.of(String.format("%s█§r%s", module.enabled ? "§a" : "§c",
                        mini ? "" : String.format(" %s", module.name))), button -> {
            module.enabled ^= true;
            MinecraftClient client = MinecraftClient.getInstance();
            if (module.enabled) module.enable(client);
            else module.disable(client);
            sa.invokeClearAndInit();
        }, ((button, matrices, mouseX, mouseY) -> screen.renderOrderedTooltip(matrices, sa.getTextRenderer()
                .wrapLines(Text.of(module.description), 200), mouseX, mouseY))));
    }

    public static class ScalableButton extends ButtonWidget {
        public ScalableButton(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier) {
            super(x, y, width, height, message, onPress, tooltipSupplier);
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
    }
}
