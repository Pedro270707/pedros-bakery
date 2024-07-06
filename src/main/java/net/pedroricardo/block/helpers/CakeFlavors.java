package net.pedroricardo.block.helpers;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBCakeBlockEntity;

import java.util.Optional;

public class CakeFlavors {
    public static final RegistryKey<Registry<CakeFlavor>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(PedrosBakery.MOD_ID, "cake_flavor"));
    public static DefaultedRegistry<CakeFlavor> REGISTRY = FabricRegistryBuilder.from(new SimpleDefaultedRegistry<>(Identifier.of(PedrosBakery.MOD_ID, "vanilla").toString(), REGISTRY_KEY, Lifecycle.stable(), true)).buildAndRegister();

    private static CakeFlavor register(String name, CakeFlavor flavor) {
        return Registry.register(REGISTRY, Identifier.of(PedrosBakery.MOD_ID, name), flavor);
    }

    public static final CakeFlavor VANILLA = register("vanilla", new CakeFlavor(null, Ingredient.ofItems(Items.WHEAT)));
    public static final CakeFlavor CHOCOLATE = register("chocolate", new CakeFlavor(VANILLA, Ingredient.ofItems(Items.COCOA_BEANS)));
    public static final CakeFlavor SWEET_BERRY = register("sweet_berry", new CakeFlavor(VANILLA, Ingredient.ofItems(Items.SWEET_BERRIES)));
    public static final CakeFlavor COAL = register("coal", new CakeFlavor(null, Ingredient.ofItems(Items.COAL)) {
        @Override
        public void onTryEat(SimpleCakeBatter batter, World world, BlockPos pos, BlockState state, PlayerEntity player) {
            super.onTryEat(batter, world, pos, state, player);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 10, 2));
        }
    });
    public static final CakeFlavor TNT = register("tnt", new CakeFlavor(null, Ingredient.ofItems(Items.TNT)) {
        @Override
        public void onTryEat(SimpleCakeBatter batter, World world, BlockPos pos, BlockState state, PlayerEntity player) {
            super.onTryEat(batter, world, pos, state, player);
            world.createExplosion(null, pos.toCenterPos().getX(), pos.toCenterPos().getY(), pos.toCenterPos().getZ(), batter instanceof CakeLayer layer ? (layer.getSize() * layer.getHeight()) / 64.0f : 0.5f, World.ExplosionSourceType.BLOCK);
        }
    });
    public static final CakeFlavor PUMPKIN = register("pumpkin", new CakeFlavor(VANILLA, Ingredient.ofItems(Items.PUMPKIN)));

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
