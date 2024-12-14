package net.pedroricardo.block.entity;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.block.CookieJarBlock;

public class CookieJarBlockEntity extends BlockEntity implements Inventory, Clearable {
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(CookieJarBlock.MAX_COOKIES, ItemStack.EMPTY);

    public CookieJarBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.COOKIE_JAR, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.stacks = DefaultedList.ofSize(CookieJarBlock.MAX_COOKIES, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.getStacks());
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.getStacks());
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
