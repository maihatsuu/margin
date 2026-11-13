package maihatsuu.margin.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MapItemSavedData.class)
public class MapItemSavedDataMixin {
	@Shadow @Final public ResourceKey<Level> dimension;

	/**
	 * Past the off-map range, vanilla drops the player marker unless the map has unlimited tracking
	 * (explorer maps). Keep it on every map, as the far-away off-limits marker.
	 */
	@ModifyExpressionValue(
		method = "addDecoration",
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;unlimitedTracking:Z")
	)
	private boolean margin$alwaysTrackFarPlayers(boolean unlimitedTracking) {
		return true;
	}

	/**
	 * Vanilla gives the off-map and off-limits player markers no rotation. Give them the same
	 * rotation as the on-map arrow, including its random spin in the Nether, so they show which way
	 * the player faces.
	 */
	@ModifyArg(
		method = "addDecoration",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/saveddata/maps/MapDecoration;<init>(Lnet/minecraft/core/Holder;BBBLjava/util/Optional;)V"
		),
		index = 3
	)
	private byte margin$rotateOffMapMarkers(
		byte rot,
		@Local Holder<MapDecorationType> type,
		@Local(argsOnly = true) @Nullable LevelAccessor level,
		@Local(argsOnly = true, ordinal = 2) double yaw
	) {
		if (!type.is(MapDecorationTypes.PLAYER_OFF_MAP) && !type.is(MapDecorationTypes.PLAYER_OFF_LIMITS)) {
			return rot;
		}
		if (dimension == Level.NETHER && level != null) {
			int l = (int) (level.getLevelData().getDayTime() / 10L);
			return (byte) (l * l * 34187121 + l * 121 >> 15 & 15);
		}
		return (byte) ((yaw + (yaw < 0.0 ? -8.0 : 8.0)) * 16.0 / 360.0);
	}
}
