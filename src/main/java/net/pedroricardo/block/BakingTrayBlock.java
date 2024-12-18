package net.pedroricardo.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.entity.BakingTrayBlockEntityPart;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BakingTrayBlock extends BlockWithEntity implements MultipartBlock<BakingTrayBlockEntity, BakingTrayBlockEntityPart, BakingTrayBlockPart> {
    protected BakingTrayBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, pos, context), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingTrayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PBBlockEntities.BAKING_TRAY, BakingTrayBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof BakingTrayBlockEntity tray)) {
            return ActionResult.PASS;
        }
        if (!tray.getCakeBatter().isEmpty() && tray.getCakeBatter().getBakeTime() >= PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
            player.giveItemStack(PBCakeBlock.of(Collections.singletonList(tray.getCakeBatter().copy(new FullBatterSizeContainer(tray.getSize(), tray.getCakeBatter().getSizeContainer().getHeight())))));
            tray.setCakeBatter(CakeBatter.getHeightOnlyEmpty());
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL & Block.REDRAW_ON_MAIN_THREAD);
            return ActionResult.success(world.isClient());
        }
        return super.onUse(state, world, pos, player, hand, hit);
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
        if (world.getBlockEntity(pos) instanceof BakingTrayBlockEntity tray) {
            return tray.getParts();
        }
        return List.of();
    }

    @Override
    public BakingTrayBlockPart getPart() {
        return (BakingTrayBlockPart) PBBlocks.BAKING_TRAY_PART;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (world.getBlockEntity(pos) instanceof BakingTrayBlockEntity tray) {
            tray.readFrom(stack);
        }
    }
}
