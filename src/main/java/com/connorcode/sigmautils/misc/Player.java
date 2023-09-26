package com.connorcode.sigmautils.misc;

import com.connorcode.sigmautils.event.EventHandler;
import com.connorcode.sigmautils.event.misc.GameLifecycle;
import com.connorcode.sigmautils.event.misc.Tick;
import com.connorcode.sigmautils.event.network.PacketSendEvent;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Objects;

import static com.connorcode.sigmautils.SigmaUtils.client;

public class Player {
    public static final Player player = new Player();

    static boolean debug = false;
    static long tick = 0;
    ArrayList<Action> actions = new ArrayList<>();

    static void debug(String format, Object... args) {
        if (debug) System.out.printf("[SigmaUtils::Player] " + format, args);
    }

    static void sequenced(SequencedPacketCreator creator) {
        assert client.interactionManager != null;
        assert client.world != null;
        client.interactionManager.sendSequencedPacket(client.world, creator);
    }

    @EventHandler
    private void onWorldClose(GameLifecycle.WorldCloseEvent event) {
        actions.clear();
    }

    @EventHandler
    private void onTick(Tick.GameTickEvent event) {
        tick++;
        actions.forEach(Action::tick);
        actions.removeIf(Action::complete);
    }

    @EventHandler
    private void onPacketSend_PacketSendEvent(PacketSendEvent event) {
        if (event.get() instanceof PlayerActionC2SPacket packet)
            Objects.requireNonNull(client.player).sendMessage(Text.of(String.format("Action: %s [%d]", packet.getAction(), packet.getSequence())), false);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void clearActions() {
        actions.clear();
    }

    public interface Action {
        default boolean complete() {
            return true;
        }

        void tick();
    }

    abstract static class InteractAction implements Action {
        InteractTime time;

        public InteractAction(InteractTime time) {
            this.time = time;
        }

        @Override
        public boolean complete() {
            return time.type == InteractTime.Type.Once;
        }

        @Override
        public void tick() {
            if (time.type == InteractTime.Type.Interval) {
                if (tick - time.startTick >= time.interval) {
                    interact();
                    time.startTick = tick;
                }
            } else interact();
        }

        abstract void interact();
    }

    public static class AttackAction extends InteractAction {
        BlockPos lastBlock;

        public AttackAction(InteractTime time) {
            super(time);
        }

        @Override
        void interact() {
            var net = client.getNetworkHandler();
            if (client.player == null || net == null || client.interactionManager == null || client.world == null)
                return;

            if (client.crosshairTarget instanceof EntityHitResult hit) {
                net.sendPacket(PlayerInteractEntityC2SPacket.attack(hit.getEntity(), client.player.isSneaking()));
                client.player.attack(hit.getEntity());
                client.player.resetLastAttackedTicks();
            }

            if (client.crosshairTarget instanceof BlockHitResult hit) {
//                System.out.printf("CrosshairTarget: %s; LastBlock: %s\n", hit.getBlockPos(), lastBlock == null ? null : lastBlock.getBlockPos());
                if (lastBlock == null || !lastBlock.equals(hit.getBlockPos())) {
                    client.interactionManager.attackBlock(hit.getBlockPos(), hit.getSide());
                }
//                if (lastBlock == null) {
//                    net.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, hit.getBlockPos(), hit.getSide()));
//                } else if (!lastBlock.getBlockPos().equals(hit.getBlockPos())) {
//                    sequenced(sequence -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, lastBlock.getBlockPos(), lastBlock.getSide(), sequence));
//                    sequenced(sequence -> new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, hit.getBlockPos(), hit.getSide(), sequence));
//                }
                lastBlock = hit.getBlockPos();
            } else if (lastBlock != null) {
//                net.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, lastBlock.getBlockPos(), lastBlock.getSide()));
                lastBlock = null;
            }
        }
    }

    public static class InteractTime {
        Type type;
        int interval;
        long startTick;

        public InteractTime(Type type) {
            this.type = type;
        }

        public static InteractTime interval(int interval) {
            InteractTime time = new InteractTime(Type.Interval);
            time.interval = interval;
            time.startTick = tick;
            return time;
        }

        public static InteractTime continuous() {
            return new InteractTime(Type.Continuous);
        }

        public static InteractTime once() {
            return new InteractTime(Type.Once);
        }

        enum Type {
            Continuous,
            Interval,
            Once
        }
    }
}
