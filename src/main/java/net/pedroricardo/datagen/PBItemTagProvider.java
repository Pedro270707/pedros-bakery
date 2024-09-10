package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PBItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public PBItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(PBTags.Items.COOKIES).add(Items.COOKIE, PBItems.APPLE_COOKIE);
        this.getOrCreateTagBuilder(PBTags.Items.CAKE_STAND_ITEM).add(Items.CAKE, PBBlocks.CAKE.asItem(), PBBlocks.CUPCAKE.asItem());
    }
}
