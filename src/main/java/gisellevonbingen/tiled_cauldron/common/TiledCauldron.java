package gisellevonbingen.tiled_cauldron.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import gisellevonbingen.tiled_cauldron.common.capabilities.CauldronTank;
import net.neoforged.neoforge.fluids.FluidType;

@Mod(TiledCauldron.MODID)
public class TiledCauldron
{
	public static final String MODID = "tiled_cauldron";

	public TiledCauldron()
	{
		IEventBus fml_bus = ModLoadingContext.get().getActiveContainer().getEventBus();
		fml_bus.addListener(this::onCommonSetup);
		fml_bus.addListener(this::onRegisterCapabilities);
		NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);
	}

	public void onCommonSetup(FMLCommonSetupEvent e)
	{
		e.enqueueWork(() ->
		{
			CauldronFluidTransfom.bootStrap();
		});
	}

	public void onRegisterCapabilities(RegisterCapabilitiesEvent e)
	{
		e.registerBlock(Capabilities.Fluid.BLOCK, (level, pos, state, blockEntity, context) ->
		{
			return CauldronTank.get(level, pos);
		}, Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON);
	}

	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock e)
	{
		ItemStack item = e.getItemStack();
		Level level = e.getLevel();
		BlockPos pos = e.getPos();
		CauldronTank tank = CauldronTank.get(level, pos);
		long amount = tank.getAmountAsLong(0);

		if (item.getItem() != Items.BUCKET)
		{
			if (item.getItem() instanceof BucketItem bucket && amount > 0 && amount < FluidType.BUCKET_VOLUME && tank.getFluid().isEmpty() == false && tank.getFluid().getFluid() == bucket.content)
			{
				if (level.isClientSide() == true)
				{
					e.setCanceled(true);
					return;
				}

				if (tank.fillFromBucket(bucket.content))
				{
					if (e.getEntity().getAbilities().instabuild == false)
					{
						e.getEntity().setItemInHand(e.getHand(), new ItemStack(Items.BUCKET));
					}
					e.setCanceled(true);
				}
			}

			return;
		}

		if (amount > 0 && amount < FluidType.BUCKET_VOLUME)
		{
			e.setCanceled(true);
		}
	}

}
