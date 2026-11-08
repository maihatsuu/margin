package maihatsuu.margin.client;

import maihatsuu.margin.EmptyMapScale;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class MarginClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			int scale = stack.is(Items.MAP) ? EmptyMapScale.get(stack) : 0;
			if (scale > 0) {
				lines.add(Component.translatable("filled_map.scale", 1 << scale).withStyle(ChatFormatting.GRAY));
				lines.add(Component.translatable("filled_map.level", scale, EmptyMapScale.MAX).withStyle(ChatFormatting.GRAY));
			}
		});
	}
}
