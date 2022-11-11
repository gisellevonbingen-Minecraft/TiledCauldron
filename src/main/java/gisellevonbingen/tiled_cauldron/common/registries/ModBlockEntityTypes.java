package gisellevonbingen.tiled_cauldron.common.registries;

import gisellevonbingen.tiled_cauldron.common.TiledCauldron;
import gisellevonbingen.tiled_cauldron.common.tile.CauldronBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TiledCauldron.MODID);

	public static final RegistryObject<BlockEntityType<?>> CAULDRON = BLOCK_ENTITY_TYPES.register("cauldron", () -> BlockEntityType.Builder.of(CauldronBlockEntity::new, Blocks.CAULDRON).build(null));
}
