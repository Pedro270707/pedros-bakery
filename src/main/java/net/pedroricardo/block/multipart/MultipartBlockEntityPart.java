package net.pedroricardo.block.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.mixin.ReadComponentsAccessor;
import org.jetbrains.annotations.Nullable;

public abstract class MultipartBlockEntityPart<T extends BlockEntity & MultipartBlockEntity> extends BlockEntity {
    @Nullable
    private BlockPos parentPos;

    public MultipartBlockEntityPart(BlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable BlockPos parentPos) {
        super(type, pos, state);
        this.parentPos = parentPos;
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

    public static void tick(World world, BlockPos pos, BlockState state, MultipartBlockEntityPart<?> blockEntity) {
        if (state.getOutlineShape(world, pos).isEmpty()) {
            world.setBlockState(pos, state.with(MultipartBlockPart.DELEGATE, false));
            world.removeBlock(pos, false);
            PBHelpers.updateListeners(world, pos, state, blockEntity);
        } else if (!(state.getBlock() instanceof MultipartBlockPart<?, ?> part) || !part.stillValid(world, pos)) {
            world.setBlockState(pos, state.with(MultipartBlockPart.DELEGATE, false));
            world.breakBlock(pos, false);
            PBHelpers.updateListeners(world, pos, state, blockEntity);
        }
    }

    @Override
    public void setStackNbt(ItemStack stack, RegistryWrapper.WrapperLookup registries) {
        if (this.getParent() != null) {
            this.getParent().setStackNbt(stack, registries);
        }
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        if (this.getParent() != null) {
            this.getParent().removeFromCopiedStackNbt(nbt);
        }
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        if (this.getParent() != null) {
            ((ReadComponentsAccessor) this.getParent()).invokeReadComponents(components);
        }
    }

    @Nullable
    public BlockPos getParentPos() {
        return this.parentPos;
    }

    @Nullable
    public T getParent() {
        if (this.getParentPos() == null || !this.hasWorld()) return null;
        try {
            T blockEntity = (T) this.getWorld().getBlockEntity(this.parentPos);
            return blockEntity;
        } catch (ClassCastException ignored) {
            return null;
        }
    }
}
