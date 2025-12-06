package me.neovai;


import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.enums.PlayState;
import me.neovai.player.PlayerController;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;


public class EmoteMotionClient implements ClientModInitializer {

    public static ResourceLocation LAYER_ID = ResourceLocation.fromNamespaceAndPath("emotemotion", "layer");

	@Override
	public void onInitializeClient() {

        // EmoteCraft Layer innit
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(LAYER_ID, 900,
                player -> new PlayerAnimationController(player,
                        (controller, state, animSetter) -> PlayState.STOP
                )
        );

        new PlayerController();

	}
}