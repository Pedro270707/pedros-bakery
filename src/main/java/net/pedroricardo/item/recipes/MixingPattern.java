package net.pedroricardo.item.recipes;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.pedroricardo.block.extras.beater.Liquid;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class MixingPattern {
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

    public static class Serializer implements JsonDeserializer<MixingPattern>, JsonSerializer<MixingPattern> {
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().registerTypeHierarchyAdapter(MixingPattern.class, new Serializer()).create();

        @Override
        public MixingPattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                throw new JsonParseException("Mixing pattern is not a JSON object");
            }
            JsonObject object = json.getAsJsonObject();
            final List<Ingredient> ingredients = new ArrayList<>();
            Liquid base;
            Liquid result;
            if (object.has("base")) {
                base = Liquid.CODEC.decode(JsonOps.INSTANCE, object.getAsJsonObject("base")).get().orThrow().getFirst();
            } else {
                throw new JsonParseException("Mixing pattern has no element 'base'");
            }
            if (object.has("result")) {
                result = Liquid.CODEC.decode(JsonOps.INSTANCE, object.getAsJsonObject("result")).get().orThrow().getFirst();
            } else {
                throw new JsonParseException("Mixing pattern has no element 'result'");
            }
            if (object.has("ingredients")) {
                object.getAsJsonArray("ingredients").forEach(element -> ingredients.add(Ingredient.fromJson(element, false)));
            }
            return new MixingPattern(ingredients, base, result);
        }

        @Override
        public JsonElement serialize(MixingPattern src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add("base", Liquid.CODEC.encodeStart(JsonOps.INSTANCE, src.getBase()).get().orThrow());
            object.add("result", Liquid.CODEC.encodeStart(JsonOps.INSTANCE, src.getResult()).get().orThrow());
            JsonArray ingredients = new JsonArray();
            src.getIngredients().forEach(ingredient -> ingredients.add(ingredient.toJson()));
            object.add("ingredients", ingredients);
            return object;
        }

        @Nullable
        public static MixingPattern fromJson(JsonElement json) {
            return GSON.fromJson(json, MixingPattern.class);
        }

        public static String toJson(MixingPattern pattern) {
            return GSON.toJson(pattern);
        }

        public static JsonElement toJsonTree(MixingPattern pattern) {
            return GSON.toJsonTree(pattern);
        }
    }
}
