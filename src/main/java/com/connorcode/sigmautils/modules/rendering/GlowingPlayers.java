package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.config.ConfigGui.getPadding;

public class GlowingPlayers extends Module {
    public static BoolSetting disableF1 = new BoolSetting(GlowingPlayers.class, "Disable F1").value(true)
            .description("Turns off when in F1 mode")
            .build();

    public GlowingPlayers() {
        super("glowing_players", "Glowing Players", "Makes all players glow!", Category.Rendering);
    }

    public static boolean renderGlowing(Entity entity) {
        return entity instanceof PlayerEntity;
    }

    public static DynamicListSetting<EntityType<?>> selector =
            new DynamicListSetting<>(GlowingPlayers.class, "Glowing Entities",
                    new DynamicListSetting.ResourceManager<EntityType<?>>() {
                        @Override
                        public List<EntityType<?>> getAllResources() {
                            return Registry.ENTITY_TYPE.stream().toList();
                        }

                        @Override
                        public int render(EntityType<?> resource, Screen screen, int x, int y) {
                            return 0;
                        }

                        @Override
                        public int renderSelector(EntityType<?> resource, Screen screen, int x, int y) {
                            if (selector.value().contains(resource)) return 0;
                            var padding = getPadding();
                            Util.addChild(screen, new ButtonWidget(x, y, 20, 20, Text.of("+"), button -> {
                                selector.add(resource);
                                ((ScreenAccessor) screen).invokeClearAndInit();
                            }));
                            Util.addDrawable(screen,
                                    (matrices, mouseX, mouseY, delta) -> client.textRenderer.draw(matrices,
                                            resource.getName().getString(), x + 20 + padding,
                                            y + padding / 2f + 10 - client.textRenderer.fontHeight / 2f, 0xFFFFFF));
                            return 20 + getPadding();
                        }

                        @Override
                        public NbtCompound serialize(List<EntityType<?>> resources) {
                            if (resources == null) return null;
                            var resourceList = new NbtList() {{
                                addAll(resources.stream()
                                        .map(Registry.ENTITY_TYPE::getId)
                                        .map(i -> NbtString.of(i.toString()))
                                        .toList());
                            }};

                            var nbt = new NbtCompound();
                            nbt.put("resources", resourceList);
                            return nbt;
                        }

                        @Override
                        public List<EntityType<?>> deserialize(NbtCompound nbt) {
                            if (!nbt.contains("resources")) return List.of();
                            var resourceList = nbt.getList("resources", 8);
                            var list = resourceList.stream()
                                    .map(NbtString.class::cast)
                                    .map(s -> Identifier.tryParse(s.asString()))
                                    .map(Registry.ENTITY_TYPE::get)
                                    .toList();
                            return new ArrayList<>(list);
                        }
                    }).build();





    /*
    Entity Groups:
        - Player
        - Mob
        - Passive
        - Projectile
        - Vehicle
        - Item
        - Misc
     */
}
