package gisellevonbingen.tiled_cauldron.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gisellevonbingen.tiled_cauldron.common.registries.ModBlockEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TiledCauldron.MODID)
public class TiledCauldron
{
	public static final String MODID = "tiled_cauldron";
	public static final Logger LOGGER = LogManager.getLogger();

	public TiledCauldron()
	{
		IEventBus fml_bus = FMLJavaModLoadingContext.get().getModEventBus();
		fml_bus.addListener(this::onCommonSetup);
		ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(fml_bus);
	}

	public void onCommonSetup(FMLCommonSetupEvent e)
	{
		e.enqueueWork(() ->
		{
			CauldronFluidTransfom.bootStrap();
		});
	}

	public static ResourceLocation rl(String path)
	{
		return new ResourceLocation(MODID, path);
	}

}
