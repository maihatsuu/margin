package maihatsuu.margin.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import maihatsuu.margin.MarginConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	// In renderMap's pose the map is 0..128, with a paper border out to -7 and 135.
	// Coordinates sit in the bottom border and the biome name in the top, each on a patch of the
	// border's own colour. A patch fills the cream band only, hiding its ragged inner edge but leaving the outer rim whole.
	@Unique private static final float MAP_CENTRE_X = 64.0F;
	@Unique private static final float BOTTOM_TEXT_Y = 130.5F;
	@Unique private static final float TOP_TEXT_Y = -2.5F;
	@Unique private static final float MAX_TEXT_WIDTH = 132.0F;
	@Unique private static final float MAX_TEXT_SCALE = 0.6F;
	@Unique private static final float GLYPH_HEIGHT = 7.0F;
	@Unique private static final float TOWARDS_VIEWER = -0.1F;
	@Unique private static final int INK_COLOUR = 0xFF3B2A1A;
	@Unique private static final int PAPER_COLOUR = 0xFFD6BE96;
	@Unique private static final float BOTTOM_PATCH_TOP_Y = 128.0F;
	@Unique private static final float BOTTOM_PATCH_BOTTOM_Y = 132.78F;
	@Unique private static final float TOP_PATCH_TOP_Y = -4.78F;
	@Unique private static final float TOP_PATCH_BOTTOM_Y = 0.0F;
	@Unique private static final float PATCH_PADDING_X = 1.0F;
	@Unique private static final float PATCH_Z = -0.05F;

	@Inject(method = "renderMap", at = @At("TAIL"))
	private void margin$writeInMargins(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack stack, CallbackInfo ci) {
		MarginConfig config = MarginConfig.get();
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		ClientLevel level = minecraft.level;
		if (player == null || level == null) {
			return;
		}

		if (config.showBiome) {
			level.getBiome(player.blockPosition()).unwrapKey().ifPresent(biome -> {
				String name = I18n.get(Util.makeDescriptionId("biome", biome.location()));
				margin$writeLine(poseStack, buffer, packedLight, name, TOP_TEXT_Y, TOP_PATCH_TOP_Y, TOP_PATCH_BOTTOM_Y);
			});
		}

		if (config.showCoordinates) {
			String text = "X " + player.getBlockX()
				+ (config.showY ? "  Y " + player.getBlockY() : "")
				+ "  Z " + player.getBlockZ();
			margin$writeLine(poseStack, buffer, packedLight, text, BOTTOM_TEXT_Y, BOTTOM_PATCH_TOP_Y, BOTTOM_PATCH_BOTTOM_Y);
		}
	}

	@Unique
	private static void margin$writeLine(PoseStack poseStack, MultiBufferSource buffer, int packedLight, String text, float textY, float patchTop, float patchBottom) {
		Font font = Minecraft.getInstance().font;
		int width = font.width(text);
		float scale = Math.min(MAX_TEXT_SCALE, MAX_TEXT_WIDTH / width);

		float halfWidth = width * scale / 2.0F;
		float patchLeft = MAP_CENTRE_X - halfWidth - PATCH_PADDING_X;
		float patchRight = MAP_CENTRE_X + halfWidth + PATCH_PADDING_X;
		Matrix4f map = poseStack.last().pose();
		VertexConsumer patch = buffer.getBuffer(RenderType.textBackground());
		patch.addVertex(map, patchLeft, patchBottom, PATCH_Z).setColor(PAPER_COLOUR).setLight(packedLight);
		patch.addVertex(map, patchRight, patchBottom, PATCH_Z).setColor(PAPER_COLOUR).setLight(packedLight);
		patch.addVertex(map, patchRight, patchTop, PATCH_Z).setColor(PAPER_COLOUR).setLight(packedLight);
		patch.addVertex(map, patchLeft, patchTop, PATCH_Z).setColor(PAPER_COLOUR).setLight(packedLight);

		poseStack.pushPose();
		poseStack.translate(MAP_CENTRE_X - halfWidth, textY - GLYPH_HEIGHT * scale / 2.0F, TOWARDS_VIEWER);
		poseStack.scale(scale, scale, 1.0F);
		font.drawInBatch(text, 0.0F, 0.0F, INK_COLOUR, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
		poseStack.popPose();
	}
}
