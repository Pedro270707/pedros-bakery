package net.pedroricardo.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFlavor;
import net.pedroricardo.block.extras.CupcakeTrayBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.client.render.CupcakeTrayBlockRenderer;
import net.pedroricardo.client.render.PBCakeBlockRenderer;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class CupcakeTrayItem extends BlockItem implements BatterContainerItem {
    public CupcakeTrayItem(Block block, Properties settings) {
        super(block, settings);
    }

    public boolean addBatter(Player player, InteractionHand hand, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.is(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CupcakeTrayBatter batter = PBHelpers.getOrDefault(stack, PBComponentTypes.CUPCAKE_TRAY_BATTER.get(), CupcakeTrayBatter.getEmpty());
        List<CakeBatter<FixedBatterSizeContainer>> batterList = Lists.newArrayList(batter.stream().iterator());
        boolean changed = false;
        for (int i = 0; i < batter.stream().size(); i++) {
            if (batter.stream().get(i).isEmpty()) {
                batterList.set(i, new CakeBatter<>(0, new FixedBatterSizeContainer(false), flavor, false));
                changed = true;
            }
        }
        if (changed) {
            PBHelpers.set(newStack, PBComponentTypes.CUPCAKE_TRAY_BATTER.get(), new CupcakeTrayBatter(batterList));
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, newStack));
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        PBHelpers.set(stack, PBComponentTypes.CUPCAKE_TRAY_BATTER.get(), CupcakeTrayBatter.getEmpty());
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
                        CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY.readFrom(stack);
                        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY, matrices, vertexConsumers, light, overlay);
                    }
                };
            }
        });
    }
}
