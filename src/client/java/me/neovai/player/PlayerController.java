package me.neovai.player;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static me.neovai.EmoteMotionClient.LAYER_ID;

import static me.neovai.emotes.Emotes.*;

public class PlayerController {

    private PlayerMotionStatus CURRENT;

    public PlayerController() {
        this.CURRENT = PlayerMotionStatus.STAY;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                updatePlayerAnimation(client);
            }
        });
    }

    private void updatePlayerAnimation(Minecraft client) {
        Player player = client.player;
        if (player == null) return;

        PlayerAnimationController controller = getController(player, LAYER_ID);

        check(PlayerMotionStatus.JUMPING, this::isJumping, this::onStartJumping, this::onStopJumping, player, controller);
        check(PlayerMotionStatus.WALKING, this::isWalking, this::onStartWalking, this::onStopWalking, player, controller);
        check(PlayerMotionStatus.SPRINTING, this::isSprinting, this::onStartSprinting, this::onStopSprinting, player, controller);
        check(PlayerMotionStatus.SHIFTING, this::isShifting, this::onStartShifting, this::onStopShifting, player, controller);
    }


    private void check(PlayerMotionStatus status, Function<Player, Boolean> isFunc, BiConsumer<Player, PlayerAnimationController> onStart, Consumer<PlayerAnimationController> onStop, Player player, PlayerAnimationController controller) {
        if (isFunc.apply(player) && CURRENT != status) {
            CURRENT = status;
            onStart.accept(player, controller);
        } else if (!isFunc.apply(player) && CURRENT == status) {
            onStop.accept(controller);
        }
    }

    // CHECK EVENTS

    private boolean isSprinting(Player player) {
        return player.isSprinting() && !isShifting(player) && player.onGround();
    }
    private boolean isWalking(Player player) {
        return player.onGround() &&
                (player.zza != 0 || player.xxa != 0) &&
                !player.isSprinting() &&
                !isShifting(player);
    }

    private boolean isJumping(Player player) {
        return player.isJumping();
    }

    private boolean isShifting(Player player) {
        return player.isCrouching();
    }

    // START EVENTS

    private void onStartSprinting(Player player, PlayerAnimationController controller) {
        controller.triggerAnimation(RUN);
    }

    private void onStartWalking(Player player, PlayerAnimationController controller) {
        controller.triggerAnimation(GAIT);
    }

    private void onStartJumping(Player player, PlayerAnimationController controller) {
        if (!isShifting(player)) {
            controller.triggerAnimation(JUMP);
        }
    }

    private void onStartShifting(Player player, PlayerAnimationController controller) {}

    // STOP EVENTS

    private void onStopSprinting(PlayerAnimationController controller) {
        CURRENT = PlayerMotionStatus.STAY;
        stopAnimation(controller);
    }

    private void onStopWalking(PlayerAnimationController controller) {
        CURRENT = PlayerMotionStatus.STAY;
        stopAnimation(controller);
    }

    private void onStopJumping(PlayerAnimationController controller) {
        CURRENT = PlayerMotionStatus.STAY;
    }

    private void onStopShifting(PlayerAnimationController controller) {
        CURRENT = PlayerMotionStatus.STAY;
    }

    private void stopAnimation(PlayerAnimationController controller) {
        controller.stop();
    }






    private PlayerAnimationController getController(Player player, ResourceLocation layer_id) {
        return  (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, layer_id);
    }

}
