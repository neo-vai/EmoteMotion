package me.neovai.config;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.main.config.ClientConfig;

public class config {

    public static void modifyEmoteCraftConfig() {
        ClientConfig config = PlatformTools.getConfig();
        config.enablePerspective.set(false);
        if (config.stopThreshold.get() < 5.0f) {
            config.stopThreshold.set(5.0f);
        }
    }
}
