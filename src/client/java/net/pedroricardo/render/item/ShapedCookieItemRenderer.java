package net.pedroricardo.render.item;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.render.PBRenderHelper;
import org.joml.Vector2i;

import java.util.*;

public class ShapedCookieItemRenderer {
    private final PixelDataGetter pixelDataGetter;
    private final Map<ItemStackKey, Set<Face>> cache = new HashMap<>();

    public ShapedCookieItemRenderer(PixelDataGetter pixelDataGetter) {
        this.pixelDataGetter = pixelDataGetter;
    }

    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!stack.contains(PBComponentTypes.COOKIE_SHAPE)) return;
        Set<Vector2i> shape = stack.get(PBComponentTypes.COOKIE_SHAPE);

        matrices.scale(-1.0f, 1.0f, -1.0f);
        matrices.translate(0.0f, 1.0f, -0.5333333f);

        ItemStackKey stackKey = new ItemStackKey(stack);
        if (!this.cache.containsKey(stackKey)) {
            this.cache.put(stackKey, getFaces(stack, shape));
        }
        Set<Face> quads = this.cache.get(stackKey);

        for (Face face : quads) {
            PBRenderHelper.createFace(face.direction(), matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(face.pixelData().texture())), face.x(), face.y(), face.z(), face.width(), face.height(), face.x(), face.y(), face.x() + 1, face.y() + 1, 16, 16, light, overlay, face.pixelData().color());
        }
    }

    private Set<Face> getFaces(ItemStack stack, Set<Vector2i> shape) {
        return cullQuads(stack, shape);
    }

    private Set<Face> getFrontAndBack(ItemStack stack, Set<Vector2i> shape) {
        Set<Face> faces = new HashSet<>();
        for (Vector2i pixel : shape) {
            faces.add(new Face(Direction.NORTH, pixel.x(), pixel.y(), 0.0f, 1.0f, 1.0f, this.pixelDataGetter.get(stack, pixel, shape)));
            faces.add(new Face(Direction.SOUTH, -pixel.x() - 1, pixel.y(), 1.0f, 1.0f, 1.0f, this.pixelDataGetter.get(stack, pixel, shape)));
        }
        return faces;
    }

    private Set<Face> cullQuads(ItemStack stack, Set<Vector2i> shape) {
        Set<Face> faces = getFrontAndBack(stack, shape);
        for (Vector2i pixel : shape) {
            if (!shape.contains(new Vector2i(pixel.x(), pixel.y() - 1))) {
                faces.add(new Face(Direction.UP, -pixel.x() - 1, 0.0f, -pixel.y(), 1.0f, 1.0f, this.pixelDataGetter.get(stack, pixel, shape)));
            }
            if (!shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))) {
                faces.add(new Face(Direction.DOWN, -pixel.x() - 1.0f, -1.0f, pixel.y() + 1.0f, 1.0f, 1.0f, this.pixelDataGetter.get(stack, pixel, shape)));
            }
            if (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y()))) {
                faces.add(new Face(Direction.WEST, 0.0f, pixel.y(), pixel.x() + 1.0f, 1.0f, 1.0f, this.pixelDataGetter.get(stack, pixel, shape)));
            }
            if (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y()))) {
                faces.add(new Face(Direction.EAST, -1.0f, pixel.y(), -pixel.x(), 1.0f, 1.0f, this.pixelDataGetter.get(stack, pixel, shape)));
            }
        }
        return faces;
    }

    record Face(Direction direction, float x, float y, float z, float width, float height, PixelData pixelData) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Face face = (Face) o;
            return Float.compare(x(), face.x()) == 0 && Float.compare(y(), face.y()) == 0 && Float.compare(z(), face.z()) == 0 && Float.compare(width(), face.width()) == 0 && Float.compare(height(), face.height()) == 0 && direction() == face.direction() && Objects.equals(pixelData(), face.pixelData());
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction(), x(), y(), z(), width(), height(), pixelData());
        }
    }

    record ItemStackKey(ItemStack stack) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackKey itemStackKey = (ItemStackKey) o;
            return ItemStack.canCombine(stack(), itemStackKey.stack());
        }

        @Override
        public int hashCode() {
            return Objects.hash(stack());
        }
    }
}
