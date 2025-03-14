package gisellevonbingen.tiled_cauldron.common.registries;

import java.util.Set;

import gisellevonbingen.tiled_cauldron.common.TiledCauldron;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntityTypes
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TiledCauldron.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CauldronBlockEntity>> CAULDRON = BLOCK_ENTITY_TYPES.register("cauldron", () -> new BlockEntityType<>(CauldronBlockEntity::new, Set.of(), null)
	{
		@Override
		public boolean isValid(BlockState state)
		{
			return state.getBlock() instanceof AbstractCauldronBlock;
		}

	});
}
