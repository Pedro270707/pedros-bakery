package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.PBHelpers;
import org.jetbrains.annotations.Nullable;

public class ItemStandBlockEntity extends BlockEntity implements Clearable {
    ItemStack stack = ItemStack.EMPTY;

    public ItemStandBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!this.stack.isEmpty()) {
            nbt.put("item", this.stack.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.stack = ItemStack.of(nbt.getCompound("item"));
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        if (this.getLevel() != null) {
            if (!this.getLevel().isClientSide()) PBHelpers.update(this, (ServerLevel) this.getLevel());
        } else {
            this.setChanged();
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void clearContent() {
        this.setStack(ItemStack.EMPTY);
    }
}
