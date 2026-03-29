package gisellevonbingen.tiled_cauldron.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gisellevonbingen.tiled_cauldron.common.TiledCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

@Mixin(CauldronBlock.class)
public class CauldronBlockMixin
{
	@Inject(method = "handlePrecipitation", at = @At("HEAD"), cancellable = true)
	private void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation, CallbackInfo ci)
	{
		if (TiledCauldron.shouldBlockVanillaWorldMutation(level, pos, state))
		{
			ci.cancel();
		}
	}

	@Inject(method = "receiveStalactiteDrip", at = @At("HEAD"), cancellable = true)
	private void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid, CallbackInfo ci)
	{
		if (TiledCauldron.shouldBlockVanillaWorldMutation(level, pos, state))
		{
			ci.cancel();
		}
	}
}
