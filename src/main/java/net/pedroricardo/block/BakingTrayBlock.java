package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.entity.BakingTrayBlockEntityPart;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BakingTrayBlock extends BlockWithEntity implements MultipartBlock<BakingTrayBlockEntity, BakingTrayBlockEntityPart, BakingTrayBlockPart> {
    public static final MapCodec<BakingTrayBlock> CODEC = createCodec(BakingTrayBlock::new);
    protected BakingTrayBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, pos, context), VoxelShapes.fullCube(), BooleanBiFunction.AND);
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof BakingTrayBlockEntity tray)) {
            return ActionResult.PASS;
        }
        if (!tray.getCakeBatter().isEmpty() && tray.getCakeBatter().getBakeTime() >= PBCakeBlock.TICKS_UNTIL_BAKED) {
            player.giveItemStack(PBCakeBlock.of(Collections.singletonList(tray.getCakeBatter().toLayer(tray.getSize()))));
            tray.setCakeBatter(CakeBatter.getEmpty());
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL_AND_REDRAW);
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
    public List<BlockPos> getParts(WorldView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return cake.getParts();
        }
        return List.of();
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        removePartsWhenReplaced(state, world, pos, newState, moved);
        super.onStateReplaced(state, world, pos, newState, moved);
        world.updateListeners(pos, state, newState, Block.NOTIFY_ALL_AND_REDRAW | (moved ? Block.MOVED : 0));
    }

    @Override
    public void removePartsWhenReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (!(world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?, ?>) || !world.getBlockState(partPos).contains(MultipartBlockPart.DELEGATE)) {
                return;
            }
            world.setBlockState(partPos, world.getBlockState(partPos).with(MultipartBlockPart.DELEGATE, false));
            if (moved) {
                world.removeBlock(partPos, true);
            } else {
                world.breakBlock(partPos, false);
            }
        }
    }

    @Override
    public BakingTrayBlockPart getPart() {
        return (BakingTrayBlockPart) PBBlocks.BAKING_TRAY_PART;
    }
}
