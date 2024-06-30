package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
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
import net.pedroricardo.block.entity.CakeStandBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

public class CakeStandBlock extends BlockWithEntity {
    public static final MapCodec<CakeStandBlock> CODEC = createCodec(CakeStandBlock::new);

    protected CakeStandBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CakeStandBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.createCuboidShape(1.0, 1.0, 1.0, 15.0, 16.0, 15.0));
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CakeStandBlockEntity stand)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stand.getItem().isEmpty() && stack.isIn(PBTags.Items.CAKE_STAND_ITEM)) {
            stand.setItem(stack.copyWithCount(1));
            stack.decrementUnlessCreative(1, player);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
            return ItemActionResult.SUCCESS;
        } else if (!stand.getItem().isEmpty()) {
            player.giveItemStack(stand.getItem());
            stand.setItem(ItemStack.EMPTY);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        this.dropCake(world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void dropCake(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CakeStandBlockEntity stand) {
            ItemStack itemStack = stand.getItem().copy();
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
            stand.clear();
            PBHelpers.updateListeners(stand);
        }
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof CakeStandBlockEntity stand && !stand.getItem().isEmpty() ? 15 : 0;
    }
}
