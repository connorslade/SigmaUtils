package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.render.WorldRender;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.misc.util.Util;
import com.connorcode.sigmautils.misc.util.WorldRenderUtils;
import com.connorcode.sigmautils.module.DocumentedEnum;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static com.connorcode.sigmautils.SigmaUtils.client;
import static com.connorcode.sigmautils.misc.util.EntityUtils.entityPos;


@ModuleInfo(description = "Adds in-game titles to different *things*")
public class Titles extends Module {
    public static final BoolSetting tamableOwner =
            new BoolSetting(Titles.class, "Tamable Owner")
                    .description("Shows the owner of a tamable entity")
                    .category("Titles")
                    .build();
    private static final NumberSetting scale = new NumberSetting(Titles.class, "Scale", 0.5, 5).value(2).build();
    private static final NumberSetting yOffset = new NumberSetting(Titles.class, "Y Offset", 0, 3).value(0.25)
            .description("Number of blocks to offset the text from the entity")
            .build();
    private static final NumberSetting precision = new NumberSetting(Titles.class, "Precision", 0, 3).value(1)
            .description("Number of decimal places to show on decimal numbers")
            .precision(0)
            .build();
    private static final EnumSetting<TimeFormat> timeFormat =
            new EnumSetting<>(Titles.class, "Time Format", TimeFormat.class).description(
                    "The formatting used for displaying relative times.").value(TimeFormat.BestFit).build();
    private static final EnumSetting<TextStyle.Color> textColor =
            new EnumSetting<>(Titles.class, "Text Color", TextStyle.Color.class).description(
                    "The color of the text. (who would have thought)").value(TextStyle.Color.White).build();
    // == Titles ==
    private static final BoolSetting tntCountdown =
            new BoolSetting(Titles.class, "TNT Countdown").category("Titles")
                    .description("Shows the time until the TNT explodes.")
                    .build();
    private static final BoolSetting itemCountdown =
            new BoolSetting(Titles.class, "Item Despawn").category("Titles")
                    .description("Shows the time until an item despawns")
                    .build();
    private static final BoolSetting itemStackCount =
            new BoolSetting(Titles.class, "Item Stack Count").category("Titles")
                    .description("Shows the number of items in a stack")
                    .build();
    private static final NumberSetting itemStackMergeRange =
            new NumberSetting(Titles.class, "Item Merge Range", 0, 10).value(5)
                    .description("The maximum distance between two items to merge their stacks")
                    .category("Titles")
                    .build();
    private static final BoolSetting arrowDespawn =
            new BoolSetting(Titles.class, "Arrow Despawn").category("Titles")
                    .description("Shows the time until an arrow despawns")
                    .build();
    private static final BoolSetting arrowInfinity =
            new BoolSetting(Titles.class, "Arrow Infinity").category("Titles")
                    .description("Shows if an arrow is infinite. (Not able to be picked up)")
                    .build();

    @EventHandler
    void onPostWorldRender(WorldRender.PostWorldRenderEvent event) {
        var itemTitles = new ArrayList<ItemTitleData>();
        assert client.world != null;

        for (var e : client.world.getEntities()) {
            if (tntCountdown.value() && e instanceof TntEntity tnt) {
                var fuze = tnt.getFuse();
                var text = Text.of(textColor.value().code() + timeFormat.value().format(fuze));
                renderText(text, tnt, event, .5);
            }

            if ((itemCountdown.value() || itemStackCount.value()) && e instanceof ItemEntity item) {
                var data = new ItemTitleData(item);
                itemTitles.stream()
                        .filter(d -> d.stackable(data))
                        .findFirst()
                        .ifPresentOrElse(d -> d.merge(data), () -> itemTitles.add(data));
            }

            if ((arrowDespawn.value() || arrowInfinity.value()) && e instanceof ArrowEntity arrow) {
                var remainingLife = Math.max(1200 - arrow.age, 0);

                var text = textColor.value().code();
                if (arrowDespawn.value()) text += timeFormat.value().format(remainingLife);
                // TODO: Dosent work with infinity enchantment in survival
                if (arrowInfinity.value()) text +=
                        (arrow.pickupType == PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY) ? " ∞" : "";

                renderText(Text.of(text), arrow, event, 0);
            }

            if (tamableOwner.value() && (e instanceof TameableEntity || e instanceof AbstractHorseEntity)) {
                var owner =
                        (e instanceof TameableEntity tamable) ? tamable.getOwnerUuid() : ((AbstractHorseEntity) e).getOwnerUuid();
                if (owner != null) {
                    var lines = new ArrayList<Text>();
                    var name = e.getCustomName();
                    if (name != null) lines.add(name);
                    lines.add(Text.of(textColor.value().code() + "Owner: " +
                            Util.uuidToName(owner).orElse(owner.toString())));
                    WorldRenderUtils.renderLines(lines,
                            entityPos(e, event.tickDelta).add(0, 1.5 + yOffset.value(), 0), scale.value(), false);
                }
            }
        }

        for (var i : itemTitles) {
            var text = textColor.value().code();
            if (itemCountdown.value()) text += timeFormat.value().format(i.toDespawn());
            if (itemStackCount.value()) text += " ×" + i.count;

            WorldRenderUtils.renderText(Text.of(text),
                    entityPos(i.lastPos, i.pos, event.tickDelta).add(0, yOffset.value(), 0), scale.value(), false);
        }
    }

    void renderText(Text text, Entity pos, WorldRender.WorldRenderEvent event, double offset) {
        WorldRenderUtils.renderText(text, entityPos(pos, event.tickDelta).add(0, offset + yOffset.value(), 0),
                scale.value(), false);
    }

    public enum TimeFormat {
        @DocumentedEnum("Ex: 182s")
        Seconds,
        @DocumentedEnum("Ex: 2 Hours")
        BestFit,
        @DocumentedEnum("Ex: 3640")
        Ticks;

        public String format(int ticks) {
            final var units = new Pair[]{
                    new Pair<>("s", 60),
                    new Pair<>("m", 60),
                    new Pair<>("h", 0)
            };

            return switch (this) {
                case BestFit -> Util.bestTime((long) (ticks / .02), units, true, precision.intValue());
                case Seconds -> String.format("%.1fs", ticks / 20f);
                case Ticks -> String.format("%d", ticks);
            };
        }
    }

    static class ItemTitleData {
        Item item;
        Vec3d pos;
        Vec3d lastPos;
        int count;
        int age;

        ItemTitleData(ItemEntity item) {
            this.item = item.getStack().getItem();
            this.pos = item.getPos();
            this.lastPos = new Vec3d(item.lastRenderX, item.lastRenderY, item.lastRenderZ);
            this.count = item.getStack().getCount();
            this.age = item.getItemAge();
        }

        boolean stackable(ItemTitleData other) {
            return item.equals(other.item) && pos.distanceTo(other.pos) < itemStackMergeRange.value();
        }

        void merge(ItemTitleData other) {
            count += other.count;
            age = Math.max(age, other.age);
        }

        int toDespawn() {
            return Math.max(6000 - age, 0);
        }
    }
}
