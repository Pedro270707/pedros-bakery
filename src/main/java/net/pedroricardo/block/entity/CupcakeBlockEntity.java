package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

public class CupcakeBlockEntity extends BlockEntity {
    private CakeBatter<FixedBatterSizeContainer> batter;

    public CupcakeBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CUPCAKE, pos, state);
        this.batter = CakeBatter.getFixedSizeEmpty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("batter", this.getBatter().toNbt(new NbtCompound(), CakeBatter.FIXED_SIZE_CODEC));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.batter = CakeBatter.fromNbt(nbt.getCompound("batter"), CakeBatter.FIXED_SIZE_CODEC, CakeBatter.getFixedSizeEmpty());
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(PBComponentTypes.FIXED_SIZE_BATTER, this.getBatter().copy());
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        CakeBatter<FixedBatterSizeContainer> batter = components.getOrDefault(PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty());
        this.setBatter(batter.copy());
    }

    public void setBatter(CakeBatter<FixedBatterSizeContainer> batter) {
        this.batter = batter;
        this.markDirty();
    }

    public CakeBatter<FixedBatterSizeContainer> getBatter() {
        return this.batter;
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove("batter");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
