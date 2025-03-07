package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.minecraft.util.Hand;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFlavor;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingTrayItem extends BlockItem implements BatterContainerItem {
    public BakingTrayItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int size = PBHelpers.getOrDefault(stack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        int height = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());

        tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.size", size, size, height));
        if (batter.isEmpty()) {
            return;
        }
        tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor", Text.translatable(batter.getFlavor().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
        if (batter.getSizeContainer().getHeight() != height && batter.getSizeContainer().getHeight() != 0) {
            tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.full", (int)(100.0f * batter.getSizeContainer().getHeight() / (float) height)).formatted(Formatting.YELLOW));
        }
    }

    public boolean addBatter(PlayerEntity player, Hand hand, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.isOf(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());
        if (batter.isEmpty()) {
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER, new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(Math.min(amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()))), flavor, false));
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, newStack));
            return true;
        } else if (batter.getBakeTime() < 200 && batter.getSizeContainer().getHeight() < PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) && flavor == batter.getFlavor()) {
            batter.getSizeContainer().setHeight(Math.min(batter.getSizeContainer().getHeight() + amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get())));
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER, batter);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, newStack));
            return true;
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);

    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        if (!(state.getBlock() instanceof MultipartBlock<?> multipart)) return super.canPlace(context, state);
        BakingTrayBlockEntity blockEntity = new BakingTrayBlockEntity(context.getBlockPos(), state);
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
        PBHelpers.set(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());
        PBHelpers.set(stack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        PBHelpers.set(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
        return stack;
    }
}
