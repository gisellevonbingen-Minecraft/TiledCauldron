package gisellevonbingen.tiled_cauldron.mixin;

import org.spongepowered.asm.mixin.Mixin;

import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin implements EntityBlock
{
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new CauldronBlockEntity(pos, state);
	}

}
