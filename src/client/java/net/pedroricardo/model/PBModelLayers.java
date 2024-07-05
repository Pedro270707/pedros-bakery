package net.pedroricardo.model;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.render.BeaterBlockRenderer;
import net.pedroricardo.render.CakeStandBlockRenderer;
import net.pedroricardo.render.CupcakeTrayBlockRenderer;

import java.util.Set;

public class PBModelLayers {
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();
    public static final EntityModelLayer BEATER = register("beater", "main", BeaterBlockRenderer::getTexturedModelData);
    public static final EntityModelLayer CAKE_STAND = register("cake_stand", "main", CakeStandBlockRenderer::getTexturedModelData);
    public static final EntityModelLayer CUPCAKE_TRAY = register("cupcake_tray", "main", CupcakeTrayBlockRenderer::getTexturedModelData);

    private static EntityModelLayer register(String id, String layer, EntityModelLayerRegistry.TexturedModelDataProvider texturedModelProvider) {
        EntityModelLayer entityModelLayer = create(id, layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        }
        EntityModelLayerRegistry.registerModelLayer(entityModelLayer, texturedModelProvider);
        return entityModelLayer;
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(Identifier.of(PedrosBakery.MOD_ID, id), layer);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering model layers");
    }
}
