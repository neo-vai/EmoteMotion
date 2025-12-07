package me.neovai.player;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import me.neovai.player.status.CurrentStatus;
import me.neovai.player.status.PlayerMotionStatus;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static me.neovai.EmoteMotionClient.LAYER_ID;

import static me.neovai.emotes.Emotes.*;

public class PlayerController {

    private CurrentStatus CURRENT;
    private int ATTACK_TIMER;


    private CurrentStatus PRESET_STAY = new CurrentStatus(PlayerMotionStatus.STAY, this::isStay, this::onStartStay, this::onStopStay);
    private CurrentStatus PRESET_WALKING = new CurrentStatus(PlayerMotionStatus.WALKING, this::isWalking, this::onStartWalking, this::onStopWalking);
    private CurrentStatus PRESET_SPRINTING = new CurrentStatus(PlayerMotionStatus.SPRINTING, this::isSprinting, this::onStartSprinting, this::onStopSprinting);
    private CurrentStatus PRESET_JUMPING = new CurrentStatus(PlayerMotionStatus.JUMPING, this::isJumping, this::onStartJumping, this::onStopJumping);
    private CurrentStatus PRESET_SHIFTING = new CurrentStatus(PlayerMotionStatus.SHIFTING, this::isShifting, this::onStartShifting, this::onStopShifting);
    private CurrentStatus PRESET_ATTACKING = new CurrentStatus(PlayerMotionStatus.ATTACKING, this::isAttacking, this::onStartAttacking, this::onStopAttacking);

    public PlayerController() {
        this.CURRENT = PRESET_STAY;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                updatePlayerAnimation(client);
            }
        });

        ClientPreAttackCallback.EVENT.register((client, player, i)
                -> {
            if (client != null && player != null) {
                attackEvent(player, player.getMainHandItem());
            }
            return false;
        });
    }

    // First > Second
    private boolean compareStatus(PlayerMotionStatus first, PlayerMotionStatus second) {
        return first.getPriority() > second.getPriority();
    }

    // First > Second
    private boolean compareStatus(CurrentStatus first, CurrentStatus second) {
        return compareStatus(first.PMSTATUS, second.PMSTATUS);
    }

    private void attackEvent(Player player, ItemStack itemStack) {
        if (CURRENT.PMSTATUS != PlayerMotionStatus.ATTACKING) {
            onStartAttacking(player, getController(player, LAYER_ID));
        }
    }

    private void updatePlayerAnimation(Minecraft client) {
        Player player = client.player;
        if (ATTACK_TIMER > 0) ATTACK_TIMER--;

        PlayerAnimationController controller = getController(player, LAYER_ID);

        check(CURRENT, player, controller);

        check(PRESET_STAY, player, controller);
        check(PRESET_WALKING, player, controller);
        check(PRESET_SPRINTING, player, controller);
        check(PRESET_JUMPING, player, controller);
        check(PRESET_SHIFTING, player, controller);
        check(PRESET_ATTACKING, player, controller);

    }

    private void check(CurrentStatus status, Player player, PlayerAnimationController controller) {
        if (status.isFunc.apply(player) && CURRENT.PMSTATUS != status.PMSTATUS) {
            if (compareStatus(status, CURRENT)) {
                CURRENT = status;
                status.onStart.accept(player, controller);
            }
        } else if (!status.isFunc.apply(player) && CURRENT == status) {
            status.onStop.accept(controller);
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
        return player.isJumping() && !player.onGround();
    }

    private boolean isShifting(Player player) {
        return player.isCrouching();
    }

    private boolean isStay(Player player) {
        return true;
    }

    private boolean isAttacking(Player player) {
        return ATTACK_TIMER > 0;
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

    private void onStartAttacking(Player player, PlayerAnimationController controller) {
        if (isSword(player.getMainHandItem())) {
            ATTACK_TIMER = SWORD_TICKS;
            controller.triggerAnimation(randomSword(null));
        }
    }

    private void onStartStay(Player player, PlayerAnimationController controller) {}

    private boolean isSword(ItemStack stack) {
        return true;
    }

    // STOP EVENTS

    private void onStopSprinting(PlayerAnimationController controller) {
        setCurrent(null);
        stopAnimation(controller);
    }

    private void onStopWalking(PlayerAnimationController controller) {
        setCurrent(null);
        stopAnimation(controller);
    }

    private void onStopJumping(PlayerAnimationController controller) {
        setCurrent(null);
    }

    private void onStopShifting(PlayerAnimationController controller) {
        setCurrent(null);
    }

    private void onStopAttacking(PlayerAnimationController controller) {
        setCurrent(null);
    }

    private void onStopStay(PlayerAnimationController controller) {}

    private void stopAnimation(PlayerAnimationController controller) {
        controller.stop();
    }

    private void setCurrent(@Nullable CurrentStatus status) {
        if (status == null) {
            CURRENT = PRESET_STAY;
        } else {
            CURRENT = status;
        }
    }

    private PlayerAnimationController getController(Player player, ResourceLocation layer_id) {
        return  (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, layer_id);
    }

}
