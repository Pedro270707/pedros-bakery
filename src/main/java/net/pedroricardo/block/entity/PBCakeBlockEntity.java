package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PBCakeBlockEntity extends MultipartBlockEntity {
    private List<CakeBatter<FullBatterSizeContainer>> batterList = new ArrayList<>();

    public PBCakeBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CAKE, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.isMainPart()) return;
        NbtList list = new NbtList();
        for (CakeBatter<FullBatterSizeContainer> layer : this.batterList) {
            list.add(layer.toNbt(new NbtCompound(), CakeBatter.FULL_CODEC));
        }
        nbt.put("batter", list);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (!this.isMainPart()) return;
        this.readCakeNbt(nbt);
    }

    protected void readCakeNbt(NbtCompound nbt) {
        this.batterList = new ArrayList<>(CakeBatter.listFrom(nbt));
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(PBComponentTypes.BATTER_LIST, this.getBatterList().stream().map(CakeBatter::copy).collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.batterList = components.getOrDefault(PBComponentTypes.BATTER_LIST, List.<CakeBatter<FullBatterSizeContainer>>of()).stream().map(CakeBatter::copy).collect(Collectors.toCollection(ArrayList::new));
    }

    public static void tick(World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        blockEntity.getBatterList().removeIf(CakeBatter::isEmpty);
        if (world.isClient()) return;
        if (!blockEntity.isMainPart()) return;
        if (blockEntity.getBatterList().isEmpty()) {
            blockEntity.remove(true);
            world.removeBlock(pos, false);
            world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
            PBHelpers.update((ServerWorld) world, pos, blockEntity);
        } else {
            blockEntity.getBatterList().forEach(batter -> CakeBatter.tick(batter, blockEntity.getBatterList(), world, pos, state, blockEntity));
            blockEntity.markDirty();
        }
    }

    public List<CakeBatter<FullBatterSizeContainer>> getBatterList() {
        return this.batterList;
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
}
