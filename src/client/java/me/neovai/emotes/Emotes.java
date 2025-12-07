package me.neovai.emotes;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Emotes {

    public static ResourceLocation RUN;
    public static ResourceLocation GAIT;
    public static ResourceLocation JUMP ;

    public static final List<ResourceLocation> SWORD = new ArrayList<>();
    public static int SWORD_TICKS;

    public static ResourceLocation randomSword(@Nullable ResourceLocation last) {
        if (SWORD.isEmpty()) return null;

        List<ResourceLocation> options = new ArrayList<>(SWORD);

        if (last != null) {
            options.remove(last);
        }

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
        SWORD_TICKS = 20;
    }

}
