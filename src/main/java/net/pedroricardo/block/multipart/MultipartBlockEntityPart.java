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
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class MultipartBlockEntityPart<T extends BlockEntity & MultipartBlockEntity> extends BlockEntity {
    @Nullable
    private BlockPos parentPos;

    public MultipartBlockEntityPart(BlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable BlockPos parentPos) {
        super(type, pos, state);
        this.parentPos = parentPos;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.parentPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("parent_pos")).result().orElse(null);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.parentPos != null) {
            NbtElement parentPosNbt = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.parentPos).result().orElse(null);
            nbt.put("parent_pos", parentPosNbt);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, MultipartBlockEntityPart<?> blockEntity) {
    }

    @Override
    public void setStackNbt(ItemStack stack) {
        if (this.getParent() != null) {
            this.getParent().setStackNbt(stack);
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
