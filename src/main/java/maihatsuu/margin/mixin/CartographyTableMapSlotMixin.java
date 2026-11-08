package maihatsuu.margin.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The table's top slot, an anonymous class that vanilla limits to filled maps. */
@Mixin(targets = "net.minecraft.world.inventory.CartographyTableMenu$3")
public class CartographyTableMapSlotMixin {
	@Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
	private void margin$acceptEmptyMaps(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.is(Items.MAP)) {
			cir.setReturnValue(true);
		}
	}
}
