package net.pedroricardo.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.pedroricardo.block.multipart.MultipartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class SetBlockStateMixin {
    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;", shift = At.Shift.BEFORE))
    private void pedrosbakery$removePartsWhenReplaced(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
        if (((LevelChunk)(Object) this).getLevel().getBlockState(pos).getBlock() instanceof MultipartBlock<?, ?, ?> multipartBlock && ((LevelChunk)(Object) this).getLevel().getBlockState(pos) != state) {
            multipartBlock.removePartsWhenReplaced(((LevelChunk)(Object) this).getLevel().getBlockState(pos), ((LevelChunk)(Object) this).getLevel(), pos, state, moved);
        }
    }
}
