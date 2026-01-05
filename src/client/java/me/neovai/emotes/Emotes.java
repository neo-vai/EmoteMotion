package me.neovai.emotes;

import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranimcore.animation.Animation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Emotes {

    public static final List<String> local_names = new ArrayList<>();

    public static ResourceLocation RUN;
    public static ResourceLocation GAIT;
    public static ResourceLocation JUMP ;

    public static final List<ResourceLocation> SWORD = new ArrayList<>();
    public static int SWORD_TICKS;

    public static final List<ResourceLocation> HAND = new ArrayList<>();
    public static int HAND_TICK;

    public static final List<ResourceLocation> AXE = new ArrayList<>();
    public static int AXE_TICK;

    public static ResourceLocation JUMPTOSTAY;
    public static ResourceLocation EAT;

    public static @Nullable ResourceLocation randomSword() {
        if (SWORD.isEmpty()) return null;
        return random(new ArrayList<>(SWORD));
    }

    public static @Nullable ResourceLocation randomAxe() {
        if (AXE.isEmpty()) return  null;
        return random(new ArrayList<>(AXE));
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

        AXE.add(ResourceLocation.parse("emotemotion:sword1"));
        AXE.add(ResourceLocation.parse("emotemotion:axe2"));
        AXE.add(ResourceLocation.parse("emotemotion:axe3"));
        AXE_TICK = 12;


        JUMPTOSTAY = ResourceLocation.parse("emotemotion:jump_to_stand");

        EAT = ResourceLocation.parse("emotemotion:eat");
    }

    public static void addAllLocalEmotes() {
        addLocalEmote(RUN);
        addLocalEmote(GAIT);
        addLocalEmote(JUMP);
        addListEmotes(SWORD);
        addListEmotes(AXE);
        addListEmotes(HAND);
        addLocalEmote(JUMPTOSTAY);
        addLocalEmote(EAT);
    }

    public static void addLocalEmote(Animation anim) {
        if (anim != null) {
            local_names.add(anim.getNameOrId());
        }
    }

    public static void addLocalEmote(ResourceLocation loc) {
        if (loc != null) {
            addLocalEmote(PlayerAnimResources.getAnimation(loc));
        }
    }

    public static void addListEmotes(List<ResourceLocation> list) {
        for (int i = 0; i < list.size(); i++) {
            local_names.add(PlayerAnimResources.getAnimation(list.get(i)).getNameOrId());
        }
    }

}
