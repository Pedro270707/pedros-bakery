package net.pedroricardo.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.pedroricardo.block.entity.ItemComponentProvider;

import java.util.Set;

public class CopyComponentsLootFunction extends LootItemConditionalFunction {
    protected CopyComponentsLootFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ContextNbtProvider.BLOCK_ENTITY.getReferencedContextParams();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity blockEntity = context.getParam(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof ItemComponentProvider provider) {
            provider.addComponents(stack);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return PBLootFunctionTypes.COPY_COMPONENTS.get();
    }

    public static CopyComponentsLootFunction.Builder builder() {
        return new CopyComponentsLootFunction.Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyComponentsLootFunction.Builder> {
        @Override
        protected CopyComponentsLootFunction.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyComponentsLootFunction(this.getConditions());
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyComponentsLootFunction> {
        @Override
        public CopyComponentsLootFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new CopyComponentsLootFunction(conditions);
        }
    }
}
