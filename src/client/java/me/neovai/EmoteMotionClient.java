package me.neovai;

import me.neovai.config.Config;
import me.neovai.emotes.Emotes;
import me.neovai.player.PlayerController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;


public class EmoteMotionClient implements ClientModInitializer {

    public static PlayerController playerController;

	@Override
	public void onInitializeClient() {
        Emotes.init();

        playerController = new PlayerController();

        // DEBUG ONLY METHOD
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("emotemotionstatus")
                    .executes(context -> {
                        playerController.toggleDebug();
                        return 1;
                    }));
        });

        Config.modifyEmoteCraftConfig();
	}
}