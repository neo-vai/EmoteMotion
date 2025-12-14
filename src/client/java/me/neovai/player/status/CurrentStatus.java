package me.neovai.player.status;

import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.Function;

public class CurrentStatus {

    public CurrentStatus(PlayerMotionStatus currentStaus, Function<Player, Boolean> isFunc, Consumer<Player> onStart, Runnable onStop) {
        PMSTATUS = currentStaus;
        this.isFunc = isFunc;
        this.onStart = onStart;
        this.onStop = onStop;
    }

    // First > Second
    public static boolean compareStatus(PlayerMotionStatus first, PlayerMotionStatus second) {
        return first.getPriority() > second.getPriority();
    }

    public PlayerMotionStatus PMSTATUS;
    public Function<Player, Boolean> isFunc;
    public Consumer<Player> onStart;
    public Runnable onStop;

}
