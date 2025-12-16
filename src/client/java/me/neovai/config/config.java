package me.neovai.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class config {

    public static void modifyEmoteCraftConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path filePath = configDir.resolve("emotecraft.json");

        JsonObject json;
        boolean recreated = false;

        try {
            if (Files.exists(filePath)) {
                try (Reader reader = Files.newBufferedReader(filePath)) {
                    json = gson.fromJson(reader, JsonObject.class);
                    if (json == null) {
                        json = new JsonObject();
                        recreated = true;
                    }
                } catch (JsonSyntaxException e) {
                    json = new JsonObject();
                    recreated = true;
                }
            } else {
                Files.createDirectories(filePath.getParent());
                json = new JsonObject();
                recreated = true;
            }

            if (recreated) {
                json.addProperty("config_version", 1);
            }

            float currentThreshold = json.has("stopthreshold") ? json.get("stopthreshold").getAsFloat() : Float.MIN_VALUE;
            if (recreated || currentThreshold < 2.0f) {
                json.addProperty("stopthreshold", 2.0f);
            }

            json.addProperty("perspective", false);

            try (Writer writer = Files.newBufferedWriter(filePath)) {
                gson.toJson(json, writer);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to handle emotecraft.json", e);
        }
    }





}
