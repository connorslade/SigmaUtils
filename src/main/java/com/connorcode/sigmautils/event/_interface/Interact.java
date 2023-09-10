package com.connorcode.sigmautils.event._interface;

import com.connorcode.sigmautils.event.Cancellable;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class Interact {
    public static class InteractBlockEvent extends Cancellable {
        ClientPlayerEntity player;
        Hand hand;
        BlockHitResult hitResult;
        ActionResult result;

        public InteractBlockEvent(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
            this.player = player;
            this.hand = hand;
            this.hitResult = hitResult;
            this.result = ActionResult.FAIL;
        }

        public void setReturnValue(ActionResult result) {
            this.cancel();
            this.result = result;
        }

        public ClientPlayerEntity getPlayer() {
            return player;
        }

        public Hand getHand() {
            return hand;
        }

        public BlockHitResult getHitResult() {
            return hitResult;
        }

        public ActionResult getResult() {
            return result;
        }
    }

    public static class InteractItemEvent extends Cancellable {
        PlayerEntity player;
        Hand hand;
        ActionResult result;

        public InteractItemEvent(PlayerEntity player, Hand hand) {
            this.player = player;
            this.hand = hand;
            this.result = ActionResult.FAIL;
        }

        public void setReturnValue(ActionResult result) {
            this.cancel();
            this.result = result;
        }

        public PlayerEntity getPlayer() {
            return player;
        }

        public Hand getHand() {
            return hand;
        }

        public ActionResult getResult() {
            return result;
        }
    }
}
