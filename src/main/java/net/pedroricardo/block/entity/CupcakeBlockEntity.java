package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

public class CupcakeBlockEntity extends BlockEntity implements ItemComponentProvider {
    private CakeBatter<FixedBatterSizeContainer> batter;

    public CupcakeBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CUPCAKE, pos, state);
        this.batter = CakeBatter.getFixedSizeEmpty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("batter", this.getBatter().toNbt(new NbtCompound(), CakeBatter.FIXED_SIZE_CODEC));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.batter = CakeBatter.fromNbt(nbt.getCompound("batter"), CakeBatter.FIXED_SIZE_CODEC, CakeBatter.getFixedSizeEmpty());
    }

    public void setBatter(CakeBatter<FixedBatterSizeContainer> batter) {
        this.batter = batter;
        this.markDirty();
    }

    public CakeBatter<FixedBatterSizeContainer> getBatter() {
        return this.batter;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void readFrom(ItemStack stack) {
        this.setBatter(PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()));
    }

    @Override
    public void addComponents(ItemStack stack) {
        PBHelpers.set(stack, PBComponentTypes.FIXED_SIZE_BATTER, this.getBatter());
    }
}
