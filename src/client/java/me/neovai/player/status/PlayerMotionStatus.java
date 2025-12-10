package me.neovai.player.status;

public enum PlayerMotionStatus {
    STAY(0),
    WALKING(1),
    SPRINTING(2),
    JUMPING(3),
    SHIFTING(3),
    ATTACKING(5);

    private final int priority;

    PlayerMotionStatus(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return priority;
    }
}
