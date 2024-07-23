package net.pedroricardo.registry;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.size.FullBatterSizeContainer;

public interface CakeFeatureRenderer {
    void render(CakeFeature feature, PBCakeBlockEntity entity, CakeBatter<FullBatterSizeContainer> layer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
