package net.pedroricardo.block.extras;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;

import java.util.Optional;

public class CakeFlavors {
    public static final RegistryKey<Registry<CakeFlavor>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(PedrosBakery.MOD_ID, "cake_flavor"));
    public static DefaultedRegistry<CakeFlavor> REGISTRY = FabricRegistryBuilder.from(new SimpleDefaultedRegistry<>(Identifier.of(PedrosBakery.MOD_ID, "vanilla").toString(), REGISTRY_KEY, Lifecycle.stable(), true)).buildAndRegister();

    private static CakeFlavor register(String name, CakeFlavor flavor) {
        return Registry.register(REGISTRY, Identifier.of(PedrosBakery.MOD_ID, name), flavor);
    }

    public static final CakeFlavor VANILLA = register("vanilla", new CakeFlavor(null, Ingredient.of(Items.WHEAT)));
    public static final CakeFlavor CHOCOLATE = register("chocolate", new CakeFlavor(VANILLA, Ingredient.of(Items.COCOA_BEANS)));
    public static final CakeFlavor SWEET_BERRY = register("sweet_berry", new CakeFlavor(VANILLA, Ingredient.of(Items.SWEET_BERRIES)));
    public static final CakeFlavor COAL = register("coal", new CakeFlavor(null, Ingredient.of(Items.COAL)) {
        @Override
        public ActionResult onTryEat(CakeBatter<?> batter, World world, BlockPos pos, BlockState state, PlayerEntity player) {
            super.onTryEat(batter, world, pos, state, player);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 10, 2));
            return ActionResult.SUCCESS;
        }
    });
    public static final CakeFlavor TNT = register("tnt", new CakeFlavor(null, Ingredient.of(Items.TNT)) {
        @Override
        public ActionResult onTryEat(CakeBatter<?> batter, World world, BlockPos pos, BlockState state, PlayerEntity player) {
            super.onTryEat(batter, world, pos, state, player);
            try {
                world.createExplosion(null, pos.toCenterPos().getX(), pos.toCenterPos().getY(), pos.toCenterPos().getZ(), (((CakeBatter<FullBatterSizeContainer>)batter).getSizeContainer().getSize() * ((CakeBatter<FullBatterSizeContainer>)batter).getSizeContainer().getHeight()) / 64.0f, World.ExplosionSourceType.BLOCK);
            } catch (ClassCastException ignored) {
                world.createExplosion(null, pos.toCenterPos().getX(), pos.toCenterPos().getY(), pos.toCenterPos().getZ(), 0.5f, World.ExplosionSourceType.BLOCK);
            }
            return ActionResult.SUCCESS;
        }
    });
    public static final CakeFlavor PUMPKIN = register("pumpkin", new CakeFlavor(VANILLA, Ingredient.of(Items.PUMPKIN, Items.PUMPKIN_PIE)));
    public static final CakeFlavor MELON = register("melon", new CakeFlavor(VANILLA, Ingredient.of(Items.MELON, Items.MELON_SLICE)));
    public static final CakeFlavor BREAD = register("bread", new CakeFlavor(VANILLA, Ingredient.of(Items.BREAD)));

    public static Optional<CakeFlavor> from(ItemStack item) {
        if (item == null || item.getItem() == Items.AIR) {
            return Optional.empty();
        }
        for (CakeFlavor flavor : REGISTRY.stream().toList()) {
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
