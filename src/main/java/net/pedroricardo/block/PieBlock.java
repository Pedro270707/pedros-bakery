package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PieBlockEntity;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

public class PieBlock extends BaseEntityBlock {
    protected PieBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult useWithItem = this.onUseWithItem(player.getItemInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != InteractionResult.PASS) return useWithItem;
        if (!(world.getBlockEntity(pos) instanceof PieBlockEntity pie)) return InteractionResult.PASS;
        if (pie.getLayers() == 2) {
            if (pie.getFillingItem().isEmpty()) {
                pie.setLayers(1);
                return InteractionResult.PASS;
            }
            if (player.isCrouching()) {
                ItemStack stack = pie.getFillingItem();
                pie.setFillingItem(ItemStack.EMPTY);
                pie.setLayers(1);
                player.addItem(stack);
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
        if (pie.isEmpty() || (pie.getTopBakeTime() < PedrosBakery.CONFIG.ticksUntilPieBaked.get() && pie.getLayers() == 3)
                || pie.getBottomBakeTime() < PedrosBakery.CONFIG.ticksUntilPieBaked.get()
                || !player.canEat(false)) return super.use(state, world, pos, player, hand, hit);
        player.getFoodData().eat(PedrosBakery.CONFIG.pieSliceFood.get(), PedrosBakery.CONFIG.pieSliceSaturation.get());
        pie.setSlices(pie.getSlices() - 1);
        if (pie.getSlices() == 0) {
            pie.setFillingItem(ItemStack.EMPTY);
            pie.setLayers(0);
            pie.setTopBakeTime(0);
            pie.setBottomBakeTime(0);
        }
        world.setBlock(pos, state, Block.UPDATE_ALL);
        world.gameEvent(player, GameEvent.EAT, pos);
        return InteractionResult.sidedSuccess(world.isClientSide());
    }

    private InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PieBlockEntity pie) || stack.isEmpty()) return InteractionResult.PASS;
        if (pie.getLayers() == 1 && pie.getSlices() == 4) {
            pie.setFillingItem(PBHelpers.splitUnlessCreative(stack, 1, player));
            pie.setLayers(2);
            world.setBlockAndUpdate(pos, state);
            if (!world.isClientSide()) {
                PBHelpers.update(pie, (ServerLevel) world);
            }
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        if (stack.is(PBItems.DOUGH.get())) {
            if (pie.isEmpty()) {
                pie.setLayers(1);
                pie.setSlices(4);
                pie.setBottomBakeTime(0);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            } else if (pie.getLayers() == 2 && pie.getSlices() == 4) {
                pie.setLayers(3);
                pie.setTopBakeTime(0);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return box(1.0, 0.0, 1.0, 15.0, 4.0, 15.0);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PBBlockEntities.PIE.get(), PieBlockEntity::tick);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PieBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (world.getBlockEntity(pos) instanceof PieBlockEntity pie) {
            pie.readFrom(stack);
        }
    }
}
