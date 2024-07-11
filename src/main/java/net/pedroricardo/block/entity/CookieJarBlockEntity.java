package net.pedroricardo.block.entity;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class CookieJarBlockEntity extends BlockEntity implements Inventory, Clearable {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(12, ItemStack.EMPTY);

    public CookieJarBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.COOKIE_JAR, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.stacks = DefaultedList.ofSize(12, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.getStacks(), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.getStacks(), registryLookup);
    }

    public DefaultedList<ItemStack> getStacks() {
        return this.stacks;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    @Override
    public int size() {
        return this.getStacks().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getStacks().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.getStacks().get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.getStacks().get(slot).split(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.getStacks().remove(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.getStacks().set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }
}
