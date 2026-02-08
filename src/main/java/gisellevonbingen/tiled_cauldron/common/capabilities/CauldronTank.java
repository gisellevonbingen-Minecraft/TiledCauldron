package gisellevonbingen.tiled_cauldron.common.capabilities;

import java.util.HashMap;
import java.util.Map;
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
	private static final Map<Location, CauldronTank> WRAPPERS = new HashMap<>();

	private record Location(Level level, BlockPos pos) {}

	public static CauldronTank get(Level level, BlockPos pos)
	{
		Location key = new Location(level, pos.immutable());
		return WRAPPERS.computeIfAbsent(key, location -> new CauldronTank(location.level(), location.pos()));
	}

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
	private final Level level;
	private final BlockPos blockPos;
	private FluidResource drainedFluid = FluidResource.EMPTY;
	private int drainedAmount;

	public CauldronTank(CauldronBlockEntity blockEntity)
	{
		this.blockEntity = blockEntity;
		this.level = null;
		this.blockPos = null;
	}

	public CauldronTank(Level level, BlockPos blockPos)
	{
		this.blockEntity = null;
		this.level = level;
		this.blockPos = blockPos;
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
		if (this.isRemoved() == true)
		{
			return FluidResource.EMPTY;
		}

		BlockState state = this.getBlockState();
		this.syncBuffer(state);
		CauldronFluidTransfom transform = CauldronFluidTransfom.getTransform(state);

		if (transform != null)
		{
			return FluidResource.of(transform.fluid());
		}
		else
		{
			return FluidResource.EMPTY;
		}

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

		CauldronFluidTransfom transform = CauldronFluidTransfom.byFluid(resource.getFluid());
		if (transform == null)
		{
			return 0;
		}

		this.setBlockState(transform.blockState(), transaction);
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
			this.drainedFluid = FluidResource.EMPTY;
			Level level = this.getLevel();
			if (level != null)
			{
				level.setBlock(this.getBlockPos(), Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
				level.invalidateCapabilities(this.getBlockPos());
			}
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

	private void setBlockState(BlockState newState, TransactionContext transaction)
	{
		Level level = this.getLevel();
		if (level == null)
		{
			return;
		}

		this.updateSnapshots(transaction);
		level.setBlock(this.getBlockPos(), newState, Block.UPDATE_ALL);
		level.invalidateCapabilities(this.getBlockPos());
	}

	@Override
	protected Snapshot createSnapshot()
	{
		return new Snapshot(this.getBlockState(), this.drainedFluid, this.drainedAmount);
	}

	@Override
	protected void revertToSnapshot(Snapshot snapshot)
	{
		Level level = this.getLevel();
		if (level == null)
		{
			return;
		}

		this.drainedFluid = snapshot.fluid;
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
		BlockState currentState = level.getBlockState(pos);

		if (currentState == originalState.state)
		{
			return;
		}

		level.setBlock(pos, originalState.state, Block.UPDATE_ALL);
		level.setBlockAndUpdate(pos, currentState);
		level.invalidateCapabilities(pos);
	}

	public CauldronBlockEntity getBlockEntity()
	{
		return this.blockEntity;
	}

	private boolean isRemoved()
	{
		if (this.blockEntity != null)
		{
			return this.blockEntity.isRemoved();
		}

		return this.level == null;
	}

	private BlockState getBlockState()
	{
		if (this.blockEntity != null)
		{
			return this.blockEntity.getBlockState();
		}

		Level level = this.getLevel();
		if (level == null)
		{
			return Blocks.AIR.defaultBlockState();
		}

		return level.getBlockState(this.blockPos);
	}

	private void syncBuffer(BlockState state)
	{
		CauldronFluidTransfom transform = CauldronFluidTransfom.getTransform(state);
		if (transform == null)
		{
			this.drainedAmount = 0;
			this.drainedFluid = FluidResource.EMPTY;
			return;
		}

		FluidResource current = FluidResource.of(transform.fluid());
		if (this.drainedFluid.isEmpty() == true || this.drainedFluid.equals(current) == false)
		{
			this.drainedFluid = current;
			this.drainedAmount = 0;
		}
	}

	private Level getLevel()
	{
		if (this.blockEntity != null)
		{
			return this.blockEntity.getLevel();
		}

		return this.level;
	}

	private BlockPos getBlockPos()
	{
		if (this.blockEntity != null)
		{
			return this.blockEntity.getBlockPos();
		}

		return this.blockPos;
	}

}
