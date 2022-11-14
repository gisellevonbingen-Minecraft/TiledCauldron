package gisellevonbingen.tiled_cauldron.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public record CauldronFluidTransfom(Fluid fluid, BlockState blockState)
{
	private static final List<CauldronFluidTransfom> LIST = new ArrayList<>();
	private static final List<CauldronFluidTransfom> LIST_READONLY = Collections.unmodifiableList(LIST);
	private static final Map<Fluid, CauldronFluidTransfom> BY_FLUIDS = new HashMap<>();
	private static final Map<Fluid, CauldronFluidTransfom> BY_FLUIDS_READONLY = Collections.unmodifiableMap(BY_FLUIDS);

	public static void bootStrap()
	{
		register(Fluids.WATER, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MAX_FILL_LEVEL));
		register(Fluids.LAVA, Blocks.LAVA_CAULDRON.defaultBlockState());
	}

	public static CauldronFluidTransfom getTransform(BlockState cauldronBlockState)
	{
		if (cauldronBlockState.getBlock() instanceof AbstractCauldronBlock cauldronBlock && cauldronBlock.isFull(cauldronBlockState) == true)
		{
			for (CauldronFluidTransfom transform : CauldronFluidTransfom.values())
			{
				if (cauldronBlock == transform.blockState.getBlock())
				{
					return transform;
				}

			}

		}

		return null;
	}

	public static DispenseItemBehavior wrapDispenseItemBehavior(BucketItem bucket, DispenseItemBehavior fallback)
	{
		if (bucket == Items.BUCKET)
		{
			return wrapFillBucket(fallback);
		}

		CauldronFluidTransfom byFluid = CauldronFluidTransfom.byFluid(bucket.getFluid());

		if (byFluid != null)
		{
			return byFluid.wrapEmptyBucket(fallback);
		}
		else
		{
			return fallback;
		}

	}

	public static DispenseItemBehavior wrapFillBucket(DispenseItemBehavior fallback)
	{
		return new DispenseItemBehavior()
		{
			@Override
			public ItemStack dispense(BlockSource source, ItemStack item)
			{
				Level level = source.getLevel();
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

				if (level.getBlockEntity(pos) instanceof CauldronBlockEntity blockEntity)
				{
					CauldronFluidTransfom transform = blockEntity.getTransform();

					if (transform != null)
					{
						blockEntity.replaceBlockAndUpdate(Blocks.CAULDRON.defaultBlockState());
						return new ItemStack(transform.fluid().getBucket());
					}

				}

				return fallback.dispense(source, item);
			}

		};

	}

	public static List<CauldronFluidTransfom> values()
	{
		return LIST_READONLY;
	}

	public static Map<Fluid, CauldronFluidTransfom> byFluids()
	{
		return BY_FLUIDS_READONLY;
	}

	public static CauldronFluidTransfom byFluid(Fluid fluid)
	{
		return BY_FLUIDS.get(fluid);
	}

	public static void register(Fluid fluid, BlockState blockState)
	{
		register(new CauldronFluidTransfom(fluid, blockState));
	}

	public static void register(CauldronFluidTransfom transfom)
	{
		if (transfom.blockState().getBlock() instanceof AbstractCauldronBlock)
		{
			if (BY_FLUIDS.containsKey(transfom.fluid()) == false)
			{
				LIST.add(transfom);
				BY_FLUIDS.put(transfom.fluid(), transfom);
			}
			else
			{
				throw new IllegalArgumentException("already registered fluid: " + transfom.fluid().getRegistryName());
			}

		}
		else
		{
			throw new IllegalArgumentException("blockState.getBlock() should be extends from " + AbstractCauldronBlock.class.getName());
		}

	}

	public DispenseItemBehavior wrapEmptyBucket(DispenseItemBehavior fallback)
	{
		return new DispenseItemBehavior()
		{
			@Override
			public ItemStack dispense(BlockSource source, ItemStack item)
			{
				Level level = source.getLevel();
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

				if (level.getBlockEntity(pos) instanceof CauldronBlockEntity blockEntity)
				{
					blockEntity.replaceBlockAndUpdate(blockState());
					return new ItemStack(Items.BUCKET);
				}

				return fallback.dispense(source, item);
			}

		};

	}

}
