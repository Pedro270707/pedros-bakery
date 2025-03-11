package net.pedroricardo.block.multipart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MultipartBlock<T extends MultipartBlockEntity<T>> extends BlockWithEntity {
    public static final BooleanProperty IS_MAIN_PART = BooleanProperty.of("is_main_part");

    protected MultipartBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(IS_MAIN_PART, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IS_MAIN_PART);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(IS_MAIN_PART, true);
    }

    @Override
    public @Nullable T createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockPos mainPartPos = this.getMainPartPosition(world, pos);
        if (mainPartPos == null) {
            mainPartPos = pos;
        }
        try {
            return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, mainPartPos, world.getBlockEntity(mainPartPos), context).offset(mainPartPos.getX() - pos.getX(), mainPartPos.getY() - pos.getY(), mainPartPos.getZ() - pos.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND);
        } catch (Exception e) {
            return VoxelShapes.fullCube();
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        BlockPos mainPartPos = this.getMainPartPosition(world, pos);
        if (!mainPartPos.equals(pos)) {
            BlockState mainState = world.getBlockState(mainPartPos);
            if (player.canHarvest(mainState) && !player.isCreative()) {
                BlockEntity blockEntity = world.getBlockEntity(mainPartPos);
                Block.dropStacks(mainState, world, mainPartPos, blockEntity, player, ItemStack.EMPTY);
            }
        }
        if (!world.isClient()) return state;
        for (BlockPos partPos : this.getParts(world, pos)) {
            world.addBlockBreakParticles(partPos, world.getBlockState(partPos));
        }
        return state;
    }

    public abstract VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, @Nullable BlockEntity blockEntity, ShapeContext context);

    @SuppressWarnings("unchecked")
    public Set<BlockPos> getParts(BlockView world, BlockPos pos) {
        try {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) return ((T) blockEntity).getPartPositions();
        } catch (ClassCastException ignored) {
        }
        return new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public void remove(World world, BlockPos pos, boolean removeMain) {
        try {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) ((T) blockEntity).remove(removeMain);
        } catch (ClassCastException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    public void createPart(World world, BlockPos pos, BlockPos partPos) {
        try {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity == null) {
                return;
            }
            T part = (T) blockEntity;
            BlockState state = world.getBlockState(pos);
            world.setBlockState(partPos, state);
            part.addPartPosition(partPos);
        } catch (ClassCastException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    public BlockPos getMainPartPosition(BlockView world, BlockPos pos) {
        try {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) return ((T) blockEntity).getMainPartPosition();
        } catch (ClassCastException ignored) {
        }
        return null;
    }

    public List<BlockPos> getPartPositionsForPlacement(WorldView world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        List<BlockPos> list = new ArrayList<>();
        if (!(state.getBlock() instanceof MultipartBlock<?> block)) return list;
        VoxelShape shape = block.getFullShape(state, world, pos, blockEntity, ShapeContext.absent());
        if (shape.isEmpty()) return list;
        Box box = shape.getBoundingBox().offset(pos);
        box = new Box(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ), Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
        for (int x = (int)box.minX; x < box.maxX; x++) {
            for (int y = (int)box.minY; y < box.maxY; y++) {
                for (int z = (int)box.minZ; z < box.maxZ; z++) {
                    BlockPos partPos = new BlockPos(x, y, z);
                    if (VoxelShapes.combineAndSimplify(shape.offset(pos.getX() - partPos.getX(), pos.getY() - partPos.getY(), pos.getZ() - partPos.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND).isEmpty()) {
                        continue;
                    }
                    if (!partPos.equals(pos)) {
                        list.add(partPos);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Default implementation to place all the parts of the block
     * @param world: the main block's world
     * @param pos: position of the main block
     * @param state: block state of the main block
     */
    public void placeParts(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof MultipartBlock<?> block)) return;

        List<BlockPos> partPositions = this.getPartPositionsForPlacement(world, pos, state, world.getBlockEntity(pos));
        this.remove(world, pos, false);
        for (BlockPos partPos : partPositions) {
            if (!world.isInBuildLimit(partPos)) continue;
            BlockState partState = world.getBlockState(partPos);
            if (partState.isReplaceable() && !partState.isSolidBlock(world, partPos)) block.createPart(world, pos, partPos);
        }
    }
}
