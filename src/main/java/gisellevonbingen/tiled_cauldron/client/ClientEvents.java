package gisellevonbingen.tiled_cauldron.client;

import gisellevonbingen.tiled_cauldron.common.TiledCauldron;
import gisellevonbingen.tiled_cauldron.common.registries.ModBlockEntityTypes;
import gisellevonbingen.tiled_cauldron.client.renderer.CauldronFluidRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = TiledCauldron.MODID, value = Dist.CLIENT)
public final class ClientEvents
{
	private ClientEvents()
	{
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CAULDRON.get(), CauldronFluidRenderer::new);
	}

}
