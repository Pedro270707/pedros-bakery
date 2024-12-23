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
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.entity.BakingTrayBlockEntityPart;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import org.jetbrains.annotations.Nullable;

public class BakingTrayBlockPart extends MultipartBlockPart<BakingTrayBlockEntity, BakingTrayBlockEntityPart> {
    public BakingTrayBlockPart(Properties settings) {
        super(settings);
    }

    @Override
    public BakingTrayBlockEntityPart newBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos) {
        return new BakingTrayBlockEntityPart(pos, state, parentPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (!(world.getBlockEntity(pos) instanceof MultipartBlockEntityPart<?> tray) || tray.getParentPos() == null || !(world.getBlockState(tray.getParentPos()).getBlock() instanceof MultipartBlock<?, ?, ?> block)) {
            return Shapes.empty();
        }
        BlockPos offset = tray.getParentPos().subtract(pos);
        return Shapes.join(block.getFullShape(world.getBlockState(tray.getParentPos()), world, tray.getParentPos(), context).move(offset.getX(), offset.getY(), offset.getZ()), Shapes.block(), BooleanOp.AND);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BakingTrayBlockEntityPart(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PBBlockEntities.BAKING_TRAY_PART.get(), BakingTrayBlockEntityPart::tick);
    }
}
