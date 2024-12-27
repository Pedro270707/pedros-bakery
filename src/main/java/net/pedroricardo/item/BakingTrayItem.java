package net.pedroricardo.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFlavor;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import net.pedroricardo.client.render.BakingTrayBlockRenderer;
import net.pedroricardo.client.render.PBCakeBlockRenderer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BakingTrayItem extends BlockItem implements BatterContainerItem {
    public BakingTrayItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        int size = PBHelpers.getOrDefault(stack, PBComponentTypes.SIZE.get(), PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        int height = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), CakeBatter.getHeightOnlyEmpty());

        tooltip.add(Component.translatable("block.pedrosbakery.baking_tray.size", size, size, height));
        if (batter.isEmpty()) {
            return;
        }
        tooltip.add(Component.translatable("block.pedrosbakery.cake.flavor", Component.translatable(batter.getFlavor().getTranslationKey())).withStyle(batter.isWaxed() ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        if (batter.getSizeContainer().getHeight() != height && batter.getSizeContainer().getHeight() != 0) {
            tooltip.add(Component.translatable("block.pedrosbakery.baking_tray.full", (int)(100.0f * batter.getSizeContainer().getHeight() / (float) height)).withStyle(ChatFormatting.YELLOW));
        }
    }

    public boolean addBatter(Player player, InteractionHand hand, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.is(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), CakeBatter.getHeightOnlyEmpty());
        if (batter.isEmpty()) {
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(Math.min(amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()))), flavor, false));
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, newStack));
            return true;
        } else if (batter.getBakeTime() < 200 && batter.getSizeContainer().getHeight() < PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) && flavor == batter.getFlavor()) {
            batter.getSizeContainer().setHeight(Math.min(batter.getSizeContainer().getHeight() + amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get())));
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), batter);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, newStack));
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        PBHelpers.set(stack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), CakeBatter.getHeightOnlyEmpty());
        PBHelpers.set(stack, PBComponentTypes.SIZE.get(), PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        PBHelpers.set(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
        return stack;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new BlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()) {
                    @Override
                    public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                        if (stack.is(PBBlocks.EXPANDABLE_BAKING_TRAY.get().asItem())) {
                            BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY.readFrom(stack);
                            Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY, matrices, vertexConsumers, light, overlay);
                        } else {
                            BakingTrayBlockRenderer.RENDER_TRAY.readFrom(stack);
                            Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(BakingTrayBlockRenderer.RENDER_TRAY, matrices, vertexConsumers, light, overlay);
                        }
                    }
                };
            }
        });
    }
}
