package maihatsuu.margin.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalByteRef;
import maihatsuu.margin.EmptyMapScale;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EmptyMapItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmptyMapItem.class)
public class EmptyMapItemMixin {
	// use() consumes the item before creating the map, and an emptied stack has lost its data.
	@Inject(method = "use", at = @At("HEAD"))
	private void margin$rememberScale(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Share("scale") LocalByteRef scale) {
		scale.set((byte) EmptyMapScale.get(player.getItemInHand(hand)));
	}

	@ModifyArg(
		method = "use",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/MapItem;create(Lnet/minecraft/world/level/Level;IIBZZ)Lnet/minecraft/world/item/ItemStack;"
		),
		index = 3
	)
	private byte margin$applyScale(byte original, @Share("scale") LocalByteRef scale) {
		return scale.get();
	}
}
