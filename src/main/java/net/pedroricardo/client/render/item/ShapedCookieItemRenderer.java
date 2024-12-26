package net.pedroricardo.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.client.render.PBRenderHelper;
import org.joml.Vector2i;

import java.util.*;

public class ShapedCookieItemRenderer {
    private final PixelDataGetter pixelDataGetter;
    private final Map<Set<Vector2i>, Set<Face>> cache = new HashMap<>();

    public ShapedCookieItemRenderer(PixelDataGetter pixelDataGetter) {
        this.pixelDataGetter = pixelDataGetter;
    }

    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!PBHelpers.contains(stack, PBComponentTypes.COOKIE_SHAPE.get())) return;
        Set<Vector2i> shape = PBHelpers.get(stack, PBComponentTypes.COOKIE_SHAPE.get());

        matrices.scale(-1.0f, 1.0f, -1.0f);
        matrices.translate(0.0f, 1.0f, -0.5333333f);

        if (!this.cache.containsKey(shape)) {
            this.cache.put(shape, getFaces(shape));
        }
        Set<Face> quads = this.cache.get(shape);

        for (Face face : quads) {
            PBRenderHelper.createFace(face.direction(), matrices, vertexConsumers.getBuffer(RenderType.entityTranslucentCull(face.pixelData().texture())), face.x(), face.y(), face.z(), face.width(), face.height(), face.x(), face.y(), face.x() + 1, face.y() + 1, 16, 16, light, overlay, face.pixelData().color());
        }
    }

    private Set<Face> getFaces(Set<Vector2i> shape) {
        return cullQuads(shape);
    }

    private Set<Face> getFrontAndBack(Set<Vector2i> shape) {
        Set<Face> faces = new HashSet<>();
        for (Vector2i pixel : shape) {
            faces.add(new Face(Direction.NORTH, pixel.x(), pixel.y(), 0.0f, 1.0f, 1.0f, this.pixelDataGetter.get(pixel, shape)));
            faces.add(new Face(Direction.SOUTH, -pixel.x() - 1, pixel.y(), 1.0f, 1.0f, 1.0f, this.pixelDataGetter.get(pixel, shape)));
        }
        return faces;
    }

    private Set<Face> cullQuads(Set<Vector2i> shape) {
        Set<Face> faces = getFrontAndBack(shape);
        for (Vector2i pixel : shape) {
            if (!shape.contains(new Vector2i(pixel.x(), pixel.y() - 1))) {
                faces.add(new Face(Direction.UP, -pixel.x() - 1, 0.0f, -pixel.y(), 1.0f, 1.0f, this.pixelDataGetter.get(pixel, shape)));
            }
            if (!shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))) {
                faces.add(new Face(Direction.DOWN, -pixel.x() - 1.0f, -1.0f, pixel.y() + 1.0f, 1.0f, 1.0f, this.pixelDataGetter.get(pixel, shape)));
            }
            if (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y()))) {
                faces.add(new Face(Direction.WEST, 0.0f, pixel.y(), pixel.x() + 1.0f, 1.0f, 1.0f, this.pixelDataGetter.get(pixel, shape)));
            }
            if (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y()))) {
                faces.add(new Face(Direction.EAST, -1.0f, pixel.y(), -pixel.x(), 1.0f, 1.0f, this.pixelDataGetter.get(pixel, shape)));
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
}
