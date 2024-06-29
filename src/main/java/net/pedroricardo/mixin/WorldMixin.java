package net.pedroricardo.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void pedrosbakery$breakRootBlock(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = ((World)(Object) this).getBlockState(pos);
        BlockEntity blockEntity = ((World)(Object) this).getBlockEntity(pos);
        if (state.getBlock() instanceof MultipartBlockPart<?, ?> && state.getOrEmpty(MultipartBlockPart.DELEGATE).orElse(false) && blockEntity instanceof MultipartBlockEntityPart<?> part && part.getParentPos() != null) {
            ((World)(Object) this).breakBlock(part.getParentPos(), drop, breakingEntity, maxUpdateDepth);
        }
    }
}
