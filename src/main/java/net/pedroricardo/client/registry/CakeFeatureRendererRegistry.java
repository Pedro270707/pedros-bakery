package net.pedroricardo.client.registry;

import com.google.common.collect.Maps;
import net.pedroricardo.block.extras.CakeFeature;

import java.util.Map;

public class CakeFeatureRendererRegistry {
    private static final Map<CakeFeature, CakeFeatureRenderer> RENDERERS = Maps.newHashMap();

    public static void register(CakeFeature feature, CakeFeatureRenderer renderer) {
        register(feature, renderer, true);
    }

    public static void register(CakeFeature feature, CakeFeatureRenderer renderer, boolean needsIrisFix) {
        RENDERERS.put(feature, renderer);
    }

    public static Map<CakeFeature, CakeFeatureRenderer> getRenderers() {
        return Maps.newHashMap(RENDERERS);
    }

    public static CakeFeatureRenderer get(CakeFeature feature) {
        return RENDERERS.get(feature);
    }
}
