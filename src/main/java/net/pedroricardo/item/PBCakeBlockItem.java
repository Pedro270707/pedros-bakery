package net.pedroricardo.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PBCakeBlockItem extends BlockItem {
    public PBCakeBlockItem(Block block, Item.Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        List<CakeBatter<FullBatterSizeContainer>> batterList = PBHelpers.getOrDefault(stack, PBComponentTypes.BATTER_LIST.get(), List.of());
        if (batterList.isEmpty()) {
            return;
        }
        List<CakeBatter<FullBatterSizeContainer>> reversed = new ArrayList<>(batterList);
        Collections.reverse(reversed);
        for (CakeBatter<FullBatterSizeContainer> batter : reversed) {
            if (batter.getTop().isPresent()) {
                tooltip.add(Component.translatable("block.pedrosbakery.cake.flavor_and_top", Component.translatable(batter.getFlavor().getTranslationKey()), Component.translatable(batter.getTop().get().getTranslationKey())).withStyle(batter.isWaxed() ? ChatFormatting.GOLD : ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("block.pedrosbakery.cake.flavor", Component.translatable(batter.getFlavor().getTranslationKey())).withStyle(batter.isWaxed() ? ChatFormatting.GOLD : ChatFormatting.GRAY));
            }
        }
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        PBCakeBlockEntity blockEntity = new PBCakeBlockEntity(context.getClickedPos(), state);
        blockEntity.readFrom(context.getItemInHand());
        VoxelShape shape = blockEntity.toShape();
        if (!context.getLevel().isUnobstructed(null, shape.move(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()))) return false;
        if (shape.isEmpty()) return false;
        AABB box = shape.bounds().move(context.getClickedPos());
        box = new AABB(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ), Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
        for (int x = (int)box.minX; x < box.maxX; x++) {
            for (int y = (int)box.minY; y < box.maxY; y++) {
                for (int z = (int)box.minZ; z < box.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state1 = context.getLevel().getBlockState(pos);
                    if (!state1.canBeReplaced() || state1.isSolidRender(context.getLevel(), pos)) return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        PBHelpers.set(stack, PBComponentTypes.BATTER_LIST.get(), Collections.singletonList(CakeBatter.getFullSizeDefault().withBakeTime(2000)));
        return stack;
    }
}
