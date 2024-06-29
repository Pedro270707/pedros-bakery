package net.pedroricardo.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerControllerMixin {
    @Shadow private BlockPos failedMiningPos;

    @Shadow protected ServerWorld world;

    @Shadow private BlockPos miningPos;

    @Shadow public abstract void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence);

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0, shift = At.Shift.BEFORE))
    private void pedrosbakery$update(CallbackInfo ci) {
        BlockState state = this.world.getBlockState(this.failedMiningPos);
        BlockEntity blockEntity = this.world.getBlockEntity(this.failedMiningPos);
        if (state.getBlock() instanceof MultipartBlockPart<?, ?> && state.getOrEmpty(MultipartBlockPart.DELEGATE).orElse(true) && blockEntity instanceof MultipartBlockEntityPart<?> part && part.getParentPos() != null) {
            this.failedMiningPos = part.getParentPos();
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1, shift = At.Shift.BEFORE))
    private void pedrosbakery$update2(CallbackInfo ci) {
        BlockState state = this.world.getBlockState(this.miningPos);
        BlockEntity blockEntity = this.world.getBlockEntity(this.miningPos);
        if (state.getBlock() instanceof MultipartBlockPart<?, ?> && state.getOrEmpty(MultipartBlockPart.DELEGATE).orElse(true) && blockEntity instanceof MultipartBlockEntityPart<?> part && part.getParentPos() != null) {
            this.miningPos = part.getParentPos();
        }
    }

    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"), cancellable = true)
    private void pedrosbakery$processBlock(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
        BlockState state = this.world.getBlockState(pos);
        BlockEntity blockEntity = this.world.getBlockEntity(pos);
        if (state.getBlock() instanceof MultipartBlockPart<?, ?> && state.getOrEmpty(MultipartBlockPart.DELEGATE).orElse(true) && blockEntity instanceof MultipartBlockEntityPart<?> part && part.getParentPos() != null) {
            this.processBlockBreakingAction(part.getParentPos(), action, direction, worldHeight, sequence);
            ci.cancel();
        }
    }
}
