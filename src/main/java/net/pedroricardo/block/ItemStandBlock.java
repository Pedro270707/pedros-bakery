package net.pedroricardo.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.ItemStandBlockEntity;

public abstract class ItemStandBlock<T extends ItemStandBlockEntity> extends BlockWithEntity {
    protected ItemStandBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    public abstract boolean canContain(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        try {
            @SuppressWarnings("unchecked")
            T stand = (T) world.getBlockEntity(pos);
            if (stand == null) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (stand.getStack().isEmpty() && canContain(stack, state, world, pos, player, hand, hit)) {
                stand.setStack(stack.copyWithCount(1));
                stack.decrementUnlessCreative(1, player);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
                return ItemActionResult.SUCCESS;
            } else if (!stand.getStack().isEmpty()) {
                player.giveItemStack(stand.getStack());
                stand.setStack(ItemStack.EMPTY);
                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } catch (ClassCastException ignored) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        this.dropItem(world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void dropItem(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemStandBlockEntity stand) {
            ItemStack stack = stand.getStack().copy();
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, stack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
            stand.clear();
            PBHelpers.updateListeners(stand);
        }
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ItemStandBlockEntity stand && !stand.getStack().isEmpty() ? 15 : 0;
    }
}
