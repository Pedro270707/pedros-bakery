package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.pedroricardo.datagen.lang.PBENLanguageProvider;

public class PedrosBakeryDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(PBENLanguageProvider::new);
        pack.addProvider(PBLootTableProvider::new);
        pack.addProvider(PBRecipeProvider::new);
        FabricTagProvider.BlockTagProvider blockTagProvider = pack.addProvider(PBBlockTagProvider::new);
        pack.addProvider((output, registriesFuture) -> new PBItemTagProvider(output, registriesFuture, blockTagProvider));
        // broken: generate in the wrong location.
//        pack.addProvider(CakeFeatureTagProvider::new);
//        pack.addProvider(CakeFlavorTagProvider::new);
//        pack.addProvider(CakeTopTagProvider::new);
        pack.addProvider(PBModelProvider::new);
        pack.addProvider(MixingPatternProvider::new);
    }
}
