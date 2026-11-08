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
	private static final int MAX_MAP_SCALE = 4;

	private static MarginConfig instance = new MarginConfig();

	public boolean showCoordinates = true;
	public boolean showY = false;
	public int defaultMapScale = 0;

	public static MarginConfig get() {
		return instance;
	}

	public byte mapScale() {
		return (byte) Math.clamp(defaultMapScale, 0, MAX_MAP_SCALE);
	}

	/** Reads the config file, creating it with defaults if missing. A broken file is left for the user to fix. */
	static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(Margin.MOD_ID + ".json");

		if (Files.notExists(path)) {
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(instance, writer);
			} catch (IOException e) {
				Margin.LOGGER.warn("Could not write {}", path, e);
			}
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
}
