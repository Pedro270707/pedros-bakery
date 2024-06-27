package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.PBCakePartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemUsageContext.class)
public class HijackItemUsageContext {
    @ModifyReturnValue(method = "getBlockPos", at = @At("RETURN"))
    private BlockPos pedrosbakery$hijackBlockPos(BlockPos original) {
        World world = ((ItemUsageContext)(Object) this).getWorld();
        if (world.getBlockState(original).isOf(PBBlocks.CAKE_PART) && world.getBlockEntity(original) instanceof PBCakePartBlockEntity part && part.getParentPos() != null) {
            return part.getParentPos();
        }
        return original;
    }
}
