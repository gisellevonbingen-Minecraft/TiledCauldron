package gisellevonbingen.tiled_cauldron.common.tile;

import gisellevonbingen.tiled_cauldron.common.CauldronFluidTransfom;
import gisellevonbingen.tiled_cauldron.common.capabilities.CauldronTank;
import gisellevonbingen.tiled_cauldron.common.registries.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public class CauldronBlockEntity extends BlockEntity
{
	private CauldronTank fluidTank = new CauldronTank(this)
	{
		@Override
		protected void onFill(Fluid prev, Fluid next)
		{
			replaceBlockAndUpdate(CauldronFluidTransfom.byFluid(next).blockState());
		};

		@Override
		protected void onDrain()
		{
			replaceBlockAndUpdate(Blocks.CAULDRON.defaultBlockState());
		};

	};

	public CauldronBlockEntity(BlockPos blockPos, BlockState state)
	{
		super(ModBlockEntityTypes.CAULDRON.get(), blockPos, state);
	}

	public void replaceBlockAndUpdate(BlockState newBlockState)
	{
		BlockPos blockPos = this.getBlockPos();
		Level level = this.getLevel();
		level.setBlockAndUpdate(blockPos, newBlockState);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if (cap == ForgeCapabilities.FLUID_HANDLER)
		{
			return LazyOptional.of(this::getFluidTank).cast();
		}

		return super.getCapability(cap, side);
	}

	public CauldronTank getFluidTank()
	{
		return this.fluidTank;
	}

	public CauldronFluidTransfom getTransform()
	{
		return CauldronFluidTransfom.getTransform(this.getBlockState());
	}

}
