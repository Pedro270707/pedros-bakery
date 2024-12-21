package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import org.jetbrains.annotations.Nullable;

public class PBCakeBlockEntityPart extends MultipartBlockEntityPart<PBCakeBlockEntity> {
    public PBCakeBlockEntityPart(BlockPos pos, BlockState state) {
        this(pos, state, null);
    }

    public PBCakeBlockEntityPart(BlockPos pos, BlockState state, BlockPos parentPos) {
        super(PBBlockEntities.CAKE_PART, pos, state, parentPos);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
