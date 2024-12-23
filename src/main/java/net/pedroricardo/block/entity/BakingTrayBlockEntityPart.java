package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import org.jetbrains.annotations.Nullable;

public class BakingTrayBlockEntityPart extends MultipartBlockEntityPart<BakingTrayBlockEntity> {
    public BakingTrayBlockEntityPart(BlockPos pos, BlockState state) {
        this(pos, state, null);
    }

    public BakingTrayBlockEntityPart(BlockPos pos, BlockState state, BlockPos parentPos) {
        super(PBBlockEntities.BAKING_TRAY_PART.get(), pos, state, parentPos);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}