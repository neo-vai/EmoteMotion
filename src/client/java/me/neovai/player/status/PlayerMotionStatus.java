package me.neovai.player.status;

public enum PlayerMotionStatus {
    STAY(0),
    WALKING(1),
    SPRINTING(2),
    JUMPING(3),
    FALLING(4),
    SWIMMING(5),
    SHIFTING(6),
    USE_ANIMATION(7),
    ATTACKING(8),
    EATING(9),
    EMOTECRAFT_ANIMATION(100);

    private final int priority;

    PlayerMotionStatus(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
}
