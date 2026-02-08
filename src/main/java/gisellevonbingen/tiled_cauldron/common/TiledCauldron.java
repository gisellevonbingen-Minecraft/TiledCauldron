package gisellevonbingen.tiled_cauldron.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gisellevonbingen.tiled_cauldron.common.registries.ModBlockEntityTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import gisellevonbingen.tiled_cauldron.common.capabilities.CauldronTank;

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
		e.registerBlock(Capabilities.Fluid.BLOCK, (level, pos, state, blockEntity, context) ->
		{
			return CauldronTank.get(level, pos);
		}, Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON);
	}

    public static Identifier rl(String path)
    {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

}
