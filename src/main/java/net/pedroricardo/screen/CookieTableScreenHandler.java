package net.pedroricardo.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.joml.Vector2i;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CookieTableScreenHandler extends AbstractContainerMenu {
    protected final ContainerLevelAccess context;
    protected final Inventory playerInventory;
    protected final Container input;
    protected final ResultContainer output = new ResultContainer();
    protected Set<Vector2i> cookieShape = new HashSet<>();

    public CookieTableScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(PBScreenHandlerTypes.COOKIE_TABLE.get(), syncId);
        this.context = context;
        this.playerInventory = playerInventory;
        this.input = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                CookieTableScreenHandler.this.slotsChanged(this);
            }
        };
        this.addSlot(new Slot(this.input, 0, 8, 50));
        this.addSlot(new Slot(this.output, 1, 152, 50) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                (CookieTableScreenHandler.this.slots.get(0)).remove(1);
                stack.getItem().onCraftedBy(stack, player.level(), player);
            }
        });
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.playerInventory, j + i * 9 + 9, 8 + j * 18, 114 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(this.playerInventory, i, 8 + i * 18, 172));
        }
    }

    public CookieTableScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.context.execute((world, pos) -> this.clearContainer(player, this.input));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            copy = slotStack.copy();
            int inventoryStart = 2;
            int inventoryEnd = 28;
            int hotbarStart = inventoryEnd + 1;
            int hotbarEnd = 38;
            if (slotIndex == 1) {
                if (!this.moveItemStackTo(slotStack, inventoryStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, copy);
            } else if (slotIndex == 0) {
                if (!this.moveItemStackTo(slotStack, inventoryStart, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotStack.is(PBTags.Items.COOKIE_INGREDIENTS)) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= inventoryStart && slotIndex < hotbarStart ? !this.moveItemStackTo(slotStack, hotbarStart, hotbarEnd, false) : !this.moveItemStackTo(slotStack, inventoryStart, inventoryEnd, false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (slotStack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
            this.broadcastChanges();
        }
        return copy;
    }

    @Override
    public void slotsChanged(Container inventory) {
        ItemStack itemStack = this.input.getItem(0);
        if (!itemStack.is(PBTags.Items.COOKIE_INGREDIENTS)) {
            this.output.removeItemNoUpdate(0);
        } else {
            this.setShapedCookie();
        }
    }

    public void setPixel(Vector2i pixel, boolean value) {
        if (pixel.x() < 0 || pixel.x() > 15 || pixel.y() < 0 || pixel.y() > 15) return;
        if (this.input.getItem(0).isEmpty()) return;
        if (value) {
            this.cookieShape.add(pixel);
        } else {
            this.cookieShape.remove(pixel);
        }
        this.setShapedCookie();
    }

    public void setCookieShape(Set<Vector2i> cookieShape) {
        this.cookieShape = cookieShape.stream().filter(pixel -> pixel.x() >= 0 && pixel.x() <= 15 && pixel.y() >= 0 && pixel.y() <= 15).collect(Collectors.toSet());
        this.setShapedCookie();
    }

    public Set<Vector2i> getCookieShape() {
        return this.cookieShape;
    }

    private void setShapedCookie() {
        this.output.setItem(0, ItemStack.EMPTY);
        if (!this.cookieShape.isEmpty() && !this.input.getItem(0).isEmpty() && this.input.getItem(0).is(PBTags.Items.COOKIE_INGREDIENTS)) {
            ItemStack cookie = new ItemStack(PBItems.SHAPED_COOKIE.get());
            PBHelpers.set(cookie, PBComponentTypes.COOKIE_SHAPE.get(), new HashSet<>(this.cookieShape));
            this.output.setItem(0, cookie);
        }
        this.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.context.evaluate((world, pos) -> {
            if (!world.getBlockState(pos).is(PBBlocks.COOKIE_TABLE.get())) {
                return false;
            }
            return player.distanceToSqr((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
        }, true);
    }
}
