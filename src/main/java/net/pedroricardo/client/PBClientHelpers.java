package net.pedroricardo.client;

import com.anthonyhilyard.prism.util.ImageAnalysis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroricardo.PedrosBakery;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PBClientHelpers {
    public static TextColor getPieColor(ItemStack stack, @Nullable Level world, @Nullable LivingEntity entity, int seed) {
        Optional<TextColor> override = PedrosBakery.PIE_COLOR_OVERRIDES.get(stack.getItem());
        if (override.isPresent()) {
            return override.get();
        }
        TextureAtlasSprite sprite;
        if (stack.getItem() instanceof BlockItem) {
            sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(((BlockItem) stack.getItem()).getBlock().defaultBlockState()).getParticleIcon();
        } else {
            sprite = Minecraft.getInstance().getItemRenderer().getModel(stack, world, entity, seed).getParticleIcon();
        }
        return ImageAnalysis.getDominantColor(sprite.contents().name().withPrefix("textures/").withSuffix(".png"), new Rect2i(0, 0, sprite.contents().width(), sprite.contents().height()));
    }
}
