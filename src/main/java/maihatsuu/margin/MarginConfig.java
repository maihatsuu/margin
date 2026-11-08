package maihatsuu.margin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class MarginConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static MarginConfig instance = new MarginConfig();

	public boolean showCoordinates = true;
	public boolean showY = false;

	public static MarginConfig get() {
		return instance;
	}

	/** Reads the config file, creating it with defaults if missing. A broken file is left for the user to fix. */
	static void load() {
		Path path = path();

		if (Files.notExists(path)) {
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(path)) {
			MarginConfig loaded = GSON.fromJson(reader, MarginConfig.class);
			if (loaded != null) {
				instance = loaded;
			}
		} catch (IOException | JsonParseException e) {
			Margin.LOGGER.warn("Could not read {}, using defaults", path, e);
		}
	}

	public static void save() {
		Path path = path();
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(instance, writer);
		} catch (IOException e) {
			Margin.LOGGER.warn("Could not write {}", path, e);
		}
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve(Margin.MOD_ID + ".json");
	}
}
