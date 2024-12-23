package net.pedroricardo.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class MultipartBlockEntityPart<T extends BlockEntity & MultipartBlockEntity> extends BlockEntity {
    @Nullable
    private BlockPos parentPos;

    public MultipartBlockEntityPart(BlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable BlockPos parentPos) {
        super(type, pos, state);
        this.parentPos = parentPos;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.parentPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("parent_pos")).result().orElse(null);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.parentPos != null) {
            Tag parentPosNbt = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.parentPos).result().orElse(null);
            nbt.put("parent_pos", parentPosNbt);
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, MultipartBlockEntityPart<?> blockEntity) {
    }

    @Override
    public void saveToItem(ItemStack stack) {
        if (this.getParent() != null) {
            this.getParent().saveToItem(stack);
        }
    }

    @Nullable
    public BlockPos getParentPos() {
        return this.parentPos;
    }

    @Nullable
    public T getParent() {
        if (this.getParentPos() == null || !this.hasLevel()) return null;
        try {
            T blockEntity = (T) this.getLevel().getBlockEntity(this.parentPos);
            return blockEntity;
        } catch (ClassCastException ignored) {
            return null;
        }
    }
}
