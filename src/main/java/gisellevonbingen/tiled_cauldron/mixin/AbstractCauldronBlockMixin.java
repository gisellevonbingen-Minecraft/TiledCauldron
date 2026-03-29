package gisellevonbingen.tiled_cauldron.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gisellevonbingen.tiled_cauldron.common.TiledCauldron;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin implements EntityBlock
{
	protected boolean shouldChangedStateKeepBlockEntity(BlockState state)
	{
		return state.getBlock() instanceof AbstractCauldronBlock;
	}

	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult,
		CallbackInfoReturnable<InteractionResult> cir)
	{
		if (TiledCauldron.shouldBlockVanillaItemInteraction(level, pos, state, stack))
		{
			cir.setReturnValue(InteractionResult.TRY_WITH_EMPTY_HAND);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new CauldronBlockEntity(pos, state);
	}

}
