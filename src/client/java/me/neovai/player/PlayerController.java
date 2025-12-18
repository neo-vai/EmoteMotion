package me.neovai.player;

import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.api.events.client.ClientEmoteAPI;
import io.github.kosmx.emotes.api.events.client.ClientEmoteEvents;
import me.neovai.player.status.CurrentStatus;
import me.neovai.player.status.PlayerMotionStatus;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static me.neovai.emotes.Emotes.*;

public class PlayerController {

    private CurrentStatus CURRENT;
    private int ATTACK_TIMER;
    private int EMOTECRAFT_ANIM_TIMER;


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
    private final CurrentStatus PRESET_CLIMBING = new CurrentStatus(PlayerMotionStatus.CLIMBING, this::isClimbing, this::onStartNothing, this::onStopNothing);
    private final CurrentStatus PRESET_EMOTECRAFT_ANIMATION = new CurrentStatus(PlayerMotionStatus.EMOTECRAFT_ANIMATION, this::isPlayingEmoteCraftAnimation, this::onStartNothing, this::onStopNothing);

    public PlayerController() {
        this.CURRENT = PRESET_STAY;

        ClientTickEvents.END_CLIENT_TICK.register(this::update);
        ClientPreAttackCallback.EVENT.register(this::attackEvent);
        ClientEmoteEvents.EMOTE_PLAY.register(this::animationPlayEvent);
        ClientEmoteEvents.LOCAL_EMOTE_STOP.register(this::animationStopEvent);
    }

    private void animationPlayEvent(Animation emoteData, float tick, UUID uuid) {
        if (local_names.isEmpty()) addAllLocalEmotes();
        if (!local_names.contains(emoteData.getNameOrId()) && CURRENT.PMSTATUS != PlayerMotionStatus.EMOTECRAFT_ANIMATION) {
            CURRENT = PRESET_EMOTECRAFT_ANIMATION;
            if (emoteData.loopType() == Animation.LoopType.PLAY_ONCE) {
                EMOTECRAFT_ANIM_TIMER = ((Float) emoteData.data().get("endTick").get()).intValue();
            } else {
                EMOTECRAFT_ANIM_TIMER = Integer.MIN_VALUE;
            }
        }
    }

    private void animationStopEvent() {
        if (CURRENT == PRESET_EMOTECRAFT_ANIMATION) {
            CURRENT = PRESET_STAY;
            EMOTECRAFT_ANIM_TIMER = 0;
        }
    }

    private boolean attackEvent(Minecraft client, LocalPlayer player, int i) {
        if (CURRENT.PMSTATUS != PlayerMotionStatus.ATTACKING) {
            onStartAttacking(player);
        }
        return false;
    }

    private void update(Minecraft client) {
        Player player = client.player;
        if (player == null) return;

        if (ATTACK_TIMER > 0) ATTACK_TIMER--;
        if (EMOTECRAFT_ANIM_TIMER > 0) EMOTECRAFT_ANIM_TIMER--;

        check(CURRENT, player);

        check(PRESET_CLIMBING, player);
        check(PRESET_EATING, player);
        check(PRESET_USE_ANIMATION, player);
        check(PRESET_FALLING, player);
        check(PRESET_SWIMMING, player);
        check(PRESET_STAY, player);
        check(PRESET_WALKING, player);
        check(PRESET_SPRINTING, player);
        check(PRESET_JUMPING, player);
        check(PRESET_SHIFTING, player);
        check(PRESET_ATTACKING, player);

    }

    private void check(CurrentStatus status, Player player) {
        if ((status.isFunc.apply(player) && CURRENT.PMSTATUS != status.PMSTATUS) && CurrentStatus.compareStatus(status.PMSTATUS, CURRENT.PMSTATUS)) {
            CURRENT.onStop.run();
            CURRENT = status;
            status.onStart.accept(player);
        } else if (!status.isFunc.apply(player) && CURRENT == status) {
            status.onStop.run();
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

    private boolean isClimbing(Player player) {
        return player.onClimbable();
    }

    private boolean isEating(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }
        return player.getUseItem().getUseAnimation() == ItemUseAnimation.EAT || player.getUseItem().getUseAnimation() == ItemUseAnimation.DRINK;
    }

    private boolean isPlayingEmoteCraftAnimation(Player player) {
        if (CURRENT.PMSTATUS != PlayerMotionStatus.EMOTECRAFT_ANIMATION) return false;
        return EMOTECRAFT_ANIM_TIMER > 0 || EMOTECRAFT_ANIM_TIMER == Integer.MIN_VALUE;
    }
    // START EVENTS

    private void onStartSprinting(Player player) {
        playAnimation(RUN);
    }

    private void onStartWalking(Player player) {
        playAnimation(GAIT);
    }

    private void onStartJumping(Player player) {
        if (!isShifting(player)) {
            playAnimation(JUMP);
        }
    }

    private void onStartAttacking(Player player) {
        ItemStack attackItem = player.getMainHandItem();
        if (isSword(attackItem)) {
            ResourceLocation sword = randomSword();
            if (sword != null) {
                ATTACK_TIMER = SWORD_TICKS;
                playAnimation(sword);
                return;
            }
        }

        if (isWeapon(attackItem)) {
            return;
        }

        ResourceLocation hand = randomHand();
        if (hand != null) {
            ATTACK_TIMER = HAND_TICK;
            playAnimation(randomHand());
        }
    }

    private void onStartFalling(Player player) {
        playAnimation(JUMP);
    }


    private void onStartNothing(Player player) {}

    private void onStartEating(Player player) {
        playAnimation(EAT);
    }

    private boolean isSword(ItemStack stack) {
        return stack.is(ItemTags.SWORDS);
    }

    private boolean isWeapon(ItemStack stack) {
        String name = stack.getItem().getDescriptionId();
        return name.endsWith("axe") || name.endsWith("spear") || name.endsWith("trident") || name.endsWith("sword") || name.endsWith("mace");
    }


    // STOP EVENTS

    private void onStopFalling() {
        setCurrent(null);
        playAnimation(JUMPTOSTAY);
    }

    private void onStopNothing() {
        setCurrent(null);
        stopAnimation();
    }

    private void setCurrent(@Nullable CurrentStatus status) {
        if (status == null) {
            CURRENT = PRESET_STAY;
        } else {
            CURRENT = status;
        }
    }

    private void playAnimation(ResourceLocation animation) {
        ClientEmoteAPI.playEmote(PlayerAnimResources.getAnimation(animation));
    }

    private void stopAnimation() {
        ClientEmoteAPI.stopEmote();
    }

}
