package maihatsuu.margin.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

/** Mod Menu's config button. Only loaded by Mod Menu; the screen needs Cloth Config, which is optional. */
public class MarginModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
			return ModMenuApi.super.getModConfigScreenFactory();
		}
		return MarginConfigScreen::create; // separate class, so Cloth classes only load when Cloth is installed
	}
}
