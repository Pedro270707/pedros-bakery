package net.pedroricardo;

import com.anthonyhilyard.prism.util.ImageAnalysis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PBClientHelpers {
    public static TextColor getPieColor(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed) {
        Optional<TextColor> override = PedrosBakery.PIE_COLOR_OVERRIDES.get(stack.getItem());
        if (override.isPresent()) {
            return override.get();
        }
        Sprite sprite;
        if (stack.getItem() instanceof BlockItem) {
            sprite = MinecraftClient.getInstance().getBlockRenderManager().getModel(((BlockItem) stack.getItem()).getBlock().getDefaultState()).getParticleSprite();
        } else {
            sprite = MinecraftClient.getInstance().getItemRenderer().getModel(stack, world, entity, seed).getParticleSprite();
        }
        return ImageAnalysis.getDominantColor(sprite.getContents().getId().withPrefixedPath("textures/").withSuffixedPath(".png"), new Rect2i(0, 0, sprite.getContents().getWidth(), sprite.getContents().getHeight()));
    }
}
