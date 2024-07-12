package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

public class CupcakeBlockEntity extends BlockEntity {
    private CakeBatter batter;

    public CupcakeBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CUPCAKE, pos, state);
        this.batter = null;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (this.getBatter() != null) {
            nbt.put("batter", this.getBatter().toNbt(new NbtCompound(), CakeBatter.WITH_TOP_CODEC));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("batter", NbtElement.COMPOUND_TYPE)) {
            this.batter = CakeBatter.fromNbt(nbt.getCompound("batter"));
        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        if (this.getBatter() != null) {
            componentMapBuilder.add(PBComponentTypes.BATTER, this.getBatter().copy());
        }
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        CakeBatter batter = components.get(PBComponentTypes.BATTER);
        this.setBatter(batter == null ? null : batter.copy());
    }

    public void setBatter(@Nullable CakeBatter batter) {
        this.batter = batter;
        this.markDirty();
    }

    @Nullable
    public CakeBatter getBatter() {
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
