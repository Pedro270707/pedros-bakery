package net.pedroricardo.render;

import net.minecraft.client.model.ModelPart;
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
        createFace(direction, matrices, vertexConsumer, x, y, z, width, height, u, v, u + width, v + height, 64.0f, 64.0f, light, overlay, color);
    }

    public static void createFace(Direction direction, MatrixStack matrices, VertexConsumer vertexConsumer, float x, float y, float z, float width, float height, float u, float v, float u2, float v2, float textureWidth, float textureHeight, int light, int overlay, int color) {
        matrices.push();
        MatrixStack.Entry entry = matrices.peek();
        Vector3f rotationEuler = direction.getRotationQuaternion().getEulerAnglesXYZ(new Vector3f());
        Quaternionf rotation = new Quaternionf().rotateXYZ(rotationEuler.x() + 3.1415927f, rotationEuler.y() + 3.1415927f, rotationEuler.z() + 3.1415927f).normalize();
        entry.getPositionMatrix().rotate(rotation);

        ModelPart.Vertex vertex3 = new ModelPart.Vertex(x + width, z, y, 8.0f, 8.0f);
        ModelPart.Vertex vertex4 = new ModelPart.Vertex(x, z, y, 8.0f, 0.0f);
        ModelPart.Vertex vertex7 = new ModelPart.Vertex(x + width, z, y + height, 8.0f, 8.0f);
        ModelPart.Vertex vertex8 = new ModelPart.Vertex(x, z, y + height, 8.0f, 0.0f);
        ModelPart.Quad quad = new ModelPart.Quad(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, u, v, u2, v2, textureWidth, textureHeight, false, direction);
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f2 = entry.transformNormal(quad.direction, vector3f);
        float normalX = vector3f2.x();
        float normalY = vector3f2.y();
        float normalZ = vector3f2.z();
        for (ModelPart.Vertex vertex : quad.vertices) {
            float i = vertex.pos.x() / 16.0f;
            float j = vertex.pos.y() / 16.0f;
            float k = vertex.pos.z() / 16.0f;
            Vector3f vector3f3 = entry.getPositionMatrix().transformPosition(i, j, k, vector3f);
            vertexConsumer.vertex(vector3f3.x(), vector3f3.y(), vector3f3.z(), color, vertex.u, vertex.v, overlay, light, normalX, normalY, normalZ);
        }

        entry.getPositionMatrix().rotate(rotation.conjugate());
        matrices.pop();
    }
}
