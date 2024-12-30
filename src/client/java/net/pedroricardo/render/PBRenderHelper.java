package net.pedroricardo.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PBRenderHelper {
    public static void createFace(Direction direction, MatrixStack matrices, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, float u, float v, int light, int overlay, int color) {
        createFace(direction, matrices, vertexConsumer, x, y, z, width, height, u, v, u + width, v + height, light, overlay, color);
    }

    public static void createFace(Direction direction, MatrixStack matrices, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, float u, float v, float u2, float v2, int light, int overlay, int color) {
        createFace(direction, matrices, vertexConsumer, x, y, z, width, height, u, v, u2, v2, 128.0f, 128.0f, light, overlay, color);
    }

    public static void createFace(Direction direction, MatrixStack matrices, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, float u, float v, float u2, float v2, float textureWidth, float textureHeight, int light, int overlay, int color) {
        matrices.push();
        MatrixStack.Entry entry = matrices.peek();
        Vector3f rotationEuler = direction.getRotationQuaternion().getEulerAnglesXYZ(new Vector3f());
        Quaternionf rotation = new Quaternionf().rotateXYZ(rotationEuler.x() + 3.1415927f, rotationEuler.y() + 3.1415927f, rotationEuler.z() + 3.1415927f).normalize();
        entry.getPositionMatrix().rotate(rotation);

        Vector3f vector3f = new Vector3f();
        Vector3f normal = entry.getNormalMatrix().transform(direction.getUnitVector(), vector3f);
        float normalX = normal.x();
        float normalY = normal.y();
        float normalZ = normal.z();

        Vector3f pos = entry.getPositionMatrix().transformPosition((x + width) / 16.0f, z / 16.0f, y / 16.0f, vector3f);
        vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), ((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, ((color >> 24) & 0xFF) / 255.0f, u2 / textureWidth, v / textureHeight, overlay, light, normalX, normalY, normalZ);
        pos = entry.getPositionMatrix().transformPosition(x / 16.0f, z / 16.0f, y / 16.0f, vector3f);
        vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), ((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, ((color >> 24) & 0xFF) / 255.0f, u / textureWidth, v / textureHeight, overlay, light, normalX, normalY, normalZ);
        pos = entry.getPositionMatrix().transformPosition(x / 16.0f, z / 16.0f, (y + height) / 16.0f, vector3f);
        vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), ((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, ((color >> 24) & 0xFF) / 255.0f, u / textureWidth, v2 / textureHeight, overlay, light, normalX, normalY, normalZ);
        pos = entry.getPositionMatrix().transformPosition((x + width) / 16.0f, z / 16.0f, (y + height) / 16.0f, vector3f);
        vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), ((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, ((color >> 24) & 0xFF) / 255.0f, u2 / textureWidth, v2 / textureHeight, overlay, light, normalX, normalY, normalZ);

        entry.getPositionMatrix().rotate(rotation.conjugate());
        matrices.pop();
    }
}
