package maihatsuu.margin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * The zoom level stored on an empty map, which the filled map made from it starts at.
 * Kept in vanilla's custom_data component so that clients without the mod can still receive these items.
 */
public final class EmptyMapScale {
	public static final int MAX = 4;
	private static final String KEY = Margin.MOD_ID + ":scale";

	private EmptyMapScale() {
	}

	public static int get(ItemStack emptyMap) {
		CustomData data = emptyMap.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		return Mth.clamp(data.copyTag().getInt(KEY), 0, MAX);
	}

	public static void set(ItemStack emptyMap, int scale) {
		CustomData.update(DataComponents.CUSTOM_DATA, emptyMap, tag -> tag.putInt(KEY, scale));
	}
}
