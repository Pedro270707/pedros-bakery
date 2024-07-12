package net.pedroricardo.block.helpers;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CakeTops {
    public static final RegistryKey<Registry<CakeTop>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(PedrosBakery.MOD_ID, "cake_top"));
    public static SimpleRegistry<CakeTop> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable(), true)).buildAndRegister();

    private static CakeTop register(String name, CakeTop top) {
        return Registry.register(REGISTRY, Identifier.of(PedrosBakery.MOD_ID, name), top);
    }

    public static final CakeTop SUGAR = register("sugar", new CakeTop(null, Ingredient.ofItems(Items.SUGAR), 0xFFFDF4D8) {
        @Override
        public void onDrink(ItemStack stack, World world, LivingEntity user) {
            super.onDrink(stack, world, user);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 50, 2));
        }
    });
    public static final CakeTop CHOCOLATE = register("chocolate", new CakeTop(SUGAR, Ingredient.ofItems(Items.COCOA_BEANS), 0xFF594939) {
        @Override
        public void onDrink(ItemStack stack, World world, LivingEntity user) {
            super.onDrink(stack, world, user);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 100, 0));
        }
    });
    public static final CakeTop SCULK = register("sculk", new CakeTop(SUGAR, Ingredient.ofItems(Items.SCULK), 0xFF052A32) {
        @Override
        public void onDrink(ItemStack stack, World world, LivingEntity user) {
            super.onDrink(stack, world, user);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0));
        }
    });
    public static final CakeTop CHORUS = register("chorus", new CakeTop(SUGAR, Ingredient.ofItems(Items.CHORUS_FRUIT), 0xFFA381A2) {
        @Override
        public void onDrink(ItemStack stack, World world, LivingEntity user) {
            super.onDrink(stack, world, user);
            if (!world.isClient()) {
                for (int i = 0; i < 16; ++i) {
                    SoundCategory soundCategory;
                    SoundEvent soundEvent;
                    double d = user.getX() + (user.getRandom().nextDouble() - 0.5) * 16.0;
                    double e = MathHelper.clamp(user.getY() + (double)(user.getRandom().nextInt(16) - 8), world.getBottomY(), (world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1));
                    double f = user.getZ() + (user.getRandom().nextDouble() - 0.5) * 16.0;
                    if (user.hasVehicle()) {
                        user.stopRiding();
                    }
                    Vec3d vec3d = user.getPos();
                    if (!user.teleport(d, e, f, true)) continue;
                    world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(user));
                    if (user instanceof FoxEntity) {
                        soundEvent = SoundEvents.ENTITY_FOX_TELEPORT;
                        soundCategory = SoundCategory.NEUTRAL;
                    } else {
                        soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                        soundCategory = SoundCategory.PLAYERS;
                    }
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory);
                    user.onLanding();
                    break;
                }
            }
        }

        @Override
        public ActionResult onTryEat(CakeBatter layer, World world, BlockPos pos, BlockState state, PlayerEntity player, PBCakeBlockEntity cake) {
            if (world instanceof ServerWorld) {
                RegistryKey<World> registryKey = world.getRegistryKey() == World.END ? World.OVERWORLD : World.END;
                ServerWorld serverWorld = ((ServerWorld)world).getServer().getWorld(registryKey);
                if (serverWorld == null) {
                    return ActionResult.PASS;
                }
                player.teleportTo(((EndPortalBlock) Blocks.END_PORTAL).createTeleportTarget(serverWorld, player, pos));
            }
            return ActionResult.SUCCESS;
        }
    });
    public static final CakeTop RED_MUSHROOM = register("red_mushroom", new CakeTop(SUGAR, Ingredient.ofItems(Items.RED_MUSHROOM), 0xFFC92B29));
    public static final CakeTop SWEET_BERRY = register("sweet_berry", new CakeTop(SUGAR, Ingredient.ofItems(Items.SWEET_BERRIES), 0xFFF6C9BD));
    public static final CakeTop DIRT = register("dirt", new CakeTop(SUGAR, Ingredient.ofItems(Items.DIRT), 0xFF79553A));
    public static final CakeTop GRASS = register("grass", new CakeTop(SUGAR, Ingredient.ofItems(Items.SHORT_GRASS, Items.TALL_GRASS), 0xFF486E3E));

    @Nullable
    public static CakeTop from(ItemStack item) {
        if (item == null || item.getItem() == Items.AIR) {
            return null;
        }
        for (CakeTop flavor : REGISTRY.stream().toList()) {
            if (flavor.ingredient().test(item)) {
                return flavor;
            }
        }
        return null;
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing top registry");
    }
}