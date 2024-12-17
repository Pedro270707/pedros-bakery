package net.pedroricardo.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.joml.Vector2i;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CookieTableScreenHandler extends ScreenHandler {
    protected final ScreenHandlerContext context;
    protected final PlayerInventory playerInventory;
    protected final Inventory input;
    protected final CraftingResultInventory output = new CraftingResultInventory();
    protected Set<Vector2i> cookieShape = new HashSet<>();

    public CookieTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(PBScreenHandlerTypes.COOKIE_TABLE, syncId);
        this.context = context;
        this.playerInventory = playerInventory;
        this.input = new SimpleInventory(1) {
            @Override
            public void markDirty() {
                super.markDirty();
                CookieTableScreenHandler.this.onContentChanged(this);
            }
        };
        this.addSlot(new Slot(this.input, 0, 8, 50) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.output, 1, 152, 50) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                (CookieTableScreenHandler.this.slots.get(0)).takeStack(1);
                stack.getItem().onCraft(stack, player.getWorld(), player);
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

    public CookieTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            copy = slotStack.copy();
            int inventoryStart = 2;
            int hotbarEnd = 29;
            if (slotIndex == 1) {
                if (!this.insertItem(slotStack, inventoryStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(slotStack, copy);
            } else if (slotIndex == 0) {
                if (!this.insertItem(slotStack, inventoryStart, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotStack.isIn(PBTags.Items.COOKIE_INGREDIENTS)) {
                if (!this.insertItem(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (slotStack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, slotStack);
        }
        return copy;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.input.getStack(0);
        if (!itemStack.isIn(PBTags.Items.COOKIE_INGREDIENTS)) {
            this.output.removeStack(0);
        } else {
            this.setShapedCookie();
        }
    }

    public void setPixel(Vector2i pixel, boolean value) {
        if (pixel.x() < 0 || pixel.x() > 15 || pixel.y() < 0 || pixel.y() > 15) return;
        if (this.input.getStack(0).isEmpty()) return;
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
        this.output.setStack(0, ItemStack.EMPTY);
        if (!this.cookieShape.isEmpty()) {
            ItemStack cookie = new ItemStack(PBItems.SHAPED_COOKIE);
            PBHelpers.set(cookie, PBComponentTypes.COOKIE_SHAPE, new HashSet<>(this.cookieShape));
            this.output.setStack(0, cookie);
        }
        this.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
//        return this.context.get((world, pos) -> {
//            if (!world.getBlockState(pos).isOf(PBBlocks.COOKIE_TABLE)) {
//                return false;
//            }
//            return player.canInteractWithBlockAt(pos, 4.0);
//        }, true);
        return true;
    }
}
