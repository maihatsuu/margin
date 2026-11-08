package maihatsuu.margin.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import maihatsuu.margin.EmptyMapScale;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CartographyTableMenu.class)
public class CartographyTableMenuMixin {
	@Shadow @Final public Container container;
	@Shadow @Final private ContainerLevelAccess access;
	@Shadow @Final private ResultContainer resultContainer;

	/** Shift-clicks an empty map into the top slot, unless a filled map there means it's wanted for copying. */
	@ModifyExpressionValue(
		method = "quickMoveStack",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0)
	)
	private boolean margin$sendEmptyMapsToTop(boolean isFilledMap, @Local(ordinal = 1) ItemStack moved) {
		return isFilledMap || moved.is(Items.MAP) && !container.getItem(CartographyTableMenu.MAP_SLOT).is(Items.FILLED_MAP);
	}

	/** An empty map plus paper gives one empty map a level further out, as for filled maps. */
	@Inject(method = "setupResultSlot", at = @At("HEAD"), cancellable = true)
	private void margin$zoomEmptyMap(ItemStack map, ItemStack additional, ItemStack currentResult, CallbackInfo ci) {
		if (!map.is(Items.MAP)) {
			return;
		}
		ci.cancel();

		access.execute((level, pos) -> {
			int scale = EmptyMapScale.get(map);
			if (additional.is(Items.PAPER) && scale < EmptyMapScale.MAX) {
				ItemStack result = map.copyWithCount(1);
				EmptyMapScale.set(result, scale + 1);
				if (!ItemStack.matches(result, currentResult)) {
					resultContainer.setItem(CartographyTableMenu.RESULT_SLOT, result);
				}
			} else {
				resultContainer.removeItemNoUpdate(CartographyTableMenu.RESULT_SLOT);
			}
			((AbstractContainerMenu) (Object) this).broadcastChanges();
		});
	}
}
