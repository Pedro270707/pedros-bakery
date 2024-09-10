package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.pedroricardo.block.helpers.CakeFlavor;
import net.pedroricardo.block.helpers.CakeFlavors;
import net.pedroricardo.block.tags.PBTags;

import java.util.concurrent.CompletableFuture;

public class CakeFlavorTagProvider extends FabricTagProvider<CakeFlavor> {
    public CakeFlavorTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, CakeFlavors.REGISTRY_KEY, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(PBTags.Flavors.INEDIBLE).add(CakeFlavors.COAL, CakeFlavors.TNT);
    }
}
