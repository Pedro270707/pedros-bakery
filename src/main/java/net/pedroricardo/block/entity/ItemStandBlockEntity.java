package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.PBHelpers;
import org.jetbrains.annotations.Nullable;

public class ItemStandBlockEntity extends BlockEntity implements Clearable {
    ItemStack stack = ItemStack.EMPTY;

    public ItemStandBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.stack.isEmpty()) {
            nbt.put("item", this.stack.writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.stack = ItemStack.fromNbt(nbt.getCompound("item"));
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        if (this.getWorld() != null) {
            if (!this.getWorld().isClient()) PBHelpers.update(this, (ServerWorld) this.getWorld());
        } else {
            this.markDirty();
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void clear() {
        this.setStack(ItemStack.EMPTY);
    }
}
