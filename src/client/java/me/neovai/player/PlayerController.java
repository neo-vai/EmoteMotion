package me.neovai.player;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import me.neovai.player.status.CurrentStatus;
import me.neovai.player.status.PlayerMotionStatus;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.jetbrains.annotations.Nullable;

import static me.neovai.EmoteMotionClient.LAYER_ID;

import static me.neovai.emotes.Emotes.*;

public class PlayerController {

    private CurrentStatus CURRENT;
    private int ATTACK_TIMER;


    private final CurrentStatus PRESET_STAY = new CurrentStatus(PlayerMotionStatus.STAY, this::isStay, this::onStartNothing, this::onStopNothing);
    private final CurrentStatus PRESET_WALKING = new CurrentStatus(PlayerMotionStatus.WALKING, this::isWalking, this::onStartWalking, this::onStopNothing);
    private final CurrentStatus PRESET_SPRINTING = new CurrentStatus(PlayerMotionStatus.SPRINTING, this::isSprinting, this::onStartSprinting, this::onStopNothing);
    private final CurrentStatus PRESET_JUMPING = new CurrentStatus(PlayerMotionStatus.JUMPING, this::isJumping, this::onStartJumping, this::onStopNothing);
    private final CurrentStatus PRESET_FALLING = new CurrentStatus(PlayerMotionStatus.FALLING, this::isFalling, this::onStartFalling, this::onStopFalling);
    private final CurrentStatus PRESET_SWIMMING = new CurrentStatus(PlayerMotionStatus.SWIMMING, this::isSwimming, this::onStartNothing, this::onStopNothing);
    private final CurrentStatus PRESET_SHIFTING = new CurrentStatus(PlayerMotionStatus.SHIFTING, this::isShifting, this::onStartNothing, this::onStopNothing);
    private final CurrentStatus PRESET_USE_ANIMATION = new CurrentStatus(PlayerMotionStatus.USE_ANIMATION, this::isUsingAnimation, this::onStartNothing, this::onStopNothing);
    private final CurrentStatus PRESET_EATING = new CurrentStatus(PlayerMotionStatus.EATING, this::isEating, this::onStartEating, this::onStopNothing);
    private final CurrentStatus PRESET_ATTACKING = new CurrentStatus(PlayerMotionStatus.ATTACKING, this::isAttacking, this::onStartAttacking, this::onStopNothing);

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

        check(PRESET_EATING, player, controller);
        check(PRESET_USE_ANIMATION, player, controller);
        check(PRESET_FALLING, player, controller);
        check(PRESET_SWIMMING, player, controller);
        check(PRESET_STAY, player, controller);
        check(PRESET_WALKING, player, controller);
        check(PRESET_SPRINTING, player, controller);
        check(PRESET_JUMPING, player, controller);
        check(PRESET_SHIFTING, player, controller);
        check(PRESET_ATTACKING, player, controller);

    }

    private void check(CurrentStatus status, Player player, PlayerAnimationController controller) {
        if ((status.isFunc.apply(player) && CURRENT.PMSTATUS != status.PMSTATUS) && compareStatus(status, CURRENT)) {
            CURRENT.onStop.accept(controller);
            CURRENT = status;
            status.onStart.accept(player, controller);
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
        return (player.isJumping() && !player.onGround() && !isFlying(player) && !player.isSwimming())
                || CURRENT.PMSTATUS == PlayerMotionStatus.JUMPING && !player.onGround() && !isFlying(player) && !player.isSwimming();
    }

    private boolean isShifting(Player player) {
        return player.isCrouching();
    }

    private boolean isStay(Player player) {
        return player.onGround();
    }

    private boolean isAttacking(Player player) {
        return ATTACK_TIMER > 0;
    }

    private boolean isFalling(Player player) {
        return !player.onGround() && !isFlying(player);
    }

    private boolean isFlying(Player player) {
        return player.getAbilities().flying;
    }

    private boolean isSwimming(Player player) {
        return player.isSwimming();
    }

    private boolean isUsingAnimation(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }

        ItemStack stack = player.getUseItem();
        ItemUseAnimation anim = stack.getItem().getUseAnimation(stack);

        return anim != ItemUseAnimation.NONE;
    }

    private boolean isEating(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }
        return player.getUseItem().getUseAnimation() == ItemUseAnimation.EAT || player.getUseItem().getUseAnimation() == ItemUseAnimation.DRINK;
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

    private void onStartAttacking(Player player, PlayerAnimationController controller) {
        ItemStack attackItem = player.getMainHandItem();
        if (isSword(attackItem)) {
            ResourceLocation sword = randomSword();
            if (sword != null) {
                ATTACK_TIMER = SWORD_TICKS;
                controller.triggerAnimation(sword);
                return;
            }
        }

        if (isWeapon(attackItem)) {
            return;
        }

        ResourceLocation hand = randomHand();
        if (hand != null) {
            ATTACK_TIMER = HAND_TICK;
            controller.triggerAnimation(randomHand());
        }
    }

    private void onStartFalling(Player player, PlayerAnimationController controller) {
        controller.triggerAnimation(JUMP);
    }

    private void onStartNothing(Player player, PlayerAnimationController controller) {}

    private void onStartEating(Player player, PlayerAnimationController controller) {
        controller.triggerAnimation(EAT);
    }

    private boolean isSword(ItemStack stack) {
        return stack.is(ItemTags.SWORDS);
    }

    private boolean isWeapon(ItemStack stack) {
        String name = stack.getItem().getDescriptionId();
        return name.endsWith("axe") || name.endsWith("spear") || name.endsWith("trident") || name.endsWith("sword") || name.endsWith("mace");
    }


    // STOP EVENTS

    private void onStopFalling(PlayerAnimationController controller) {
        setCurrent(null);
        controller.triggerAnimation(JUMPTOSTAY);
    }

    private void onStopNothing(PlayerAnimationController controller) {
        setCurrent(null);
        stopAnimation(controller);
    }

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
        return  (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer( player, layer_id);
    }

}
