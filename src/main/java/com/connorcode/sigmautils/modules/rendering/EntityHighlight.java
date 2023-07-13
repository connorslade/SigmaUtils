package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.DynamicListSetting;
import com.connorcode.sigmautils.config.settings.list.SimpleList;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.EntityRender;
import com.connorcode.sigmautils.misc.Components;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.mixin.ScreenAccessor;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.modules.meta.Padding.getPadding;

@ModuleInfo(description = "Outlines entities through blocks")
public class EntityHighlight extends Module {
    public static BoolSetting disableF1 = new BoolSetting(EntityHighlight.class, "Disable on F1").value(true)
            .description("Turns off when in F1 mode")
            .build();
    public static DynamicListSetting<GlowingEntity> entities =
            new DynamicListSetting<>(EntityHighlight.class, "Glowing Entities",
                    GlowingEntitySelectorManager::new).value(new GlowingEntity[]{
                    new GlowingEntity(EntityType.PLAYER, -1)
            }).description("Edit which entities are highlighted and what color.").build();

    public static boolean isGlowing(Entity entity) {
        return entities.value().stream().anyMatch(glowingEntity -> glowingEntity.type.equals(entity.getType()));
    }

    public static Optional<Integer> getGlowingColor(Entity entity) {
        return entities.value()
                .stream()
                .filter(glowingEntity -> glowingEntity.type.equals(entity.getType()))
                .findFirst()
                .map(glowingEntity ->
                        glowingEntity.color == -1 ? entity.getTeamColorValue() : getColor(glowingEntity.color));
    }

    private static int getColor(int index) {
        var color = TextStyle.Color.values()[index].asHexColor();
        return TextRenderer.tweakTransparency(color);
    }

    @EventHandler
    void onEntityHighlightEvent(EntityRender.EntityHighlightEvent event) {
        if (!isGlowing(event.getEntity()) || (disableF1.value() && client.options.hudHidden)) return;
        getGlowingColor(event.getEntity()).ifPresent(event::setColor);
        event.setHasOutline(true);
    }

    static class GlowingEntity {
        EntityType<?> type;
        // hex color
        int color;

        public GlowingEntity(NbtCompound nbt) {
            this.type = Registries.ENTITY_TYPE.get(Identifier.tryParse(nbt.getString("type")));
            this.color = nbt.getInt("color");
        }

        public GlowingEntity(EntityType<?> type, int color) {
            this.type = type;
            this.color = color;
        }

        public void nextColor() {
            var colors = TextStyle.Color.values().length;
            this.color += Screen.hasShiftDown() ? -1 : 1;
            if (this.color >= colors) this.color = -1;
            if (this.color < -1) this.color = colors - 1;
        }

        public NbtCompound toNbt() {
            var nbt = new NbtCompound();
            nbt.putString("type", Registries.ENTITY_TYPE.getId(type).toString());
            nbt.putInt("color", color);
            return nbt;
        }

        public String getName() {
            return type.getName().getString();
        }
    }

    static class GlowingEntitySelectorManager implements DynamicListSetting.ResourceManager<GlowingEntity> {
        DynamicListSetting<GlowingEntity> setting;

        GlowingEntitySelectorManager(DynamicListSetting<GlowingEntity> setting) {
            this.setting = setting;
        }

        @Override
        public List<GlowingEntity> getAllResources() {
            return Registries.ENTITY_TYPE.stream().map(e -> new GlowingEntity(e, -1)).toList();
        }

        @Override
        public String getDisplay(GlowingEntity resource) {
            return resource.getName();
        }

        @Override
        public void render(GlowingEntity resource, Screen screen, int x, int y) {
            var padding = getPadding();
            if (resource.color == -1) {
                Util.addChild(screen, ButtonWidget.builder(Text.of("T"), button -> {
                    resource.nextColor();
                    ((ScreenAccessor) screen).invokeClearAndInit();
                }).position(x + 20 + padding, y).size(20, 20).build());
            } else {
                Util.addChild(screen, new Components.DrawableElement() {
                    @Override
                    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
                        drawContext.fill(x + 20 + padding, y + padding, x - padding + 40, y - padding + 20,
                                getColor(resource.color));
                    }

                    @Override
                    public boolean mouseClicked(double mouseX, double mouseY, int button) {
                        if (mouseX < x + 20 + padding || mouseX > x - padding + 40 || mouseY < y + padding ||
                                mouseY > y - padding + 20) return false;
                        resource.nextColor();
                        client.getSoundManager()
                                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        ((ScreenAccessor) screen).invokeClearAndInit();
                        return true;
                    }
                });
            }
            SimpleList.render(entities, resource, getDisplay(resource), screen, x, y, 20);
        }

        @Override
        public boolean renderSelector(GlowingEntity resource, Screen screen, int x, int y) {
            if (entities.value().stream().anyMatch(s -> s.type.equals(resource.type))) return false;
            SimpleList.selector(entities, resource, getDisplay(resource), screen, x, y, 0);
            return true;
        }

        @Override
        public NbtElement serialize(List<GlowingEntity> resources) {
            if (resources == null) return null;
            var resourceList = new NbtList();
            resourceList.addAll(resources.stream().map(GlowingEntity::toNbt).toList());
            return resourceList;
        }

        @Override
        public List<GlowingEntity> deserialize(NbtElement nbt) {
            if (!(nbt instanceof NbtList resourceList)) return null;
            var list = resourceList.stream().map(n -> new GlowingEntity((NbtCompound) n)).toList();
            return new ArrayList<>(list);
        }
    }
}
