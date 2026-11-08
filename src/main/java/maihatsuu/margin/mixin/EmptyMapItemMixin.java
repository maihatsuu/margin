package maihatsuu.margin.mixin;

import maihatsuu.margin.MarginConfig;
import net.minecraft.world.item.EmptyMapItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EmptyMapItem.class)
public class EmptyMapItemMixin {
	@ModifyArg(
		method = "use",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/world/level/Level;IIBZZ)Lnet/minecraft/world/item/ItemStack;"
		),
		index = 3
	)
	private byte margin$useDefaultScale(byte scale) {
		return MarginConfig.get().mapScale();
	}
}
