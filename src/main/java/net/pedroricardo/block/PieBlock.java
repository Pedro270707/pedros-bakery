package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PieBlockEntity;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

public class PieBlock extends BlockWithEntity {
    public static final MapCodec<PieBlock> CODEC = createCodec(PieBlock::new);

    protected PieBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PieBlockEntity pie)) return ActionResult.PASS;
        if (pie.getLayers() == 2) {
            if (pie.getFillingItem().isEmpty()) {
                pie.setLayers(1);
                return ActionResult.PASS;
            }
            if (player.isSneaking()) {
                ItemStack stack = pie.getFillingItem();
                pie.setFillingItem(ItemStack.EMPTY);
                pie.setLayers(1);
                player.giveItemStack(stack);
                return ActionResult.success(world.isClient());
            }
        }
        if (pie.isEmpty() || (pie.getTopBakeTime() < PedrosBakery.CONFIG.ticksUntilPieBaked.get() && pie.getLayers() == 3)
                || pie.getBottomBakeTime() < PedrosBakery.CONFIG.ticksUntilPieBaked.get()
                || !player.canConsume(false)) return super.onUse(state, world, pos, player, hit);
        player.getHungerManager().add(PedrosBakery.CONFIG.pieSliceFood.get(), PedrosBakery.CONFIG.pieSliceSaturation.get());
        pie.setSlices(pie.getSlices() - 1);
        if (pie.getSlices() == 0) {
            pie.setFillingItem(ItemStack.EMPTY);
            pie.setLayers(0);
            pie.setTopBakeTime(0);
            pie.setBottomBakeTime(0);
        }
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        world.emitGameEvent(player, GameEvent.EAT, pos);
        if (!world.isClient()) {
            PBHelpers.update(pie, (ServerWorld) world);
        }
        return ActionResult.success(world.isClient());
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PieBlockEntity pie) || stack.isEmpty()) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (pie.getLayers() == 1 && pie.getSlices() == 4) {
            pie.setFillingItem(stack.splitUnlessCreative(1, player));
            pie.setLayers(2);
            world.setBlockState(pos, state);
            if (!world.isClient()) {
                PBHelpers.update(pie, (ServerWorld) world);
            }
            return ItemActionResult.success(world.isClient());
        }
        if (stack.isOf(PBItems.DOUGH)) {
            if (pie.isEmpty()) {
                pie.setLayers(1);
                pie.setSlices(4);
                pie.setBottomBakeTime(0);
                stack.decrementUnlessCreative(1, player);
                return ItemActionResult.success(world.isClient());
            } else if (pie.getLayers() == 2 && pie.getSlices() == 4) {
                pie.setLayers(3);
                pie.setTopBakeTime(0);
                stack.decrementUnlessCreative(1, player);
                return ItemActionResult.success(world.isClient());
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(1.0, 0.0, 1.0, 15.0, 4.0, 15.0);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.PIE, PieBlockEntity::tick);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PieBlockEntity(pos, state);
    }
}
