package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.CakeFeatures;
import net.pedroricardo.block.tags.PBTags;

import java.util.concurrent.CompletableFuture;

public class CakeFeatureTagProvider extends FabricTagProvider<CakeFeature> {
    public CakeFeatureTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, CakeFeatures.REGISTRY_KEY, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(PBTags.Features.INEDIBLE).add(CakeFeatures.GLASS);
    }
}
