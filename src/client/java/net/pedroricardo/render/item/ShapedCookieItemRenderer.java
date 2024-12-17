package net.pedroricardo.render.item;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.render.PBRenderHelper;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Collectors;

public class ShapedCookieItemRenderer {
    private final PixelDataGetter pixelDataGetter;
    private final Map<Set<Vector2i>, Set<Face>> cache = new HashMap<>();

    public ShapedCookieItemRenderer(PixelDataGetter pixelDataGetter) {
        this.pixelDataGetter = pixelDataGetter;
    }

    public static final Identifier LIGHT_BORDER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_light_border.png");
    public static final Identifier DARK_BORDER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_dark_border.png");
    public static final Identifier LIGHT_INNER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_light_inner.png");
    public static final Identifier DARK_INNER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_dark_inner.png");

    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!PBHelpers.contains(stack, PBComponentTypes.COOKIE_SHAPE)) return;
        Set<Vector2i> shape = PBHelpers.get(stack, PBComponentTypes.COOKIE_SHAPE);

        matrices.translate(1.0f, 1.0f, 0.4666666f);

//        if (!this.cache.containsKey(shape)) {
            this.cache.put(shape, getFaces(shape));
//        }
        Set<Face> quads = this.cache.get(shape);

        for (Face face : quads) {
            PBRenderHelper.createFace(face.direction(), matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(face.pixelData().texture())), face.x(), face.y(), face.z(), face.width(), face.height(), face.x(), face.y(), face.x() + 1, face.y() + 1, 16, 16, light, overlay, face.pixelData().color());
        }
    }

    private Set<Face> getFaces(Set<Vector2i> shape) {
        shape = shape.stream().map(vector -> new Vector2i(-vector.x() + 15, vector.y())).collect(Collectors.toSet());
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
