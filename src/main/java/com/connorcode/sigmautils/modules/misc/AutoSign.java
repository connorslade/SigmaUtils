package com.connorcode.sigmautils.modules.misc;

import com.connorcode.sigmautils.config.settings.EnumSetting;
import com.connorcode.sigmautils.config.settings.StringSetting;
import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event._interface.Interact;
import com.connorcode.sigmautils.event.misc.GameLifecycle;
import com.connorcode.sigmautils.event.network.PacketReceiveEvent;
import com.connorcode.sigmautils.module.Module;
import com.connorcode.sigmautils.module.ModuleInfo;
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


@ModuleInfo(description = "Automatically writes text on signs you place. If you are looking at a sign, it will edit that sign.")
public class AutoSign extends Module {
    EnumSetting<Mode> mode = new EnumSetting<>(AutoSign.class, "Mode", Mode.class)
            .description("If the mode is complete, each side will be written to the sign, even if its empty. " +
                         "If its lazy, only sids with text will be written.")
            .value(Mode.Lazy)
            .build();
    EnumSetting<FrontDefinition> frontDefinition =
            new EnumSetting<>(AutoSign.class, "Front Definition", FrontDefinition.class)
                    .description("Changes how the 'front' of the sign is defined." +
                            "Placement: The front is the side you placed the sign on." +
                            "LookingAt: The front is the side you are looking at.")
                    .value(FrontDefinition.Placement)
                    .build();
    SignTextSetting signText = new SignTextSetting();
    HashMap<BlockPos, UpdateTask> tasks = new HashMap<>();

    @EventHandler
    void onInteractBlock(Interact.InteractBlockEvent event) {
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
            var count = item.getCount();
            item.useOnBlock(itemUsageContext);
            if (client.player.isCreative()) item.setCount(count);
        }

        // Determine if sides should be reversed
        var mode = frontDefinition.value() == FrontDefinition.Placement;
        var front = (mode || !lookingAtSign) || ((SignBlockEntity) Objects.requireNonNull(
                client.world.getBlockEntity(event.getHitResult().getBlockPos()))).isPlayerFacingFront(
                client.player);

        tasks.put(signBlock, signText.asTask(event, signBlock, !front));

        // Interact block
        client.interactionManager.sendSequencedPacket(client.world,
                sequence -> new PlayerInteractBlockC2SPacket(event.getHand(), event.getHitResult(), sequence));
        event.setReturnValue(ActionResult.CONSUME_PARTIAL);
    }

    @EventHandler
    void onPacketReceive(PacketReceiveEvent packet) {
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
    }

    @EventHandler
    void onWorldClose(GameLifecycle.WorldCloseEvent event) {
        tasks.clear();
    }

    enum Mode {
        Complete,
        Lazy
    }

    enum FrontDefinition {
        Placement,
        LookingAt
    }

    class UpdateTask {
        Hand hand;
        BlockHitResult hitResult;
        BlockPos signPos;
        List<Boolean> sides;
        boolean reverse;

        public UpdateTask(Interact.InteractBlockEvent event, BlockPos signPos, boolean front, boolean back, boolean reverse) {
            this.hand = event.getHand();
            this.hitResult = event.getHitResult();
            this.signPos = signPos;
            this.reverse = reverse;
            this.sides = new ArrayList<>();
            if (front) sides.add(true);
            if (back) sides.add(false);
        }

        boolean next() {
            var nextSide = sides.remove(0);
            var packet = AutoSign.this.signText.getPacket(nextSide, reverse, signPos);
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

        UpdateTask asTask(Interact.InteractBlockEvent event, BlockPos signPos, boolean reverse) {
            return new UpdateTask(event, signPos, this.toBeWritten(true), this.toBeWritten(false), reverse);
        }

        UpdateSignC2SPacket getPacket(boolean front, boolean reverse, BlockPos pos) {
            var lines = front ^ reverse ? frontLines : backLines;
            return new UpdateSignC2SPacket(pos, front,
                    lines[0].value(),
                    lines[1].value(),
                    lines[2].value(),
                    lines[3].value()
            );
        }
    }
}
