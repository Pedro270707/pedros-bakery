package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class PBCakeBlockEntity extends BlockEntity implements MultipartBlockEntity {
    private List<CakeBatter<FullBatterSizeContainer>> batterList = Lists.newArrayList();
    private List<BlockPos> parts = Lists.newArrayList();

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
        NbtList list = new NbtList();
        for (CakeBatter<FullBatterSizeContainer> layer : this.batterList) {
            list.add(layer.toNbt(new NbtCompound(), CakeBatter.FULL_CODEC));
        }
        nbt.put("batter", list);
        nbt.put("parts", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.parts).result().orElse(new NbtList()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.readCakeNbt(nbt);
        this.parts = Lists.newArrayList(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.get("parts")).result().orElse(Lists.newArrayList()).iterator());
    }

    protected void readCakeNbt(NbtCompound nbt) {
        this.batterList = Lists.newArrayList(CakeBatter.listFrom(nbt).iterator());
    }

    public static void tick(World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        blockEntity.getBatterList().removeIf(CakeBatter::isEmpty);
        if (world.isClient()) return;
        if (blockEntity.getBatterList().isEmpty()) {
            blockEntity.removeAllParts(world);
            world.removeBlock(pos, false);
            world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
            PBHelpers.update((ServerWorld) world, pos, blockEntity);
        } else {
            blockEntity.getBatterList().forEach(batter -> CakeBatter.tick(batter, blockEntity.getBatterList(), world, pos, state, blockEntity));
            blockEntity.markDirty();
        }
        blockEntity.updateParts(world, pos, state);
    }

    public List<CakeBatter<FullBatterSizeContainer>> getBatterList() {
        return this.batterList;
    }

    public List<BlockPos> getParts() {
        return this.parts;
    }

    @Override
    public void updateParts(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof MultipartBlock<?, ?, ?> block)) return;
        VoxelShape shape = block.getFullShape(state, world, pos, ShapeContext.absent());
        if (shape.isEmpty()) return;
        Box box = shape.getBoundingBox().offset(pos);
        box = new Box(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ), Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
        this.removeAllParts(world);
        for (int x = (int)box.minX; x < box.maxX; x++) {
            for (int y = (int)box.minY; y < box.maxY; y++) {
                for (int z = (int)box.minZ; z < box.maxZ; z++) {
                    BlockPos partPos = new BlockPos(x, y, z);
                    if (!world.isInBuildLimit(partPos) || VoxelShapes.combineAndSimplify(shape, VoxelShapes.fullCube().offset(partPos.getX() - pos.getX(), partPos.getY() - pos.getY(), partPos.getZ() - pos.getZ()), BooleanBiFunction.AND).isEmpty()) continue;
                    BlockState partState = world.getBlockState(partPos);
                    if (partState.isReplaceable() && !partState.isSolidBlock(world, partPos) && !partPos.equals(pos)) {
                        this.createPart(world, block, partPos, pos);
                    }
                }
            }
        }
    }

    public float getHeight() {
        return (float) this.batterList.stream().mapToDouble((batter) -> batter.getSizeContainer().getHeight()).sum();
    }

    public VoxelShape toShape() {
        return toShape(this.getBatterList(), this.getCachedState(), this.getWorld(), this.getPos());
    }

    public static VoxelShape toShape(List<CakeBatter<FullBatterSizeContainer>> batterList, BlockState state, World world, BlockPos pos) {
        VoxelShape shape = VoxelShapes.empty();
        float currentHeight = 0;
        for (CakeBatter<FullBatterSizeContainer> batter : batterList) {
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
    public void setStackNbt(ItemStack stack) {
        super.setStackNbt(stack);
        PBHelpers.set(stack, PBComponentTypes.BATTER_LIST, this.getBatterList());
    }

    public void readFrom(ItemStack stack) {
        this.getBatterList().clear();
        this.getBatterList().addAll(PBHelpers.getOrDefault(stack, PBComponentTypes.BATTER_LIST, List.of()).stream().map(CakeBatter::copy).collect(Collectors.toCollection(Lists::newArrayList)));
    }
}
