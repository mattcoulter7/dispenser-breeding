package com.dispenserbreeding.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigManager {
	private static final String FILE_NAME = "dispenserbreeding.json5";
	private static final Jankson JANKSON = Jankson.builder().build();

	private static DispenserBreedingConfig config = new DispenserBreedingConfig();

	private ConfigManager() {
	}

	public static void load() {
		Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

		try {
			if (Files.notExists(configPath)) {
				save(configPath);
				return;
			}

			JsonObject configJson = JANKSON.load(configPath.toFile());
			DispenserBreedingConfig loaded = JANKSON.fromJson(configJson, DispenserBreedingConfig.class);
			config = loaded == null ? new DispenserBreedingConfig() : loaded;

			// Re-save to include newly added fields with default values.
			save(configPath);
		} catch (IOException | SyntaxError e) {
			config = new DispenserBreedingConfig();
			save(configPath);
		} catch (Exception e) {
			config = new DispenserBreedingConfig();
		}
	}

	public static void save() {
		save(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
	}

	private static void save(Path configPath) {
		try {
			Files.createDirectories(configPath.getParent());
			JsonObject json = JANKSON.toJson(config);
			Files.writeString(configPath, json.toJson(true, true), StandardCharsets.UTF_8);
		} catch (IOException ignored) {
		}
	}

	public static DispenserBreedingConfig get() {
		return config;
	}
}
