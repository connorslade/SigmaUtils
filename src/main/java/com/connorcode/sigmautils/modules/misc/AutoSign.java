package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.Interact;
import com.connorcode.sigmautils.event.PacketReceiveCallback;
import com.connorcode.sigmautils.module.Category;
import com.connorcode.sigmautils.module.Module;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SignItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class AutoSign extends Module {
    // TODO: Option to decide if front/back is defined by the sign or by your current position
    EnumSetting<Mode> mode = new EnumSetting<>(AutoSign.class, "Mode", Mode.class)
            .description("If the mode is complete, each side will be written to the sign, even if its empty. " +
                    "If its lazy, only sids with text will be written.")
            .value(Mode.Lazy)
            .build();
    SignTextSetting signText = new SignTextSetting();
    HashMap<BlockPos, UpdateTask> tasks = new HashMap<>();

    public AutoSign() {
        super("auto_sign", "Auto Sign",
                "Automatically writes text on signs you place. " +
                        "If you are looking at a sign, it will edit that sign.",
                Category.Misc);
    }

    public void init() {
        super.init();

        Interact.InteractBlockCallback.EVENT.register(event -> {
            assert client.player != null && client.interactionManager != null && client.world != null;
            if (!enabled || event.getHitResult().getType() != HitResult.Type.BLOCK) return;
            var lookingAtSign = event.getHitResult().getType() == HitResult.Type.BLOCK &&
                    (client.world.getBlockEntity(event.getHitResult().getBlockPos()) instanceof SignBlockEntity);
            var item = client.player.getStackInHand(event.getHand());
            if (!(item.getItem() instanceof SignItem) && !lookingAtSign) return;

            // Place Sign
            var signBlock = event.getHitResult().getBlockPos();
            if (!lookingAtSign) {
                signBlock = signBlock.offset(event.getHitResult().getSide());
                var itemUsageContext = new ItemUsageContext(client.player, event.getHand(), event.getHitResult());
                item.useOnBlock(itemUsageContext);
            }

            // Interact block
            client.interactionManager.sendSequencedPacket(client.world,
                    sequence -> new PlayerInteractBlockC2SPacket(event.getHand(), event.getHitResult(), sequence));

            tasks.put(signBlock, signText.asTask(event, signBlock));
            event.setReturnValue(ActionResult.CONSUME);
        });

        PacketReceiveCallback.EVENT.register(packet -> {
            assert client.interactionManager != null && client.world != null;
            if (!enabled || !(packet.get() instanceof SignEditorOpenS2CPacket sign) ||
                    !tasks.containsKey(sign.getPos())) return;
            var task = tasks.get(sign.getPos());
            var again = task.next();

            if (again) {
                var hitResult = new BlockHitResult(task.hitResult.getPos(), task.hitResult.getSide().getOpposite(),
                        sign.getPos(), false);
                client.interactionManager.sendSequencedPacket(client.world,
                        sequence -> new PlayerInteractBlockC2SPacket(task.hand, hitResult, sequence));
            } else tasks.remove(sign.getPos());
            packet.cancel();
        });
    }

    enum Mode {
        Complete,
        Lazy
    }

    class UpdateTask {
        Hand hand;
        BlockHitResult hitResult;
        BlockPos signPos;
        List<Boolean> sides;

        public UpdateTask(Interact.InteractBlockCallback.InteractBlockEvent event, BlockPos signPos, boolean front, boolean back) {
            this.hand = event.getHand();
            this.hitResult = event.getHitResult();
            this.signPos = signPos;
            this.sides = new ArrayList<>();
            if (front) sides.add(true);
            if (back) sides.add(false);
        }

        boolean next() {
            var nextSide = sides.remove(0);
            var packet = AutoSign.this.signText.getPacket(nextSide, signPos);
            Objects.requireNonNull(client.getNetworkHandler()).sendPacket(packet);

            return !this.sides.isEmpty();
        }
    }

    class SignTextSetting {
        StringSetting[] frontLines = new StringSetting[4];
        StringSetting[] backLines = new StringSetting[4];

        public SignTextSetting() {
            frontLines[0] = lineSetting(1, true).value("Hello, world!").build();
            frontLines[1] = lineSetting(2, true).value("- SigmaUtils").build();

            for (int i = 3; i <= 4; i++) frontLines[i - 1] = lineSetting(i, true).build();
            for (int i = 1; i <= 4; i++) backLines[i - 1] = lineSetting(i, false).build();
        }

        private static StringSetting lineSetting(int line, boolean side) {
            return new StringSetting(AutoSign.class, "")
                    .category("Sign Text - " + (side ? "Front" : "Back"))
                    .id("line_" + line + "_" + (side ? "front" : "back"))
                    .showName(false);
        }

        boolean hasLines(boolean front) {
            for (StringSetting line : front ? frontLines : backLines) if (!line.value().isEmpty()) return true;
            return false;
        }

        boolean toBeWritten(boolean front) {
            if (mode.value() == Mode.Complete) return true;
            return hasLines(front);
        }

        UpdateTask asTask(Interact.InteractBlockCallback.InteractBlockEvent event, BlockPos signPos) {
            return new UpdateTask(event, signPos, this.toBeWritten(true), this.toBeWritten(false));
        }

        UpdateSignC2SPacket getPacket(boolean front, BlockPos pos) {
            var lines = front ? frontLines : backLines;
            return new UpdateSignC2SPacket(pos, front, lines[0].value(), lines[1].value(), lines[2].value(),
                    lines[3].value());
        }
    }
}
