package net.pedroricardo.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(UseOnContext.class)
public class HijackItemUsageContext {
    @ModifyReturnValue(method = "getClickedPos", at = @At("RETURN"))
    private BlockPos pedrosbakery$hijackBlockPos(BlockPos original) {
        Level world = ((UseOnContext)(Object) this).getLevel();
        if (world.getBlockState(original).is(PBBlocks.CAKE_PART.get()) && world.getBlockEntity(original) instanceof PBCakeBlockEntityPart part && part.getParentPos() != null) {
            return part.getParentPos();
        }
        return original;
    }
}
