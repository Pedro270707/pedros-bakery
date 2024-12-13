package net.pedroricardo.item.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.pedroricardo.block.extras.beater.Liquid;

import java.util.*;

public class MixingPattern {
    public static final Codec<MixingPattern> CODEC = RecordCodecBuilder.create(instance -> instance.group(Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients").forGetter(MixingPattern::getIngredients), Liquid.CODEC.fieldOf("base").forGetter(MixingPattern::getBase), Liquid.CODEC.fieldOf("result").forGetter(MixingPattern::getResult)).apply(instance, MixingPattern::new));

    private final List<Ingredient> ingredients;
    private final Liquid base;
    private final Liquid result;

    public MixingPattern(List<Ingredient> ingredients, Liquid base, Liquid result) {
        this.ingredients = ingredients;
        this.base = base;
        this.result = result;
    }

    public boolean matches(Collection<ItemStack> stacks, Liquid base) {
        if (!this.getBase().equals(base)) return false;
        Set<ItemStack> matchedStacks = new HashSet<>();
        for (Ingredient ingredient : this.ingredients) {
            boolean matched = false;
            for (ItemStack stack : stacks) {
                if (!ingredient.test(stack) || matchedStacks.contains(stack)) continue;
                matchedStacks.add(stack);
                matched = true;
            }
            if (!matched) return false;
        }
        return matchedStacks.size() == stacks.size();
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public Liquid getResult() {
        return this.result;
    }

    public Liquid getBase() {
        return this.base;
    }
}
