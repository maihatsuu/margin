package maihatsuu.margin.client;

import maihatsuu.margin.MarginConfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Cloth Config screen for config/margin.json. */
final class MarginConfigScreen {
	private MarginConfigScreen() {
	}

	static Screen create(Screen parent) {
		MarginConfig config = MarginConfig.get();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("config.margin.title"))
				.setSavingRunnable(MarginConfig::save);
		ConfigEntryBuilder entries = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.margin.category.general"));

		general.addEntry(entries.startBooleanToggle(Component.translatable("config.margin.showCoordinates"), config.showCoordinates)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("config.margin.showCoordinates.tooltip"))
				.setSaveConsumer(v -> config.showCoordinates = v)
				.build());
		general.addEntry(entries.startBooleanToggle(Component.translatable("config.margin.showY"), config.showY)
				.setDefaultValue(false)
				.setTooltip(Component.translatable("config.margin.showY.tooltip"))
				.setSaveConsumer(v -> config.showY = v)
				.build());

		return builder.build();
	}
}
