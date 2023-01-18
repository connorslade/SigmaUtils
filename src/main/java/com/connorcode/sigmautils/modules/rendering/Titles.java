package com.connorcode.sigmautils.modules.rendering;

import com.connorcode.sigmautils.config.settings.BoolSetting;
import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.NumberSetting;
import com.connorcode.sigmautils.event.WorldRender;
import com.connorcode.sigmautils.misc.TextStyle;
import com.connorcode.sigmautils.misc.Util;
import com.connorcode.sigmautils.misc.WorldRenderUtils;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class Titles extends Module {
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
            new BoolSetting(Titles.class, "TNT Countdown").displayType(BoolSetting.DisplayType.CHECKBOX)
                    .description("Shows the time until the TNT explodes.")
                    .category("Titles")
                    .build();
    private static final BoolSetting itemCountdown =
            new BoolSetting(Titles.class, "Item Despawn").displayType(BoolSetting.DisplayType.CHECKBOX)
                    .description("Shows the time until an item despawns")
                    .category("Titles")
                    .build();
    private static final BoolSetting itemStackCount =
            new BoolSetting(Titles.class, "Item Stack Count").displayType(BoolSetting.DisplayType.CHECKBOX)
                    .description("Shows the number of items in a stack")
                    .category("Titles")
                    .build();
    private static final BoolSetting arrowDespawn =
            new BoolSetting(Titles.class, "Arrow Despawn").displayType(BoolSetting.DisplayType.CHECKBOX)
                    .description("Shows the time until an arrow despawns")
                    .category("Titles")
                    .build();
    private static final BoolSetting arrowInfinity =
            new BoolSetting(Titles.class, "Arrow Infinity").displayType(BoolSetting.DisplayType.CHECKBOX)
                    .description("Shows if an arrow is infinite. (Not able to be picked up)")
                    .category("Titles")
                    .build();
    public static final BoolSetting tamableOwner =
            new BoolSetting(Titles.class, "Tamable Owner").displayType(BoolSetting.DisplayType.CHECKBOX)
                    .description("Shows the owner of a tamable entity")
                    .category("Titles")
                    .build();

    public Titles() {
        super("titles", "Titles", "Adds in-game titles to different *things*", Category.Rendering);
    }

    @Override
    public void init() {
        super.init();

        WorldRender.PostWorldRenderCallback.EVENT.register(event -> {
            assert client.world != null;
            for (var e : client.world.getEntities()) {
                if (tntCountdown.value() && e instanceof TntEntity tnt) {
                    var fuze = tnt.getFuse();
                    var text = Text.of(textColor.value().code + timeFormat.value().format(fuze));
                    WorldRenderUtils.renderText(text, e.getPos().add(0, .5 + yOffset.value(), 0), scale.value());
                }

                if ((itemCountdown.value() || itemStackCount.value()) && e instanceof ItemEntity item) {
                    var toDespawn = Math.max(6000 - item.getItemAge(), 0);
                    var count = item.getStack().getCount();

                    String text = textColor.value().code;
                    if (itemCountdown.value()) text += timeFormat.value().format(toDespawn);
                    if (itemStackCount.value()) text += " ×" + count;
                    WorldRenderUtils.renderText(Text.of(text), e.getPos().add(0, yOffset.value(), 0), scale.value());
                }

                if ((arrowDespawn.value() || arrowInfinity.value()) && e instanceof ArrowEntity arrow) {
                    var remainingLife = Math.max(1200 - arrow.age, 0);

                    var text = textColor.value().code;
                    if (arrowDespawn.value()) text += timeFormat.value().format(remainingLife);
                    // TODO: Dosent work with infinity enchantment in survival
                    if (arrowInfinity.value()) text +=
                            (arrow.pickupType == PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY) ? " ∞" : "";

                    WorldRenderUtils.renderText(Text.of(text), e.getPos().add(0, yOffset.value(), 0), scale.value());
                }

                if (tamableOwner.value() && (e instanceof TameableEntity || e instanceof AbstractHorseEntity)) {
                    var owner = (e instanceof TameableEntity tamable) ? tamable.getOwnerUuid() : ((AbstractHorseEntity) e).getOwnerUuid();
                    if (owner != null) {
                        var lines = new ArrayList<Text>();
                        var name = e.getCustomName();
                        if (name != null) lines.add(name);
                        lines.add(Text.of(textColor.value().code + "Owner: " +
                                Util.uuidToName(owner).orElse(owner.toString())));
                        WorldRenderUtils.renderLines(lines,  e.getPos().add(0, 1.5 + yOffset.value(), 0), scale.value());
                    }
                }
            }
        });
    }

    public enum TimeFormat {
        Seconds,
        BestFit,
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
}
