package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;

public class ConfirmDisconnect extends Module {
    public ConfirmDisconnect() {
        super("confirm_disconnect", "Confirm Disconnect", "Requires you to confirm your server disconnects.",
                Category.Interface);
    }

    public static class ConfirmDisconnectScreen extends Screen {
        public ConfirmDisconnectScreen() {
            super(Text.of("Disconnect?"));
        }

        @Override
        protected void init() {
            this.addDrawableChild(ButtonWidget.builder(Text.of("Disconnect"), button -> {
                assert this.client != null;
                assert this.client.world != null;
                boolean singlePlayer = client.isInSingleplayer();
                boolean realms = client.isConnectedToRealms();

                client.world.disconnect();
                if (singlePlayer)
                    client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
                else client.disconnect();

                TitleScreen titleScreen = new TitleScreen();
                if (singlePlayer) client.setScreen(titleScreen);
                else if (realms) client.setScreen(new RealmsMainScreen(titleScreen));
                else client.setScreen(new MultiplayerScreen(titleScreen));
            }).position(this.width / 2 - 122, this.height / 4 - 10).size(120, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.of("Back"), button -> {
                assert client != null;
                client.setScreen(null);
            }).position(this.width / 2 + 2, this.height / 4 - 10).size(120, 20).build());
        }

        @Override
        public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
            assert client != null;
            matrices.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
            Text text = Text.of("Do you want to disconnect?");
            matrices.drawText(client.textRenderer, text.asOrderedText(),
                    this.width / 2 - textRenderer.getWidth(text) / 2,
                    this.height / 4 - 20 - textRenderer.fontHeight / 2, 16777215, false);
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
