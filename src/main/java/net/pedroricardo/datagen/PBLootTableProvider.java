package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.StatePredicate;
import net.pedroricardo.block.ButterChurnBlock;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.util.CopyComponentsLootFunction;
import net.pedroricardo.item.PBItems;

public class PBLootTableProvider extends FabricBlockLootTableProvider {
    public PBLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        this.addDrop(PBBlocks.CAKE, LootTable.builder());
        this.addDrop(PBBlocks.CANDLE_CAKE, Items.CANDLE);
        this.addDrop(PBBlocks.WHITE_CANDLE_CAKE, Items.CANDLE);
        this.addDrop(PBBlocks.ORANGE_CANDLE_CAKE, Items.ORANGE_CANDLE);
        this.addDrop(PBBlocks.MAGENTA_CANDLE_CAKE, Items.MAGENTA_CANDLE);
        this.addDrop(PBBlocks.LIGHT_BLUE_CANDLE_CAKE, Items.LIGHT_BLUE_CANDLE);
        this.addDrop(PBBlocks.YELLOW_CANDLE_CAKE, Items.YELLOW_CANDLE);
        this.addDrop(PBBlocks.LIME_CANDLE_CAKE, Items.LIME_CANDLE);
        this.addDrop(PBBlocks.PINK_CANDLE_CAKE, Items.PINK_CANDLE);
        this.addDrop(PBBlocks.GRAY_CANDLE_CAKE, Items.GRAY_CANDLE);
        this.addDrop(PBBlocks.LIGHT_GRAY_CANDLE_CAKE, Items.LIGHT_GRAY_CANDLE);
        this.addDrop(PBBlocks.CYAN_CANDLE_CAKE, Items.CYAN_CANDLE);
        this.addDrop(PBBlocks.PURPLE_CANDLE_CAKE, Items.PURPLE_CANDLE);
        this.addDrop(PBBlocks.BLUE_CANDLE_CAKE, Items.BLUE_CANDLE);
        this.addDrop(PBBlocks.BROWN_CANDLE_CAKE, Items.BROWN_CANDLE);
        this.addDrop(PBBlocks.GREEN_CANDLE_CAKE, Items.GREEN_CANDLE);
        this.addDrop(PBBlocks.RED_CANDLE_CAKE, Items.RED_CANDLE);
        this.addDrop(PBBlocks.BLACK_CANDLE_CAKE, Items.BLACK_CANDLE);
        this.addDrop(PBBlocks.CAKE_PART, LootTable.builder());
        this.addDrop(PBBlocks.BEATER);
        this.addDrop(PBBlocks.BAKING_TRAY);
        this.addDrop(PBBlocks.BAKING_TRAY_PART, LootTable.builder());
        this.addDrop(PBBlocks.CAKE_STAND);
        this.addDrop(PBBlocks.PLATE);
        this.addDrop(PBBlocks.EXPANDABLE_BAKING_TRAY);
        this.addDrop(PBBlocks.CUPCAKE_TRAY, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(PBBlocks.CUPCAKE_TRAY).apply(CopyComponentsLootFunction.builder())).build()));
        this.addDrop(PBBlocks.CUPCAKE);
        this.addDrop(PBBlocks.COOKIE_JAR);
        this.addDrop(PBBlocks.BUTTER_CHURN, LootTable.builder()
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(PBBlocks.BUTTER_CHURN)))
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(PBItems.BUTTER)
                                .conditionally(BlockStatePropertyLootCondition.builder(PBBlocks.BUTTER_CHURN)
                                        .properties(StatePredicate.Builder.create().exactMatch(ButterChurnBlock.CHURN_STATE, ButterChurnBlock.ChurnState.BUTTER))))));
        this.addDrop(PBBlocks.PIE, LootTable.builder()
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(PBBlocks.PIE)
                                .apply(CopyComponentsLootFunction.builder()))));
        this.addDrop(PBBlocks.COOKIE_TABLE);
    }
}
