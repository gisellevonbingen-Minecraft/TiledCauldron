package gisellevonbingen.tiled_cauldron.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import gisellevonbingen.tiled_cauldron.common.registries.ModBlockEntityTypes;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@Mod(TiledCauldron.MODID)
public class TiledCauldron
{
	public static final String MODID = "tiled_cauldron";
	public static final Logger LOGGER = LogManager.getLogger();

	public TiledCauldron()
	{
		IEventBus fml_bus = ModLoadingContext.get().getActiveContainer().getEventBus();
		fml_bus.addListener(this::onCommonSetup);
		fml_bus.addListener(this::onRegisterCapabilities);
		ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(fml_bus);
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
		e.registerBlock(Capabilities.FluidHandler.BLOCK, this::getFluidTank, Blocks.CAULDRON);

		for (CauldronFluidTransfom cauldronFluidTransfom : CauldronFluidTransfom.values())
		{
			e.registerBlock(Capabilities.FluidHandler.BLOCK, this::getFluidTank, cauldronFluidTransfom.blockState().getBlock());
		}

	}

	private IFluidHandler getFluidTank(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Direction context)
	{
		if (blockEntity instanceof CauldronBlockEntity cauldronBlockEntity)
		{
			return cauldronBlockEntity.getFluidTank();
		}

		return null;
	}

	public static ResourceLocation rl(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

}
