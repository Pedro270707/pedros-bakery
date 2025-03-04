package net.pedroricardo.block.multipart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
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
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, pos, context), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    public abstract VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

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
}
