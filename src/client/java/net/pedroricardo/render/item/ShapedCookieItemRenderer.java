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

public class ShapedCookieItemRenderer {
    public static final Identifier LIGHT_BORDER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_light_border.png");
    public static final Identifier DARK_BORDER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_dark_border.png");
    public static final Identifier LIGHT_INNER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_light_inner.png");
    public static final Identifier DARK_INNER_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_dark_inner.png");

    private static final Map<Set<Vector2i>, CookieFaces> cache = new HashMap<>();

    public static void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!PBHelpers.contains(stack, PBComponentTypes.COOKIE_SHAPE)) return;
        Set<Vector2i> shape = PBHelpers.get(stack, PBComponentTypes.COOKIE_SHAPE);

        matrices.translate(1.3333333f, 1.3333333f, 0.45f);
        matrices.scale(1.6666666f, 1.6666666f, 1.6666666f);

        if (!cache.containsKey(shape)) {
            cache.put(shape, getFaces(shape));
        }
        CookieFaces quads = cache.get(shape);

        for (Face face : quads.lightBorder()) {
            PBRenderHelper.createFace(face.direction(), matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(LIGHT_BORDER_TEXTURE)), face.x(), face.y(), face.z(), face.width(), face.height(), face.x(), face.y(), face.x() + 1, face.y() + 1, 16, 16, light, overlay, 0xFFFFFFFF);
        }
        for (Face face : quads.darkBorder()) {
            PBRenderHelper.createFace(face.direction(), matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(DARK_BORDER_TEXTURE)), face.x(), face.y(), face.z(), face.width(), face.height(), face.x(), face.y(), face.x() + 1, face.y() + 1, 16, 16, light, overlay, 0xFFFFFFFF);
        }
        for (Face face : quads.lightInner()) {
            PBRenderHelper.createFace(face.direction(), matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(LIGHT_INNER_TEXTURE)), face.x(), face.y(), face.z(), face.width(), face.height(), face.x(), face.y(), face.x() + 1, face.y() + 1, 16, 16, light, overlay, 0xFFFFFFFF);
        }
        for (Face face : quads.darkInner()) {
            PBRenderHelper.createFace(face.direction(), matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(DARK_INNER_TEXTURE)), face.x(), face.y(), face.z(), face.width(), face.height(), face.x(), face.y(), face.x() + 1, face.y() + 1, 16, 16, light, overlay, 0xFFFFFFFF);
        }
    }

    private static CookieFaces getFaces(Set<Vector2i> shape) {
        Set<Vector2i> lightBorder = new HashSet<>();
        Set<Vector2i> darkBorder = new HashSet<>();
        Set<Vector2i> lightInner = new HashSet<>();
        Set<Vector2i> darkInner = new HashSet<>();

        Set<Vector2i> remaining = new HashSet<>(shape);
        for (Vector2i pixel : shape) {
            if ((!shape.contains(new Vector2i(pixel.x() + 1, pixel.y())) && shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))) || !shape.contains(new Vector2i(pixel.x(), pixel.y() - 1))) {
                lightBorder.add(pixel);
                remaining.remove(pixel);
            } else if (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y())) || !shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))) {
                darkBorder.add(pixel);
                remaining.remove(pixel);
            }
        }
        for (Vector2i pixel : remaining) {
            if (!remaining.contains(new Vector2i(pixel.x(), pixel.y() + 1)) || !remaining.contains(new Vector2i(pixel.x(), pixel.y() + 2))) {
                darkInner.add(pixel);
            } else {
                lightInner.add(pixel);
            }
        }
        return cullQuads(lightBorder, darkBorder, lightInner, darkInner, shape);
    }

    private static CookieFaces cullQuads(Set<Vector2i> lightBorder, Set<Vector2i> darkBorder, Set<Vector2i> lightInner, Set<Vector2i> darkInner, Set<Vector2i> shape) {
        return new CookieFaces(cullQuads(lightBorder, shape), cullQuads(darkBorder, shape), getFrontAndBack(lightInner), getFrontAndBack(darkInner));
    }

    private static Set<Face> getFrontAndBack(Set<Vector2i> set) {
        Set<Face> faces = new HashSet<>();
        for (Vector2i pixel : set) {
            faces.add(new Face(Direction.NORTH, pixel.x(), pixel.y(), 0.0f, 1.0f, 1.0f));
            faces.add(new Face(Direction.SOUTH, -pixel.x() - 1, pixel.y(), 1.0f, 1.0f, 1.0f));
        }
        return faces;
    }

    private static Set<Face> cullQuads(Set<Vector2i> set, Set<Vector2i> shape) {
        Set<Face> faces = getFrontAndBack(set);
        for (Vector2i pixel : set) {
            if (!shape.contains(new Vector2i(pixel.x(), pixel.y() - 1))) {
                faces.add(new Face(Direction.UP, -pixel.x() - 1, 0.0f, -pixel.y(), 1.0f, 1.0f));
            }
            if (!shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))) {
                faces.add(new Face(Direction.DOWN, -pixel.x() - 1.0f, -1.0f, pixel.y() + 1.0f, 1.0f, 1.0f));
            }
            if (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y()))) {
                faces.add(new Face(Direction.WEST, 0.0f, pixel.y(), pixel.x() + 1.0f, 1.0f, 1.0f));
            }
            if (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y()))) {
                faces.add(new Face(Direction.EAST, -1.0f, pixel.y(), -pixel.x(), 1.0f, 1.0f));
            }
        }
        return faces;
    }

    record Face(Direction direction, float x, float y, float z, float width, float height) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Face face = (Face) o;
            return Float.compare(x(), face.x()) == 0 && Float.compare(y(), face.y()) == 0 && Float.compare(z(), face.z()) == 0 && Float.compare(width(), face.width()) == 0 && Float.compare(height(), face.height()) == 0 && direction() == face.direction();
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction(), x(), y(), z(), width(), height());
        }
    }
}
