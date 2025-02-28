package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
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
        this.getOrCreateTagBuilder(PBTags.Items.COOKIES).add(Items.COOKIE, PBItems.APPLE_COOKIE, PBItems.SHAPED_COOKIE);
        this.getOrCreateTagBuilder(PBTags.Items.CAKE_STAND_ITEM).add(Items.CAKE, PBBlocks.CAKE.asItem(), PBBlocks.CUPCAKE.asItem(), PBBlocks.PIE.asItem());
        this.getOrCreateTagBuilder(PBTags.Items.COOKIE_INGREDIENTS).add(PBItems.DOUGH);
        this.getOrCreateTagBuilder(PBTags.Items.CURDLES_CHEESE).add(Items.LEATHER, Items.RABBIT_HIDE);
        this.getOrCreateTagBuilder(PBTags.Items.UNLOCKS_CHEESE_RECIPES).addTag(PBTags.Items.CURDLES_CHEESE).add(Items.MILK_BUCKET, PBItems.CHEESE, PBItems.HARD_CHEESE);

        this.getOrCreateTagBuilder(ConventionalItemTags.FOODS).add(PBItems.DONUT, PBItems.APPLE_COOKIE, PBItems.BUTTER, PBItems.SHAPED_COOKIE, PBItems.CHEESE, PBItems.TORTILLA, PBItems.QUESADILLA);
    }
}
