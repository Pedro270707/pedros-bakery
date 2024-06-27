package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.PBCakePartBlock;
import org.jetbrains.annotations.Nullable;

public class PBCakePartBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos parentPos;

    public PBCakePartBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CAKE_PART, pos, state);
    }

    public PBCakePartBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos) {
        this(pos, state);
        this.parentPos = parentPos;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.parentPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("parent_pos")).result().orElse(null);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (this.parentPos != null) {
            NbtElement parentPosNbt = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.parentPos).result().orElse(null);
            nbt.put("parent_pos", parentPosNbt);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, PBCakePartBlockEntity blockEntity) {
        if (state.getOutlineShape(world, pos).isEmpty()) {
            world.setBlockState(pos, state.with(PBCakePartBlock.DELEGATE, false));
            world.removeBlock(pos, false);
        } else if (blockEntity.parentPos == null || !(world.getBlockEntity(blockEntity.parentPos) instanceof PBCakeBlockEntity) || blockEntity.getCachedState().getOutlineShape(world, pos).isEmpty()) {
            world.setBlockState(pos, state.with(PBCakePartBlock.DELEGATE, false));
            world.breakBlock(pos, false);
        }
    }

    @Override
    public void setStackNbt(ItemStack stack, RegistryWrapper.WrapperLookup registries) {
        if (this.getParent() != null) {
            this.getParent().setStackNbt(stack, registries);
        }
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        if (this.getParent() != null) {
            this.getParent().readComponents(components);
        }
    }

    @Nullable
    public BlockPos getParentPos() {
        return this.parentPos;
    }

    @Nullable
    public PBCakeBlockEntity getParent() {
        if (this.parentPos == null || !this.hasWorld() || !(this.getWorld().getBlockEntity(this.parentPos) instanceof PBCakeBlockEntity cake)) {
            return null;
        }
        return cake;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
