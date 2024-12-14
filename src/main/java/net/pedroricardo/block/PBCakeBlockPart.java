package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import org.jetbrains.annotations.Nullable;

public class PBCakeBlockPart extends MultipartBlockPart<PBCakeBlockEntity, PBCakeBlockEntityPart> {
    protected PBCakeBlockPart(Settings settings) {
        super(settings);
    }

    @Override
    public PBCakeBlockEntityPart createBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos) {
        return new PBCakeBlockEntityPart(pos, state, parentPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof MultipartBlockEntityPart<?> cake) || cake.getParentPos() == null || !(world.getBlockState(cake.getParentPos()).getBlock() instanceof MultipartBlock<?, ?, ?> block)) {
            return VoxelShapes.empty();
        }
        BlockPos offset = cake.getParentPos().subtract(pos);
        return VoxelShapes.combineAndSimplify(block.getFullShape(world.getBlockState(cake.getParentPos()), world, cake.getParentPos(), context).offset(offset.getX(), offset.getY(), offset.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntityPart(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PBBlockEntities.CAKE_PART, PBCakeBlockEntityPart::tick);
    }
}
