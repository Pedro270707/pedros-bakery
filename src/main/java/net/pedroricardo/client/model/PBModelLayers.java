package net.pedroricardo.client.model;

import com.google.common.collect.Sets;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.pedroricardo.PedrosBakery;

import java.util.Set;

public class PBModelLayers {
    private static final Set<ModelLayerLocation> LAYERS = Sets.newHashSet();
    public static final ModelLayerLocation BEATER = create("beater", "main");
    public static final ModelLayerLocation CAKE_STAND = create("cake_stand", "main");
    public static final ModelLayerLocation PLATE = create("plate", "main");
    public static final ModelLayerLocation CUPCAKE_TRAY = create("cupcake_tray", "main");
    public static final ModelLayerLocation CUPCAKE = create("cupcake", "main");
    public static final ModelLayerLocation UNRAISED_PIE = create("pie", "unraised");
    public static final ModelLayerLocation SLIGHTLY_RAISED_PIE = create("pie", "slightly_raised");
    public static final ModelLayerLocation FULLY_RAISED_PIE = create("pie", "fully_raised");

    private static ModelLayerLocation create(String id, String layer) {
        return new ModelLayerLocation(new ResourceLocation(PedrosBakery.MOD_ID, id), layer);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering model layers");
    }
}
