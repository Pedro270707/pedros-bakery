package net.pedroricardo.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.pedroricardo.block.entity.ItemComponentProvider;

import java.util.Set;

public class CopyComponentsLootFunction extends ConditionalLootFunction {
    protected CopyComponentsLootFunction(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ContextLootNbtProvider.BLOCK_ENTITY.getRequiredParameters();
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof ItemComponentProvider provider) {
            provider.addComponents(stack);
        }
        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return PBLootFunctionTypes.COPY_COMPONENTS;
    }

    public static CopyComponentsLootFunction.Builder builder() {
        return new CopyComponentsLootFunction.Builder();
    }

    public static class Builder extends ConditionalLootFunction.Builder<CopyComponentsLootFunction.Builder> {
        @Override
        protected CopyComponentsLootFunction.Builder getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new CopyComponentsLootFunction(this.getConditions());
        }
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<CopyComponentsLootFunction> {
        @Override
        public CopyComponentsLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return new CopyComponentsLootFunction(conditions);
        }
    }
}
