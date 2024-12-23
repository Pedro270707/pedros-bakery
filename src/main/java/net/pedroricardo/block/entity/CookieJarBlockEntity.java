package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.block.CookieJarBlock;

public class CookieJarBlockEntity extends BlockEntity implements Container, Clearable {
    private NonNullList<ItemStack> stacks = NonNullList.withSize(CookieJarBlock.MAX_COOKIES, ItemStack.EMPTY);

    public CookieJarBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.COOKIE_JAR.get(), pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.stacks = NonNullList.withSize(CookieJarBlock.MAX_COOKIES, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.getStacks());
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.getStacks());
    }

    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }

    @Override
    public void clearContent() {
        this.stacks.clear();
    }

    @Override
    public int getContainerSize() {
        return this.getStacks().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getStacks().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.getStacks().get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return this.getStacks().get(slot).split(amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.getStacks().remove(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.getStacks().set(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
