package maihatsuu.margin.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import maihatsuu.margin.MarginConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	// In renderMap's pose the map is 0..128, with a paper border out to -7 and 135.
	// The text sits in the bottom border, so it is always ink on paper, however dark the map is.
	@Unique private static final float MAP_CENTRE_X = 64.0F;
	@Unique private static final float BORDER_CENTRE_Y = 131.5F;
	@Unique private static final float MAX_TEXT_WIDTH = 132.0F;
	@Unique private static final float MAX_TEXT_SCALE = 0.7F;
	@Unique private static final float GLYPH_HEIGHT = 7.0F;
	@Unique private static final float TOWARDS_VIEWER = -0.1F;
	@Unique private static final int INK_COLOUR = 0xFF3B2A1A;

	@Inject(method = "renderMap", at = @At("TAIL"))
	private void margin$writeCoordinates(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack stack, CallbackInfo ci) {
		MarginConfig config = MarginConfig.get();
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (!config.showCoordinates || player == null) {
			return;
		}

		String text = "X " + player.getBlockX()
			+ (config.showY ? "  Y " + player.getBlockY() : "")
			+ "  Z " + player.getBlockZ();

		Font font = minecraft.font;
		int width = font.width(text);
		float scale = Math.min(MAX_TEXT_SCALE, MAX_TEXT_WIDTH / width);

		poseStack.pushPose();
		poseStack.translate(MAP_CENTRE_X - width * scale / 2.0F, BORDER_CENTRE_Y - GLYPH_HEIGHT * scale / 2.0F, TOWARDS_VIEWER);
		poseStack.scale(scale, scale, 1.0F);
		font.drawInBatch(text, 0.0F, 0.0F, INK_COLOUR, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
		poseStack.popPose();
	}
}
