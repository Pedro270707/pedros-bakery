package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import org.jetbrains.annotations.Nullable;

public class PBCakeBlockPart extends MultipartBlockPart<PBCakeBlockEntity, PBCakeBlockEntityPart> {
    protected PBCakeBlockPart(Properties settings) {
        super(settings);
    }

    @Override
    public PBCakeBlockEntityPart newBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos) {
        return new PBCakeBlockEntityPart(pos, state, parentPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (!(world.getBlockEntity(pos) instanceof MultipartBlockEntityPart<?> cake) || cake.getParentPos() == null || !(world.getBlockState(cake.getParentPos()).getBlock() instanceof MultipartBlock<?, ?, ?> block)) {
            return Shapes.empty();
        }
        BlockPos offset = cake.getParentPos().subtract(pos);
        return Shapes.join(block.getFullShape(world.getBlockState(cake.getParentPos()), world, cake.getParentPos(), context).move(offset.getX(), offset.getY(), offset.getZ()), Shapes.block(), BooleanOp.AND);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntityPart(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PBBlockEntities.CAKE_PART.get(), PBCakeBlockEntityPart::tick);
    }
}
