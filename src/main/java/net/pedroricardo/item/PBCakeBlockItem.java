package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PBCakeBlockItem extends BlockItem {
    public PBCakeBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<CakeBatter<FullBatterSizeContainer>> batterList = PBHelpers.getOrDefault(stack, PBComponentTypes.BATTER_LIST, List.of());
        if (batterList.isEmpty()) {
            return;
        }
        List<CakeBatter<FullBatterSizeContainer>> reversed = new ArrayList<>(batterList);
        Collections.reverse(reversed);
        for (CakeBatter<FullBatterSizeContainer> batter : reversed) {
            if (batter.getTop().isPresent()) {
                tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor_and_top", Text.translatable(batter.getFlavor().getTranslationKey()), Text.translatable(batter.getTop().get().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor", Text.translatable(batter.getFlavor().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
            }
        }
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        if (!(state.getBlock() instanceof MultipartBlock<?> multipart)) return super.canPlace(context, state);
        PBCakeBlockEntity blockEntity = new PBCakeBlockEntity(context.getBlockPos(), state);
        blockEntity.readFrom(context.getStack());
        PlayerEntity player = context.getPlayer();
        ShapeContext shapeContext = player == null ? ShapeContext.absent() : ShapeContext.of(player);
        if (!context.getWorld().doesNotIntersectEntities(null, multipart.getFullShape(state, context.getWorld(), context.getBlockPos(), blockEntity, shapeContext).offset(context.getBlockPos().getX(), context.getBlockPos().getY(), context.getBlockPos().getZ()))) return false;
        List<BlockPos> list = multipart.getPartPositionsForPlacement(context.getWorld(), context.getBlockPos(), state, blockEntity);
        return list.stream().noneMatch(partPos -> {
            BlockState partState = context.getWorld().getBlockState(partPos);
            return !partState.isReplaceable() || partState.isSolidBlock(context.getWorld(), partPos);
        });
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        PBHelpers.set(stack, PBComponentTypes.BATTER_LIST, Collections.singletonList(CakeBatter.getFullSizeDefault().withBakeTime(2000)));
        return stack;
    }
}
