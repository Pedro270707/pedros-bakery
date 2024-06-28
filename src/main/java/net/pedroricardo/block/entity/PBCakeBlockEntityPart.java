package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PBCakeBlockEntityPart extends MultipartBlockEntityPart {
    public PBCakeBlockEntityPart(BlockPos pos, BlockState state) {
        this(pos, state, null);
    }

    public PBCakeBlockEntityPart(BlockPos pos, BlockState state, BlockPos parentPos) {
        super(PBBlockEntities.CAKE_PART, pos, state, parentPos);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
