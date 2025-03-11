package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntity;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PBCakeBlockEntity extends MultipartBlockEntity<PBCakeBlockEntity> implements ItemComponentProvider, StackReadingBlockEntity {
    private List<CakeBatter<FullBatterSizeContainer>> batterList = new ArrayList<>();
    private BlockPos centerOffset = BlockPos.ORIGIN;

    public PBCakeBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CAKE, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.isMainPart()) return;
        NbtList list = new NbtList();
        for (CakeBatter<FullBatterSizeContainer> layer : this.batterList) {
            list.add(layer.toNbt(new NbtCompound(), CakeBatter.FULL_CODEC));
        }
        nbt.put("batter", list);
        nbt.put("center_offset", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.centerOffset).result().orElse(new NbtIntArray(new int[]{0, 0, 0})));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (!this.isMainPart()) return;
        this.readCakeNbt(nbt);
        if (nbt.contains("center_offset", NbtElement.INT_ARRAY_TYPE)) {
            this.centerOffset = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt.get("center_offset")).result().orElse(BlockPos.ORIGIN);
        } else {
            this.centerOffset = BlockPos.ORIGIN;
        }
    }

    protected void readCakeNbt(NbtCompound nbt) {
        this.batterList = new ArrayList<>(CakeBatter.listFrom(nbt));
    }

    public static void tick(World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (!blockEntity.isMainPart()) return;
        if (blockEntity.getBatterList().removeIf(CakeBatter::isEmpty) && !world.isClient())
            PBHelpers.update((ServerWorld) world, pos, blockEntity);
        if (world.isClient()) return;
        if (blockEntity.getBatterList().isEmpty()) {
            blockEntity.remove(true);
            PBHelpers.update((ServerWorld) world, pos, blockEntity);
        } else {
            blockEntity.getBatterList().forEach(batter -> CakeBatter.tick(batter, blockEntity.getBatterList(), world, pos, state, blockEntity));
            blockEntity.markDirty();
        }
    }

    public List<CakeBatter<FullBatterSizeContainer>> getBatterList() {
        return this.getMainPart().batterList;
    }

    public float getHeight() {
        return (float) this.getBatterList().stream().mapToDouble((batter) -> batter.getSizeContainer().getHeight()).sum();
    }

    public VoxelShape toShape() {
        return toShape(this.getBatterList(), this.getCachedState(), this.getWorld(), this.getPos());
    }

    public static VoxelShape toShape(List<CakeBatter<FullBatterSizeContainer>> batterList, BlockState state, World world, BlockPos pos) {
        VoxelShape shape = VoxelShapes.empty();
        float currentHeight = 0;
        for (CakeBatter<FullBatterSizeContainer> batter : batterList) {
            if (batter.isEmpty()) continue;
            VoxelShape batterShape = batter.getShape(state, world, pos, ShapeContext.absent());
            shape = VoxelShapes.union(shape, batterShape.offset(0.0, currentHeight, 0.0));
            currentHeight += (float) batterShape.getMax(Direction.Axis.Y);
        }
        return shape;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void readFrom(ItemStack stack) {
        this.getBatterList().clear();
        this.getBatterList().addAll(PBHelpers.getOrDefault(stack, PBComponentTypes.BATTER_LIST, List.of()).stream().map(CakeBatter::copy).collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public void addComponents(ItemStack stack) {
        PBHelpers.set(stack, PBComponentTypes.BATTER_LIST, this.getBatterList());
    }

    public BlockPos getCenterOffset() {
        return this.getMainPart().centerOffset;
    }

    @Override
    public void updateMainPartPosition(BlockPos pos) {
        BlockPos previousCenterOffset = this.getCenterOffset();
        BlockPos previousMainPartPosition = this.getMainPartPosition();
        super.updateMainPartPosition(pos);
        this.getMainPart().centerOffset = previousCenterOffset.subtract(this.getMainPartPosition().subtract(previousMainPartPosition));
    }

    public void updateParts() {
        if (!this.hasWorld()) return;
        ((MultipartBlock<?>) this.getMainPart().getCachedState().getBlock()).remove(this.getWorld(), this.getMainPart().getPos(), false);
        ((MultipartBlock<?>) this.getMainPart().getCachedState().getBlock()).placeParts(this.getWorld(), this.getMainPart().getPos(), this.getMainPart().getCachedState());
    }
}
