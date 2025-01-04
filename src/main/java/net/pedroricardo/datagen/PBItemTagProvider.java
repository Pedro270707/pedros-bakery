package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
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
        this.getOrCreateTagBuilder(PBTags.Items.COOKIES).add(Items.COOKIE, PBItems.APPLE_COOKIE, PBItems.SHAPED_COOKIE).addOptional(Identifier.of("bakery", "strawberry_glazed_cookie")).addOptional(Identifier.of("bakery", "sweetberry_glazed_cookie")).addOptional(Identifier.of("bakery", "chocolate_glazed_cookie"));
        this.getOrCreateTagBuilder(PBTags.Items.CAKE_STAND_ITEM).add(Items.CAKE, PBBlocks.CAKE.asItem(), PBBlocks.CUPCAKE.asItem(), PBBlocks.PIE.asItem());
        this.getOrCreateTagBuilder(PBTags.Items.COOKIE_INGREDIENTS).add(PBItems.DOUGH).addOptional(Identifier.of("create", "dough")).addOptional(Identifier.of("farm_and_charm", "dough"));
        this.getOrCreateTagBuilder(PBTags.Items.FROSTABLES).add(PBItems.DONUT, PBItems.SHAPED_COOKIE);
    }
}
