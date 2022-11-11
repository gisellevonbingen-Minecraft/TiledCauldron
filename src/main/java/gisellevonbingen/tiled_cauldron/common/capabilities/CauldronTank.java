package gisellevonbingen.tiled_cauldron.common.capabilities;

import gisellevonbingen.tiled_cauldron.common.CauldronFluidTransfom;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CauldronTank implements IFluidHandler
{
	private final CauldronBlockEntity blockEntity;

	public CauldronTank(CauldronBlockEntity blockEntity)
	{
		this.blockEntity = blockEntity;
	}

	@Override
	public int getTanks()
	{
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank)
	{
		return this.getFluid();
	}

	public FluidStack getFluid()
	{
		CauldronBlockEntity blockEntity = this.getBlockEntity();

		if (blockEntity.isRemoved() == true)
		{
			return FluidStack.EMPTY;
		}

		CauldronFluidTransfom transform = CauldronFluidTransfom.getTransform(this.getBlockEntity().getBlockState());

		if (transform != null)
		{
			return new FluidStack(transform.fluid(), this.getTankCapacity());
		}
		else
		{
			return FluidStack.EMPTY;
		}

	}

	@Override
	public int getTankCapacity(int tank)
	{
		return this.getTankCapacity();
	}

	public int getTankCapacity()
	{
		return FluidAttributes.BUCKET_VOLUME;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack)
	{
		return this.isFluidValid(stack);
	}

	public boolean isFluidValid(FluidStack stack)
	{
		return this.isFluidValid(stack.getFluid());
	}

	public boolean isFluidValid(Fluid fluid)
	{
		return CauldronFluidTransfom.byFluid(fluid) != null;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		if (this.getBlockEntity().isRemoved() == true || resource.isEmpty() == true || this.isFluidValid(resource) == false)
		{
			return 0;
		}

		FluidStack fluid = this.getFluid();

		if (fluid.isEmpty() == true || fluid.isFluidEqual(resource) == false)
		{
			int filling = this.getTankCapacity();

			if (resource.getAmount() >= filling)
			{
				if (action.execute() == true)
				{
					this.onFill(fluid.getFluid(), resource.getFluid());
				}

				return filling;
			}

		}

		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action)
	{
		if (this.getBlockEntity().isRemoved() == true || resource.isEmpty() == true || this.getFluid().isFluidEqual(resource) == false)
		{
			return FluidStack.EMPTY;
		}
		else
		{
			return this.drain(resource.getAmount(), action);
		}

	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action)
	{
		if (this.getBlockEntity().isRemoved() == true)
		{
			return FluidStack.EMPTY;
		}

		FluidStack fluid = this.getFluid();

		if (maxDrain >= fluid.getAmount())
		{
			if (action.execute() == true)
			{
				this.onDrain();
			}

			return fluid.copy();
		}
		else
		{
			return FluidStack.EMPTY;
		}

	}

	protected void onFill(Fluid prev, Fluid next)
	{

	}

	protected void onDrain()
	{

	}

	public CauldronBlockEntity getBlockEntity()
	{
		return this.blockEntity;
	}

}
