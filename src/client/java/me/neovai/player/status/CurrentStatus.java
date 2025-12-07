package me.neovai.player.status;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CurrentStatus {

    public CurrentStatus(PlayerMotionStatus currentStaus, Function<Player, Boolean> isFunc, BiConsumer<Player, PlayerAnimationController> onStart, Consumer<PlayerAnimationController> onStop) {
        PMSTATUS = currentStaus;
        this.isFunc = isFunc;
        this.onStart = onStart;
        this.onStop = onStop;
    }

    public PlayerMotionStatus PMSTATUS;
    public Function<Player, Boolean> isFunc;
    public BiConsumer<Player, PlayerAnimationController> onStart;
    public Consumer<PlayerAnimationController> onStop;

}
