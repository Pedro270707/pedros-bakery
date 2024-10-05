package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.CakeTops;
import net.pedroricardo.block.tags.PBTags;

import java.util.concurrent.CompletableFuture;

public class CakeTopTagProvider extends FabricTagProvider<CakeTop> {
    public CakeTopTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, CakeTops.REGISTRY_KEY, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(PBTags.Tops.INEDIBLE).add(CakeTops.DIRT, CakeTops.GRASS);
    }
}
