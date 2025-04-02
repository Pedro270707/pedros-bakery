package net.pedroricardo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
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
        return getAverageColor(sprite.getContents().getId().withPrefixedPath("textures/").withSuffixedPath(".png"), new Rect2i(0, 0, sprite.getContents().getWidth(), sprite.getContents().getHeight())).orElse(TextColor.fromFormatting(Formatting.BLACK));
    }

    public static Optional<TextColor> getAverageColor(Identifier imageLocation, Rect2i region) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ResourceManager resourceManager = minecraft.getResourceManager();

        try (InputStream imageStream = resourceManager.getResource(imageLocation).isPresent() ? resourceManager.getResource(imageLocation).get().getInputStream() : null) {
            if (imageStream == null) return Optional.empty();
            BufferedImage image = ImageIO.read(imageStream);
            if (region != null) {
                image = image.getSubimage(region.getX(), region.getY(), region.getWidth(), region.getHeight());
            }

            return Optional.of(getAverageColor(image));
        } catch (Exception var9) {
            return Optional.empty();
        }
    }

    public static TextColor getAverageColor(BufferedImage image) {
        long reds = 0;
        long greens = 0;
        long blues = 0;
        long totalWeight = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getRGB(x, y);
                int weight = (color >> 24) & 0xFF; // alpha
                reds += ((color >> 16) & 0xFF) * weight;
                greens += ((color >> 8) & 0xFF) * weight;
                blues += (color & 0xFF) * weight;
                totalWeight += weight;
            }
        }

        if (totalWeight == 0) {
            return TextColor.fromFormatting(Formatting.BLACK);
        }

        int red = (int)(reds / totalWeight);
        int green = (int)(greens / totalWeight);
        int blue = (int)(blues / totalWeight);
        return TextColor.fromRgb((red << 16) | (green << 8) | blue | 0xFF000000);
    }
}
