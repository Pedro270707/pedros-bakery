package net.pedroricardo;

import com.anthonyhilyard.prism.util.ImageAnalysis;
import net.fabricmc.fabric.impl.client.model.loading.BakerImplHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;

public class PBClientHelpers {
    public static final Map<Set<Vector2i>, BakedModel> COOKIE_SHAPE_TO_BAKED_MODEL = new HashMap<>();

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
