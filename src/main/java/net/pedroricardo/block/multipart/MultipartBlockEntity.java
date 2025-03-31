package net.pedroricardo.block.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MultipartBlockEntity<T extends MultipartBlockEntity<T>> extends BlockEntity {
    protected HashSet<BlockPos> partOffsets = new HashSet<>();
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
            this.partOffsets = new HashSet<>(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.get("multipart_part_offsets")).result().orElse(new ArrayList<>()));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("multipart_main_offset", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.mainOffset == null ? this.pos : this.mainOffset).result().orElse(new NbtIntArray(new int[]{0, 0, 0})));
        if (this.mainOffset.equals(BlockPos.ORIGIN)) {
            nbt.put("multipart_part_offsets", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, new ArrayList<>(this.partOffsets)).result().orElseThrow());
        }
    }

    @SuppressWarnings("unchecked")
    public void addPartPosition(BlockPos pos) {
        T main = this.getMainPart();
        if (main == this) {
            this.partOffsets.add(pos.subtract(this.getPos()));
        } else {
            main.addPartPosition(pos);
        }
        if (!this.hasWorld()) return;
        BlockEntity blockEntity = this.getWorld().getBlockEntity(pos);
        try {
            if (blockEntity == null) return;
            ((T) blockEntity).setMainPartPosition(this.getMainPartPosition());
        } catch (ClassCastException ignored) {
        }
    }

    public void updatePartOffsets(Collection<BlockPos> parts) {
        T main = this.getMainPart();
        if (main == this) {
            this.partOffsets = new HashSet<>(parts);
        } else {
            main.updatePartOffsets(parts);
        }
    }

    public Set<BlockPos> getPartPositions() {
        T main = this.getMainPart();
        if (main == this) {
            return this.partOffsets.stream().map(pos -> pos.add(this.getPos())).collect(Collectors.toSet());
        }
        return main.getPartPositions();
    }

    @SuppressWarnings("unchecked")
    public List<T> getParts() {
        if (!this.hasWorld()) return new ArrayList<>();

        return this.getPartPositions().stream().filter(pos -> {
            BlockEntity blockEntity = this.getWorld().getBlockEntity(pos);
            return blockEntity != null && blockEntity.getClass() == this.getClass();
        }).map(pos -> ((T) this.getWorld().getBlockEntity(pos))).toList();
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
        T oldMainPart = this.getMainPart();
        NbtCompound compound = oldMainPart.createNbt();
        List<T> parts = this.getParts();
        this.setMainPartPosition(pos);
        for (T blockEntity : parts) {
            if (blockEntity == this) continue;
            blockEntity.setMainPartPosition(pos);
        }
        T mainPart = this.getMainPart();
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
        mainPart.readNbt(compound);
        Set<BlockPos> set = oldMainPart.partOffsets.stream().map(offset -> offset.add(oldMainPart.getPos()).subtract(pos)).collect(Collectors.toSet());
        mainPart.partOffsets.clear();
        mainPart.partOffsets.addAll(set);
        mainPart.addPartPosition(mainPart.getPos());
    }

    @SuppressWarnings("unchecked")
    public T getMainPart() {
        if (!this.hasWorld() || this.getMainPartPosition() == null || this.getMainPartPosition().equals(this.getPos())) {
            return (T) this;
        }

        BlockEntity entity = this.getWorld().getBlockEntity(this.getMainPartPosition());

        if (entity instanceof MultipartBlockEntity<?>) {
            try {
                return (T) entity;
            } catch (ClassCastException e) {
                return (T) this;
            }
        }

        return (T) this;
    }

    public boolean isMainPart() {
        return this.getMainPartPosition().equals(this.getPos());
    }

    public void remove(boolean removeMain) {
        if (!this.hasWorld()) return;
        for (BlockPos partPos : this.getPartPositions()) {
            if (!removeMain && partPos.equals(this.getMainPartPosition())) continue;
//            if (partPos.equals(new BlockPos(-31, 173, 16))) new Throwable().printStackTrace();
            this.getWorld().removeBlock(partPos, false);
            this.getWorld().emitGameEvent(null, GameEvent.BLOCK_DESTROY, partPos);
        }
        this.getMainPart().partOffsets.clear();
        this.getMainPart().partOffsets.add(BlockPos.ORIGIN);
    }
}
