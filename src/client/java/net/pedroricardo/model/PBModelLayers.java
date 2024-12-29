package net.pedroricardo.model;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.render.*;

import java.util.Set;

public class PBModelLayers {
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();
    public static final EntityModelLayer BEATER = register("beater", "main", BeaterBlockRenderer::getTexturedModelData);
    public static final EntityModelLayer CAKE_STAND = register("cake_stand", "main", CakeStandBlockRenderer::getTexturedModelData);
    public static final EntityModelLayer PLATE = register("plate", "main", PlateBlockRenderer::getTexturedModelData);
    public static final EntityModelLayer CUPCAKE_TRAY = register("cupcake_tray", "main", CupcakeTrayBlockRenderer::getTexturedModelData);
    public static final EntityModelLayer CUPCAKE = register("cupcake", "main", CupcakeBlockRenderer::getTexturedModelData);
    public static final EntityModelLayer UNRAISED_PIE = register("pie", "unraised", PieBlockRenderer::getUnraisedTexturedModelData);
    public static final EntityModelLayer SLIGHTLY_RAISED_PIE = register("pie", "slightly_raised", PieBlockRenderer::getSlightlyRaisedTexturedModelData);
    public static final EntityModelLayer FULLY_RAISED_PIE = register("pie", "fully_raised", PieBlockRenderer::getFullyRaisedTexturedModelData);

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
        PedrosBakery.LOGGER.debug("Initializing model layer registry");
    }
}
