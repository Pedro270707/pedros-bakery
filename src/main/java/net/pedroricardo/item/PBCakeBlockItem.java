package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.size.FullBatterSizeContainer;

import java.util.List;

public class PBCakeBlockItem extends BlockItem {
    public PBCakeBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        List<CakeBatter<FullBatterSizeContainer>> batterList = stack.getOrDefault(PBComponentTypes.BATTER_LIST, List.of());
        if (batterList.isEmpty()) {
            return;
        }
        for (CakeBatter<FullBatterSizeContainer> batter : batterList.reversed()) {
            if (batter.getTop().isPresent()) {
                tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor_and_top", Text.translatable(batter.getFlavor().getTranslationKey()), Text.translatable(batter.getTop().get().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor", Text.translatable(batter.getFlavor().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
            }
        }
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        PBCakeBlockEntity blockEntity = new PBCakeBlockEntity(context.getBlockPos(), state);
        blockEntity.readComponents(context.getStack());
        VoxelShape shape = blockEntity.toShape(state.get(Properties.HORIZONTAL_FACING));
        if (!context.getWorld().doesNotIntersectEntities(null, shape.offset(context.getBlockPos().getX(), context.getBlockPos().getY(), context.getBlockPos().getZ()))) return false;
        if (shape.isEmpty()) return false;
        Box box = shape.getBoundingBox().offset(context.getBlockPos());
        box = new Box(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ), Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
        for (int x = (int)box.minX; x < box.maxX; x++) {
            for (int y = (int)box.minY; y < box.maxY; y++) {
                for (int z = (int)box.minZ; z < box.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state1 = context.getWorld().getBlockState(pos);
                    if (!state1.isReplaceable() || state1.isSolidBlock(context.getWorld(), pos)) return false;
                }
            }
        }
        return true;
    }
}
