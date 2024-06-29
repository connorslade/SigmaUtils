package com.connorcode.sigmautils.modules._interface;

import com.connorcode.sigmautils.SigmaUtils;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.list.SimpleList;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.connorcode.sigmautils.SigmaUtils.client;

@ModuleInfo(description = "Uses random textures for the background tessellation", documentation = "It will change every time you open a new screen.")
public class RandomBackground extends Module {
    static final List<String> validBackgrounds = new BufferedReader(new InputStreamReader(Objects.requireNonNull(SigmaUtils.class.getClassLoader()
            .getResourceAsStream("assets/sigma-utils/background_blocks.txt")))).lines().toList();
    static EnumSetting<Util.FilterType> filterType = Util.filterSetting(RandomBackground.class)
            .description("Whether to blacklist or whitelist background textures.").value(Util.FilterType.Blacklist)
            .build();
    static DynamicListSetting<Block> textures = new DynamicListSetting<>(RandomBackground.class, "Textures", BlockList::new).description("Possible background textures.")
            .build();
    static String asset;
    static int screenHash = -1;

    public static Identifier getTexture() {
        int currentScreenHash = Objects.requireNonNull(Objects.requireNonNull(client).currentScreen).hashCode();

        if (screenHash != currentScreenHash || asset == null) {
            screenHash = currentScreenHash;

            List<String> valid = switch (filterType.value()) {
                case Whitelist ->
                        textures.value().stream().map(block -> Registries.BLOCK.getId(block).getPath()).toList();
                case Blacklist -> validBackgrounds.stream().filter(s -> !textures.value()
                        .contains(Registries.BLOCK.get(Identifier.tryParse("minecraft:" + s)))).toList();
            };

            var assetIndex = new Random().nextInt(valid.size());
            asset = valid.get(assetIndex);
        }

        return Identifier.tryParse("textures/block/" + asset + ".png");
    }

    static class BlockList extends SimpleList<Block> {

        public BlockList(DynamicListSetting<Block> setting) {
            super(setting, Registries.BLOCK);
        }

        @Override
        public String getDisplay(Block value) {
            return value.getName().getString();
        }

        @Override
        public boolean renderSelector(Block resource, Screen screen, int x, int y) {
            var rawId = Registries.BLOCK.getId(resource).getPath();
            var id = Identifier.tryParse("textures/block/" + rawId + ".png");
            if (!RandomBackground.validBackgrounds.contains(rawId) || this.setting.value().contains(resource))
                return false;
            Util.addDrawable(screen, new Components.DrawableElement() {
                @Override
                public void render(DrawContext context, int mouseX, int mouseY, float delta) {
                    context.drawTexture(id, x - 1, y + 2, 0, 0.0F, 0.0F, 16, 16, 16, 16);
                }
            });
            return super.renderSelector(resource, screen, x + 16, y);
        }
    }
}
