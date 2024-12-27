package net.pedroricardo.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.client.render.CupcakeBlockRenderer;
import net.pedroricardo.client.render.PieBlockRenderer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class PieItem extends BlockItem {
    public PieItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        PieDataComponent pieDataComponent = PBHelpers.getOrDefault(stack, PBComponentTypes.PIE_DATA.get(), PieDataComponent.EMPTY);
        if (!pieDataComponent.filling().isEmpty() && pieDataComponent.layers() >= 2) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".flavor", pieDataComponent.filling().getHoverName()).withStyle(ChatFormatting.GRAY));
            if (pieDataComponent.filling().is(this)) {
                tooltip.add(Component.translatable(this.getDescriptionId() + ".pie_flavor").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new BlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()) {
                    @Override
                    public void renderByItem(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
                        PieBlockRenderer.RENDER_PIE.readFrom(stack);
                        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(PieBlockRenderer.RENDER_PIE, matrices, vertexConsumers, light, overlay);
                    }
                };
            }
        });
    }
}
