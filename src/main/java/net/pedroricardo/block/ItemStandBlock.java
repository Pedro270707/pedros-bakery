package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.ItemStandBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class ItemStandBlock<T extends ItemStandBlockEntity> extends BaseEntityBlock {
    protected ItemStandBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    public abstract boolean canContain(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        try {
            @SuppressWarnings("unchecked")
            T stand = (T) world.getBlockEntity(pos);
            if (stand == null) return InteractionResult.PASS;
            if (stand.getStack().isEmpty() && canContain(stack, state, world, pos, player, hand, hit)) {
                stand.setStack(PBHelpers.splitUnlessCreative(stack, 1, player));
                world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
                return InteractionResult.SUCCESS;
            } else if (!stand.getStack().isEmpty()) {
                player.addItem(stand.getStack());
                stand.setStack(ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        } catch (ClassCastException ignored) {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.is(newState.getBlock())) {
            return;
        }
        this.dropItem(world, pos);
        super.onRemove(state, world, pos, newState, moved);
    }

    private void dropItem(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemStandBlockEntity stand && !world.isClientSide()) {
            ItemStack stack = stand.getStack().copy();
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, stack);
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
            stand.clearContent();
            PBHelpers.update(stand, (ServerLevel) world);
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ItemStandBlockEntity stand && !stand.getStack().isEmpty() ? 15 : 0;
    }
}
