package gisellevonbingen.tiled_cauldron.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gisellevonbingen.tiled_cauldron.common.CauldronFluidTransfom;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin
{
	@Inject(method = "getDispenseMethod", at = @At("TAIL"), cancellable = true)
	private void getDispenseMethod(ItemStack item, CallbackInfoReturnable<DispenseItemBehavior> cir)
	{
		if (item.getItem() instanceof BucketItem bucket)
		{
			DispenseItemBehavior internal = cir.getReturnValue();
			DispenseItemBehavior wrap = CauldronFluidTransfom.wrapDispenseItemBehavior(bucket, internal);
			cir.setReturnValue(wrap);
		}

	}

}
