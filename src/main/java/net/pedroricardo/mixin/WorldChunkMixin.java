package net.pedroricardo.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    @Unique
    @Nullable
    private BlockEntity tempBlockEntity = null;

    @Unique
    private BlockState tempBlockState = null;

    @Inject(method = "setBlockState", at = @At("HEAD"))
    private void pedrosbakery$captureBlockEntity(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        BlockEntity blockEntity = ((WorldChunk)(Object) this).getBlockEntity(pos);
        this.tempBlockState = ((WorldChunk)(Object) this).getBlockState(pos);
        if (this.tempBlockState.getBlock() instanceof MultipartBlockPart<?, ?> && this.tempBlockState.getOrEmpty(MultipartBlockPart.DELEGATE).orElse(false) && blockEntity instanceof MultipartBlockEntityPart<?> part && part.getParentPos() != null) {
            this.tempBlockEntity = part;
        } else {
            this.tempBlockEntity = null;
        }
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onStateReplaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V"))
    private void pedrosbakery$onPartStateReplaced(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        if (this.tempBlockEntity != null) {
            ((MultipartBlockPart<?, ?>)this.tempBlockState.getBlock()).onStateReplaced(this.tempBlockState, ((WorldChunk)(Object) this).getWorld(), pos, ((WorldChunk)(Object) this).getWorld().getBlockState(pos), this.tempBlockEntity, moved);
        }
    }
}