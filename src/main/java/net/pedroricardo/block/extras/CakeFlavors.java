package net.pedroricardo.block.extras;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = PedrosBakery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CakeFlavors {
    public static final ResourceKey<Registry<CakeFlavor>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PedrosBakery.MOD_ID, "cake_flavor"));
    public static final DeferredRegister<CakeFlavor> CAKE_FLAVORS = DeferredRegister.create(REGISTRY_KEY, PedrosBakery.MOD_ID);
    public static Supplier<IForgeRegistry<CakeFlavor>> registrySupplier; // Note: registry supplier value may be NULL

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void newRegistryEvent(@NotNull NewRegistryEvent event) {
        registrySupplier = event.create(new RegistryBuilder<CakeFlavor>()
                .setName(REGISTRY_KEY.location())
                .hasTags()
                .setDefaultKey(new ResourceLocation(PedrosBakery.MOD_ID, "vanilla")));
    }

    public static final RegistryObject<CakeFlavor> VANILLA = CAKE_FLAVORS.register("vanilla", () -> new CakeFlavor(null, Ingredient.of(Items.WHEAT)));
    public static final RegistryObject<CakeFlavor> CHOCOLATE = CAKE_FLAVORS.register("chocolate", () -> new CakeFlavor(VANILLA.get(), Ingredient.of(Items.COCOA_BEANS)));
    public static final RegistryObject<CakeFlavor> SWEET_BERRY = CAKE_FLAVORS.register("sweet_berry", () -> new CakeFlavor(VANILLA.get(), Ingredient.of(Items.SWEET_BERRIES)));
    public static final RegistryObject<CakeFlavor> COAL = CAKE_FLAVORS.register("coal", () -> new CakeFlavor(null, Ingredient.of(Items.COAL)) {
        @Override
        public InteractionResult onTryEat(CakeBatter<?> batter, Level world, BlockPos pos, BlockState state, Player player) {
            super.onTryEat(batter, world, pos, state, player);
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 10, 2));
            return InteractionResult.SUCCESS;
        }
    });
    public static final RegistryObject<CakeFlavor> TNT = CAKE_FLAVORS.register("tnt", () -> new CakeFlavor(null, Ingredient.of(Items.TNT)) {
        @Override
        public InteractionResult onTryEat(CakeBatter<?> batter, Level world, BlockPos pos, BlockState state, Player player) {
            super.onTryEat(batter, world, pos, state, player);
            try {
                world.explode(null, pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(), (((CakeBatter<FullBatterSizeContainer>)batter).getSizeContainer().getSize() * ((CakeBatter<FullBatterSizeContainer>)batter).getSizeContainer().getHeight()) / 64.0f, Level.ExplosionInteraction.BLOCK);
            } catch (ClassCastException ignored) {
                world.explode(null, pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(), 0.5f, Level.ExplosionInteraction.BLOCK);
            }
            return InteractionResult.SUCCESS;
        }
    });
    public static final RegistryObject<CakeFlavor> PUMPKIN = CAKE_FLAVORS.register("pumpkin", () -> new CakeFlavor(VANILLA.get(), Ingredient.of(Items.PUMPKIN, Items.PUMPKIN_PIE)));
    public static final RegistryObject<CakeFlavor> MELON = CAKE_FLAVORS.register("melon", () -> new CakeFlavor(VANILLA.get(), Ingredient.of(Items.MELON, Items.MELON_SLICE)));
    public static final RegistryObject<CakeFlavor> BREAD = CAKE_FLAVORS.register("bread", () -> new CakeFlavor(VANILLA.get(), Ingredient.of(Items.BREAD)));

    public static Optional<CakeFlavor> from(ItemStack item) {
        if (item == null || item.getItem() == Items.AIR || registrySupplier.get() == null) {
            return Optional.empty();
        }
        for (CakeFlavor flavor : registrySupplier.get().getValues().stream().toList()) {
            if (flavor.ingredient().test(item)) {
                return Optional.of(flavor);
            }
        }
        return Optional.empty();
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing flavor registry");
    }
}
