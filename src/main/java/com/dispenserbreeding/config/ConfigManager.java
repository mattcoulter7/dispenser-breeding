package com.dispenserbreeding.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;

public final class ConfigManager {
	private static final String FILE_NAME = "dispenserbreeding.json5";

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

			try (JsonReader reader = JsonReader.json5(configPath)) {
				DispenserBreedingConfig loaded = reader.read(DispenserBreedingConfig.class);
				config = loaded == null ? new DispenserBreedingConfig() : loaded;
			}
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
			try (JsonWriter writer = JsonWriter.json5(configPath, JsonFormat.pretty())) {
				writer.write(config);
			}
		} catch (IOException ignored) {
		}
	}

	public static DispenserBreedingConfig get() {
		return config;
	}
}
