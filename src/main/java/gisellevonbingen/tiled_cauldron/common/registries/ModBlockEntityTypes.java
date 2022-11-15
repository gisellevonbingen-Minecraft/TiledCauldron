package gisellevonbingen.tiled_cauldron.common.registries;

import gisellevonbingen.tiled_cauldron.common.TiledCauldron;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, TiledCauldron.MODID);

	public static final RegistryObject<BlockEntityType<?>> CAULDRON = BLOCK_ENTITY_TYPES.register("cauldron", () -> new BlockEntityType<>(CauldronBlockEntity::new, null, null)
	{
		@Override
		public boolean isValid(BlockState state)
		{
			return state.getBlock() instanceof AbstractCauldronBlock;
		}

	});
}
