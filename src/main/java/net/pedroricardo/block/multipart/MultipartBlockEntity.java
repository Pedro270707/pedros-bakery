package net.pedroricardo.block.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MultipartBlockEntity<T extends MultipartBlockEntity<T>> extends BlockEntity {
    private HashSet<BlockPos> partOffsets = new HashSet<>();
    private BlockPos mainOffset;

    public MultipartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.mainOffset = BlockPos.ORIGIN;
        this.partOffsets.add(BlockPos.ORIGIN);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Optional<BlockPos> optionalPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("multipart_main_offset")).result();
        this.mainOffset = optionalPos.orElseGet(this::getPos);
        if (this.mainOffset.equals(BlockPos.ORIGIN)) {
            this.partOffsets = new HashSet<>(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.getList("multipart_part_offsets", NbtElement.INT_ARRAY_TYPE)).result().orElse(new ArrayList<>()));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("multipart_main_offset", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.mainOffset == null ? this.pos : this.mainOffset).result().orElse(new NbtIntArray(new int[]{0, 0, 0})));
        if (this.mainOffset.equals(BlockPos.ORIGIN)) {
            nbt.put("multipart_part_offsets", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, new ArrayList<>(this.partOffsets)).result().orElse(new NbtList()));
        }
    }

    public void addPartPosition(BlockPos pos) {
        T main = this.getMainPart();
        if (main == this) {
            this.partOffsets.add(pos.subtract(this.getPos()));
        } else {
            main.addPartPosition(pos);
        }
        if (!this.hasWorld()) return;
        BlockEntity blockEntity = this.getWorld().getBlockEntity(pos);
        if (blockEntity instanceof MultipartBlockEntity part) part.setMainPartPosition(this.getMainPartPosition());
    }

    public void updatePartOffsets(Collection<BlockPos> parts) {
        MultipartBlockEntity main = this.getMainPart();
        if (main == this) {
            this.partOffsets = new HashSet<>(parts);
        } else {
            main.updatePartOffsets(parts);
        }
    }

    public Set<BlockPos> getPartPositions() {
        MultipartBlockEntity main = this.getMainPart();
        if (main == this) {
            return this.partOffsets.stream().map(pos -> pos.add(this.getPos())).collect(Collectors.toSet());
        }
        return main.getPartPositions();
    }

    public List<MultipartBlockEntity> getParts() {
        if (!this.hasWorld()) return new ArrayList<>();

        return this.getPartPositions().stream().filter(pos -> {
            BlockEntity blockEntity = this.getWorld().getBlockEntity(pos);
            return blockEntity != null && blockEntity.getClass() == this.getClass();
        }).map(pos -> ((MultipartBlockEntity) this.getWorld().getBlockEntity(pos))).toList();
    }

    public BlockPos getMainPartPosition() {
        return this.mainOffset.add(this.getPos());
    }

    public void setMainPartPosition(BlockPos pos) {
        this.mainOffset = pos.subtract(this.getPos());
        if (!this.hasWorld()) return;
        this.getWorld().setBlockState(this.getPos(), this.getCachedState().with(MultipartBlock.IS_MAIN_PART, this.mainOffset.equals(BlockPos.ORIGIN)));
    }

    public void updateMainPartPosition(BlockPos pos) {
        MultipartBlockEntity mainPart = this.getMainPart();
        NbtCompound compound = mainPart.createNbt();
        this.setMainPartPosition(pos);
        for (MultipartBlockEntity blockEntity : this.getParts()) {
            if (blockEntity == this) continue;
            blockEntity.setMainPartPosition(pos);
        }
        mainPart = this.getMainPart();
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
        mainPart.readNbt(compound);
        mainPart.addPartPosition(mainPart.getPos());
    }

    public T getMainPart() {
        try {
            if (!this.hasWorld() || this.getMainPartPosition() == null || this.getMainPartPosition().equals(this.getPos())) return (T) this;
            return (T) this.getWorld().getBlockEntity(this.getMainPartPosition());
        } catch (ClassCastException ignored) {
            try {
                return (T) this;
            } catch (ClassCastException ignored2) {
                return null;
            }
        }
    }

    public boolean isMainPart() {
        return this.getMainPartPosition().equals(this.getPos());
    }

    public void remove(boolean removeMain) {
        if (!this.hasWorld()) return;
        for (BlockPos partPos : this.getPartPositions()) {
            if (!removeMain && partPos.equals(this.getMainPartPosition())) continue;
            this.getWorld().removeBlock(partPos, false);
        }
    }
}
