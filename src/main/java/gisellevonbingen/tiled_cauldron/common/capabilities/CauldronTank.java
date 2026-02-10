package gisellevonbingen.tiled_cauldron.common.capabilities;

import java.util.Objects;

import gisellevonbingen.tiled_cauldron.common.CauldronFluidTransfom;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class CauldronTank extends SnapshotJournal<CauldronTank.Snapshot> implements ResourceHandler<FluidResource>
{
	static final class Snapshot
	{
		private final BlockState state;
		private final FluidResource fluid;
		private final int drainedAmount;

		private Snapshot(BlockState state, FluidResource fluid, int drainedAmount)
		{
			this.state = state;
			this.fluid = fluid;
			this.drainedAmount = drainedAmount;
		}
	}

	private final CauldronBlockEntity blockEntity;
	private FluidResource fluid = FluidResource.EMPTY;
	private int drainedAmount = 0;

	public CauldronTank(CauldronBlockEntity blockEntity)
	{
		this.blockEntity = blockEntity;

		CauldronFluidTransfom transform = CauldronFluidTransfom.getTransform(blockEntity.getBlockState());
		this.fluid = transform != null ? FluidResource.of(transform.fluid()) : FluidResource.EMPTY;
	}

	@Override
	public int size()
	{
		return 1;
	}

	@Override
	public FluidResource getResource(int index)
	{
		Objects.checkIndex(index, size());
		return this.getFluid();
	}

	public FluidResource getFluid()
	{
		return this.fluid;
	}

	@Override
	public long getAmountAsLong(int index)
	{
		Objects.checkIndex(index, size());
		if (this.getFluid().isEmpty() == true)
		{
			return 0;
		}

		return Math.max(0, this.getTankCapacity() - this.drainedAmount);
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource resource)
	{
		Objects.checkIndex(index, size());
		if (resource.isEmpty())
		{
			return this.getTankCapacity();
		}

		return this.isFluidValid(resource) == true ? this.getTankCapacity() : 0;
	}

	@Override
	public boolean isValid(int index, FluidResource resource)
	{
		Objects.checkIndex(index, size());
		TransferPreconditions.checkNonEmpty(resource);
		return this.isFluidValid(resource);
	}

	@Override
	public int insert(int index, FluidResource resource, int amount, TransactionContext transaction)
	{
		Objects.checkIndex(index, size());
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

		if (this.isRemoved() == true || this.isFluidValid(resource) == false)
		{
			return 0;
		}

		FluidResource current = this.getFluid();
		if (current.isEmpty() == false)
		{
			if (current.equals(resource) == false)
			{
				return 0;
			}

			int filling = Math.min(amount, this.drainedAmount);
			if (filling <= 0)
			{
				return 0;
			}

			this.updateSnapshots(transaction);
			this.drainedAmount -= filling;
			return filling;
		}

		int filling = this.getTankCapacity();
		if (amount < filling)
		{
			return 0;
		}

		this.fluid = resource;
		this.updateSnapshots(transaction);
		return filling;
	}

	@Override
	public int extract(int index, FluidResource resource, int amount, TransactionContext transaction)
	{
		Objects.checkIndex(index, size());
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

		if (this.isRemoved() == true)
		{
			return 0;
		}

		FluidResource fluid = this.getFluid();
		if (fluid.isEmpty() == true || fluid.equals(resource) == false)
		{
			return 0;
		}

		int available = this.getTankCapacity() - this.drainedAmount;
		if (available <= 0)
		{
			return 0;
		}

		int draining = Math.min(amount, available);
		this.updateSnapshots(transaction);
		this.drainedAmount += draining;

		if (this.drainedAmount >= this.getTankCapacity())
		{
			this.drainedAmount = 0;
			this.fluid = FluidResource.EMPTY;
		}

		return draining;
	}

	private int getTankCapacity()
	{
		return FluidType.BUCKET_VOLUME;
	}

	private boolean isFluidValid(FluidResource resource)
	{
		return resource.isComponentsPatchEmpty() == true && CauldronFluidTransfom.byFluid(resource.getFluid()) != null;
	}

	@Override
	protected Snapshot createSnapshot()
	{
		return new Snapshot(this.getBlockState(), this.fluid, this.drainedAmount);
	}

	@Override
	protected void revertToSnapshot(Snapshot snapshot)
	{
		Level level = this.getLevel();
		if (level == null)
		{
			return;
		}

		this.fluid = snapshot.fluid;
		this.drainedAmount = snapshot.drainedAmount;
		level.setBlock(this.getBlockPos(), snapshot.state, Block.UPDATE_ALL);
		level.invalidateCapabilities(this.getBlockPos());
	}

	@Override
	protected void onRootCommit(Snapshot originalState)
	{
		Level level = this.getLevel();
		if (level == null || this.isRemoved() == true)
		{
			return;
		}

		BlockPos pos = this.getBlockPos();
		CauldronFluidTransfom newTransform = CauldronFluidTransfom.byFluid(this.fluid.getFluid());

		level.setBlock(pos, newTransform != null ? newTransform.blockState() : Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
		level.invalidateCapabilities(pos);
	}

	public CauldronBlockEntity getBlockEntity()
	{
		return this.blockEntity;
	}

	private boolean isRemoved()
	{
		return this.blockEntity.isRemoved();
	}

	private BlockState getBlockState()
	{
		return this.blockEntity.getBlockState();
	}

	private Level getLevel()
	{
		return this.blockEntity.getLevel();
	}

	private BlockPos getBlockPos()
	{
		return this.blockEntity.getBlockPos();
	}

}
