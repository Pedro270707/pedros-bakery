package net.pedroricardo.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.ButterChurnBlock;
import net.pedroricardo.block.CookieJarBlock;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.item.PBItems;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;

public class PBModelProvider extends FabricModelProvider {
    public PBModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        Identifier templateCake = Models.PARTICLE.upload(Identifier.of(PedrosBakery.MOD_ID, "block/template_cake"), TextureMap.particle(TextureMap.getSubId(Blocks.CAKE, "_side")), blockStateModelGenerator.modelCollector);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.WHITE_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.ORANGE_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.MAGENTA_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.LIGHT_BLUE_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.YELLOW_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.LIME_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.PINK_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.GRAY_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.LIGHT_GRAY_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.CYAN_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.PURPLE_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.BLUE_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.BROWN_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.GREEN_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.RED_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.BLACK_CANDLE_CAKE, templateCake);
        registerParentedBlockModel(blockStateModelGenerator, PBBlocks.CAKE_PART, templateCake);
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.BEATER, TextureMap.getId(Blocks.IRON_BLOCK));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.BAKING_TRAY, TextureMap.getId(Blocks.IRON_BLOCK));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.BAKING_TRAY_PART, TextureMap.getId(Blocks.IRON_BLOCK));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.CAKE_STAND, TextureMap.getId(Blocks.GLASS));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.PLATE, TextureMap.getId(Blocks.GLASS));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.EXPANDABLE_BAKING_TRAY, TextureMap.getId(Blocks.IRON_BLOCK));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.CUPCAKE_TRAY, TextureMap.getId(Blocks.ANVIL));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.CUPCAKE, Identifier.of(PedrosBakery.MOD_ID, "item/cupcake_liner"));
        blockStateModelGenerator.registerBuiltinWithParticle(PBBlocks.PIE, TextureMap.getId(Blocks.IRON_BLOCK));
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(PBBlocks.COOKIE_JAR).coordinate(BlockStateVariantMap.create(CookieJarBlock.COOKIES)
                .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(PBBlocks.COOKIE_JAR)))
                .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_one_cookie")))
                .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_two_cookies")))
                .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_three_cookies")))
                .register(4, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_four_cookies")))
                .register(5, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_five_cookies")))
                .register(6, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_six_cookies")))
                .register(7, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_seven_cookies")))
                .register(8, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_eight_cookies")))
                .register(9, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_nine_cookies")))
                .register(10, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_ten_cookies")))
                .register(11, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_eleven_cookies")))
                .register(12, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.COOKIE_JAR, "_twelve_cookies")))
        ));
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(PBBlocks.BUTTER_CHURN).coordinate(BlockStateVariantMap.create(ButterChurnBlock.CHURN_STATE)
                .register(ButterChurnBlock.ChurnState.EMPTY, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(PBBlocks.BUTTER_CHURN)))
                .register(ButterChurnBlock.ChurnState.MILK, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.BUTTER_CHURN, "_with_milk")))
                .register(ButterChurnBlock.ChurnState.BUTTER, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(PBBlocks.BUTTER_CHURN, "_with_butter")))
        ));

        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(PBBlocks.CAKE);
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(PBBlocks.CUPCAKE_TRAY);
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(PBBlocks.CUPCAKE);
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(PBBlocks.PIE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(PBBlocks.BEATER.asItem(), Models.GENERATED);
        itemModelGenerator.register(PBBlocks.CAKE_STAND.asItem(), Models.GENERATED);
        itemModelGenerator.register(PBBlocks.PLATE.asItem(), Models.GENERATED);
        itemModelGenerator.register(PBBlocks.COOKIE_JAR.asItem(), Models.GENERATED);
        Models.GENERATED.upload(Identifier.of(PedrosBakery.MOD_ID, "item/cupcake_liner"), TextureMap.layer0(Identifier.of(PedrosBakery.MOD_ID, "item/cupcake_liner")), itemModelGenerator.writer);
        Models.GENERATED_TWO_LAYERS.upload(ModelIds.getItemModelId(PBItems.FROSTING_BOTTLE), TextureMap.layered(TextureMap.getId(PBItems.FROSTING_BOTTLE).withSuffixedPath("_overlay"), TextureMap.getId(PBItems.FROSTING_BOTTLE)), itemModelGenerator.writer);
        registerDonut(itemModelGenerator, PBItems.DONUT);
        itemModelGenerator.register(PBItems.WHITE_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.ORANGE_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.MAGENTA_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.LIGHT_BLUE_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.YELLOW_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.LIME_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.PINK_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.GRAY_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.LIGHT_GRAY_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.CYAN_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.PURPLE_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.BLUE_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.BROWN_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.GREEN_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.RED_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.BLACK_SPRINKLES, Models.GENERATED);
        itemModelGenerator.register(PBItems.APPLE_COOKIE, Models.GENERATED);
        itemModelGenerator.register(PBItems.BUTTER, Models.GENERATED);
        itemModelGenerator.register(PBItems.BUTTER_CHURN_STAFF, Models.HANDHELD);
        registerParentedItemModel(itemModelGenerator, PBBlocks.EXPANDABLE_BAKING_TRAY.asItem(), Identifier.of(PedrosBakery.MOD_ID, "item/template_baking_tray"));
        registerParentedItemModel(itemModelGenerator, PBBlocks.BAKING_TRAY.asItem(), Identifier.of(PedrosBakery.MOD_ID, "item/template_baking_tray"));
        registerParentedItemModel(itemModelGenerator, PBBlocks.BUTTER_CHURN.asItem(), ModelIds.getBlockModelId(PBBlocks.BUTTER_CHURN));
    }

    private static void registerDonut(ItemModelGenerator itemModelGenerator, Item donut) {
        Models.GENERATED_TWO_LAYERS.upload(Registries.ITEM.getId(donut).withPrefixedPath("item/frosted_"), TextureMap.layered(TextureMap.getId(donut), TextureMap.getId(donut).withSuffixedPath("_frosting")), itemModelGenerator.writer);
        registerGeneratedWithPredicate(itemModelGenerator, donut, List.of(Triple.of(PedrosBakery.MOD_ID + ":frosted", 1, Registries.ITEM.getId(donut).withPrefixedPath("item/frosted_"))));
    }

    private static void registerGeneratedWithPredicate(ItemModelGenerator itemModelGenerator, Item item, List<Triple<String, Number, Identifier>> predicates) {
        Identifier modelId = ModelIds.getItemModelId(item);
        JsonObject withoutPredicateJsonObject = Models.GENERATED.createJson(modelId,
                Map.of(TextureKey.LAYER0, TextureMap.getId(item)));
        JsonArray overrides = new JsonArray();
        for (Triple<String, Number, Identifier> predicateTriple : predicates) {
            JsonObject predicateJson = new JsonObject();
            JsonObject override = new JsonObject();
            predicateJson.addProperty(predicateTriple.getLeft(), predicateTriple.getMiddle());
            override.add("predicate", predicateJson);
            override.addProperty("model", predicateTriple.getRight().toString());
            overrides.add(override);
        }
        withoutPredicateJsonObject.add("overrides", overrides);
        itemModelGenerator.writer.accept(modelId, () -> withoutPredicateJsonObject);
    }

    private static void registerParentedItemModel(ItemModelGenerator generator, Item item, Identifier parentModelId) {
        generator.writer.accept(ModelIds.getItemModelId(item), new SimpleModelSupplier(parentModelId));
    }

    private static void registerParentedBlockModel(BlockStateModelGenerator generator, Block block, Identifier parentModelId) {
        generator.modelCollector.accept(ModelIds.getBlockModelId(block), new SimpleModelSupplier(parentModelId));
        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, ModelIds.getBlockModelId(block)));
    }
}
