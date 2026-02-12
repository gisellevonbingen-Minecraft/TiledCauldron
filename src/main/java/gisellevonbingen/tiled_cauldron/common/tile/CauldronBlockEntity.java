package gisellevonbingen.tiled_cauldron.common.tile;

import gisellevonbingen.tiled_cauldron.common.capabilities.CauldronTank;
import gisellevonbingen.tiled_cauldron.common.registries.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CauldronBlockEntity extends BlockEntity
{
	private final CauldronTank fluidTank = new CauldronTank(this);

	public CauldronBlockEntity(BlockPos blockPos, BlockState state)
	{
		super(ModBlockEntityTypes.CAULDRON.get(), blockPos, state);
	}

	public CauldronTank getFluidTank()
	{
		return this.fluidTank;
	}

}
