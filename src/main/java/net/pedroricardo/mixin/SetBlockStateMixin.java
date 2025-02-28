package net.pedroricardo.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import net.pedroricardo.block.multipart.MultipartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class SetBlockStateMixin {
    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;"))
    private void pedrosbakery$removePartsWhenReplaced(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        if (((WorldChunk)(Object) this).getWorld().getBlockState(pos).getBlock() instanceof MultipartBlock<?, ?, ?> multipartBlock && ((WorldChunk)(Object) this).getWorld().getBlockState(pos) != state) {
            multipartBlock.removePartsWhenReplaced(((WorldChunk)(Object) this).getWorld().getBlockState(pos), ((WorldChunk)(Object) this).getWorld(), pos, state, moved);
        }
    }
}
