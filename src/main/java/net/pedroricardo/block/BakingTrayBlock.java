package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class BakingTrayBlock extends MultipartBlock<BakingTrayBlockEntity> {
    public static final MapCodec<BakingTrayBlock> CODEC = createCodec(BakingTrayBlock::new);
    protected BakingTrayBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockPos mainPartPos = this.getMainPartPosition(world, pos);
        if (mainPartPos == null) {
            mainPartPos = pos;
        }
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, mainPartPos, context).offset(mainPartPos.getX() - pos.getX(), mainPartPos.getY() - pos.getY(), mainPartPos.getZ() - pos.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BakingTrayBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingTrayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.BAKING_TRAY, BakingTrayBlockEntity::tick);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        this.remove(world, pos, true);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof BakingTrayBlockEntity tray)) {
            return ActionResult.PASS;
        }
        if (!tray.getCakeBatter().isEmpty() && tray.getCakeBatter().getBakeTime() >= PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
            player.giveItemStack(PBCakeBlock.of(Collections.singletonList(tray.getCakeBatter().copy(new FullBatterSizeContainer(tray.getSize(), tray.getCakeBatter().getSizeContainer().getHeight())))));
            tray.setCakeBatter(CakeBatter.getHeightOnlyEmpty());
            BlockPos mainPartPos = this.getMainPartPosition(world, pos);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, mainPartPos, GameEvent.Emitter.of(state));
            world.updateListeners(mainPartPos, state, state, Block.NOTIFY_ALL_AND_REDRAW);
            return ActionResult.success(world.isClient());
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof BakingTrayBlockEntity tray)) {
            return Blocks.CAKE.getDefaultState().getOutlineShape(world, pos, context);
        }
        return Block.createCuboidShape(8.0 - tray.getSize() / 2.0, 0.0, 8.0 - tray.getSize() / 2.0, 8.0 + tray.getSize() / 2.0, tray.getHeight(), 8.0 + tray.getSize() / 2.0);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        this.placeParts(world, pos, state);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        BlockPos mainPartPos = this.getMainPartPosition(world, pos);
        if (!mainPartPos.equals(pos)) {
            world.breakBlock(mainPartPos, player.canHarvest(state) && !player.isCreative(), player);
        }
        return state;
    }

    public void placeParts(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof MultipartBlock<?> block)) return;
        VoxelShape shape = block.getFullShape(state, world, pos, ShapeContext.absent());
        if (shape.isEmpty()) return;
        Box box = shape.getBoundingBox().offset(pos);
        box = new Box(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ), Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
        for (int x = (int)box.minX; x < box.maxX; x++) {
            for (int y = (int)box.minY; y < box.maxY; y++) {
                for (int z = (int)box.minZ; z < box.maxZ; z++) {
                    BlockPos partPos = new BlockPos(x, y, z);
                    if (!world.isInBuildLimit(partPos) || VoxelShapes.combineAndSimplify(shape.offset(pos.getX() - partPos.getX(), pos.getY() - partPos.getY(), pos.getZ() - partPos.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND).isEmpty()) continue;
                    BlockState partState = world.getBlockState(partPos);
                    if (partState.isReplaceable() && !partState.isSolidBlock(world, partPos) && !partPos.equals(pos)) {
                        block.createPart(world, pos, partPos);
                    }
                }
            }
        }
    }
}
