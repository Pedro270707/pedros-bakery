package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

public class CupcakeBlockEntity extends BlockEntity implements ItemComponentProvider {
    private CakeBatter<FixedBatterSizeContainer> batter;

    public CupcakeBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CUPCAKE.get(), pos, state);
        this.batter = CakeBatter.getFixedSizeEmpty();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("batter", this.getBatter().toNbt(new CompoundTag(), CakeBatter.FIXED_SIZE_CODEC));
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.batter = CakeBatter.fromNbt(nbt.getCompound("batter"), CakeBatter.FIXED_SIZE_CODEC, CakeBatter.getFixedSizeEmpty());
    }

    public void setBatter(CakeBatter<FixedBatterSizeContainer> batter) {
        this.batter = batter;
        this.setChanged();
    }

    public CakeBatter<FixedBatterSizeContainer> getBatter() {
        return this.batter;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void readFrom(ItemStack stack) {
        this.setBatter(PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER.get(), CakeBatter.getFixedSizeEmpty()));
    }

    @Override
    public void addComponents(ItemStack stack) {
        PBHelpers.set(stack, PBComponentTypes.FIXED_SIZE_BATTER.get(), this.getBatter());
    }
}
