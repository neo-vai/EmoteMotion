package me.neovai.emotes;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Emotes {

    public static ResourceLocation RUN;
    public static ResourceLocation GAIT;
    public static ResourceLocation JUMP ;

    public static final List<ResourceLocation> SWORD = new ArrayList<>();
    public static int SWORD_TICKS;

    public static final List<ResourceLocation> HAND = new ArrayList<>();
    public static int HAND_TICK;

    public static ResourceLocation JUMPTOSTAY;
    public static ResourceLocation EAT;

    public static @Nullable ResourceLocation randomSword() {
        if (SWORD.isEmpty()) return null;
        return random(new ArrayList<>(SWORD));
    }

    public static @Nullable ResourceLocation randomHand() {
        if (HAND.isEmpty()) return null;
        return random(new ArrayList<>(HAND));
    }


    private static @Nullable ResourceLocation random(@NotNull List<ResourceLocation> options) {
        if (options.isEmpty()) return null;
        int index = (int) (Math.random() * options.size());
        return options.get(index);
    }

    public static void init() {
        RUN = ResourceLocation.parse("emotemotion:run");
        GAIT = ResourceLocation.parse("emotemotion:gait");
        JUMP = ResourceLocation.parse("emotemotion:jump");

        SWORD.add(ResourceLocation.parse("emotemotion:sword1"));
        SWORD.add(ResourceLocation.parse("emotemotion:sword2"));
        SWORD.add(ResourceLocation.parse("emotemotion:sword3"));
        SWORD_TICKS = 11;

        HAND.add(ResourceLocation.parse("emotemotion:hand1"));
        HAND.add(ResourceLocation.parse("emotemotion:hand2"));
        HAND.add(ResourceLocation.parse("emotemotion:hand3"));
        HAND_TICK = 8;

        JUMPTOSTAY = ResourceLocation.parse("emotemotion:jump_to_stand");

        EAT = ResourceLocation.parse("emotemotion:eat");
    }

}
