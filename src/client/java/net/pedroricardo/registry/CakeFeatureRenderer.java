package net.pedroricardo.registry;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeLayer;

public interface CakeFeatureRenderer {
    void render(PBCakeBlockEntity entity, CakeLayer layer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
