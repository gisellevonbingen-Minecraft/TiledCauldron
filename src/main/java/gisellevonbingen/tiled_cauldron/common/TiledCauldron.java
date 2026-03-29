package gisellevonbingen.tiled_cauldron.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gisellevonbingen.tiled_cauldron.common.registries.ModBlockEntityTypes;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

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
			this.wrapBucketInteractions();
		});
	}

	public void onRegisterCapabilities(RegisterCapabilitiesEvent e)
	{
		e.registerBlock(Capabilities.Fluid.BLOCK, (level, pos, state, blockEntity, context) ->
		{
			return blockEntity instanceof CauldronBlockEntity cauldronBlockEntity ? cauldronBlockEntity.getFluidTank() : null;
		}, Blocks.CAULDRON, Blocks.WATER_CAULDRON, Blocks.LAVA_CAULDRON);
	}

	public static Identifier rl(String path)
	{
		return Identifier.fromNamespaceAndPath(MODID, path);
	}

	private void wrapBucketInteractions()
	{
		this.wrapBucketInteraction(CauldronInteraction.WATER, Items.BUCKET);
		this.wrapBucketInteraction(CauldronInteraction.WATER, Items.WATER_BUCKET);
		this.wrapBucketInteraction(CauldronInteraction.WATER, Items.LAVA_BUCKET);
		this.wrapBucketInteraction(CauldronInteraction.WATER, Items.POWDER_SNOW_BUCKET);
		this.wrapBucketInteraction(CauldronInteraction.LAVA, Items.BUCKET);
		this.wrapBucketInteraction(CauldronInteraction.LAVA, Items.WATER_BUCKET);
		this.wrapBucketInteraction(CauldronInteraction.LAVA, Items.LAVA_BUCKET);
		this.wrapBucketInteraction(CauldronInteraction.LAVA, Items.POWDER_SNOW_BUCKET);
	}

	private void wrapBucketInteraction(CauldronInteraction.InteractionMap map, Item item)
	{
		CauldronInteraction interaction = map.map().get(item);
		map.map().put(item, (state, level, pos, player, hand, stack) ->
		{
			return this.isPartialFluidCauldron(level, pos) ? InteractionResult.TRY_WITH_EMPTY_HAND : interaction.interact(state, level, pos, player, hand, stack);
		});
	}

	private boolean isPartialFluidCauldron(Level level, BlockPos pos)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		return blockEntity instanceof CauldronBlockEntity cauldronBlockEntity && cauldronBlockEntity.getFluidTank().hasPartialBucket();
	}

	public static boolean shouldBlockVanillaItemInteraction(Level level, BlockPos pos, BlockState state, ItemStack stack)
	{
		return !(stack.getItem() instanceof BucketItem) && shouldBlockVanillaWorldMutation(level, pos, state);
	}

	public static boolean shouldBlockVanillaWorldMutation(Level level, BlockPos pos, BlockState state)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof CauldronBlockEntity cauldronBlockEntity)
		{
			var fluidTank = cauldronBlockEntity.getFluidTank();
			return fluidTank.hasManagedFluid() && !fluidTank.isVanillaStateCompatible(state);
		}

		return false;
	}

}
