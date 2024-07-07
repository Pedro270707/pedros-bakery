package net.pedroricardo.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.helpers.CakeLayer;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PBCakeBlockEntity extends BlockEntity implements MultipartBlockEntity {
    private List<CakeLayer> layers = Lists.newArrayList();
    private List<BlockPos> parts = Lists.newArrayList();

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
        NbtList list = new NbtList();
        for (CakeLayer layer : this.layers) {
            list.add(layer.toNbt(new NbtCompound()));
        }
        nbt.put("layers", list);
        nbt.put("parts", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.parts).result().orElse(new NbtList()));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.readCakeNbt(nbt);
        this.parts = Lists.newArrayList(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.get("parts")).result().orElse(Lists.newArrayList()).iterator());
    }

    protected void readCakeNbt(NbtCompound nbt) {
        this.layers = Lists.newArrayList(CakeLayer.listFrom(nbt).iterator());
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        readCakeNbt(components.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).copyNbt());
    }

    public static void tick(World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        blockEntity.getLayers().removeIf(CakeLayer::isEmpty);
        if (blockEntity.getLayers().isEmpty()) {
            blockEntity.removeAllParts(world);
            world.removeBlock(pos, false);
            world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
            PBHelpers.updateListeners(world, pos, state, blockEntity);
        } else {
            blockEntity.getLayers().forEach(layer -> layer.tick(world, pos, state, blockEntity));
            blockEntity.markDirty();
        }
        if (!world.isClient()) {
            blockEntity.updateParts(world, pos, state);
        }
    }

    public List<CakeLayer> getLayers() {
        return this.layers;
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
        return (float) this.layers.stream().mapToDouble(CakeLayer::getHeight).sum();
    }

    public VoxelShape toShape(Direction direction) {
        return toShape(this.getLayers(), direction);
    }

    public static VoxelShape toShape(List<CakeLayer> layers, Direction direction) {
        VoxelShape shape = VoxelShapes.empty();
        float currentHeight = 0;
        for (CakeLayer cakeLayer : layers) {
            shape = switch (direction) {
                default -> VoxelShapes.union(shape, Block.createCuboidShape(8 - cakeLayer.getSize() / 2.0, currentHeight, 8 - cakeLayer.getSize() / 2.0, 8 + cakeLayer.getSize() / 2.0 - cakeLayer.getBites(), currentHeight + cakeLayer.getHeight(), 8 + cakeLayer.getSize() / 2.0));
                case SOUTH -> VoxelShapes.union(shape, Block.createCuboidShape(8 - cakeLayer.getSize() / 2.0 + cakeLayer.getBites(), currentHeight, 8 - cakeLayer.getSize() / 2.0, 8 + cakeLayer.getSize() / 2.0, currentHeight + cakeLayer.getHeight(), 8 + cakeLayer.getSize() / 2.0));
                case WEST -> VoxelShapes.union(shape, Block.createCuboidShape(8 - cakeLayer.getSize() / 2.0, currentHeight, 8 - cakeLayer.getSize() / 2.0 + cakeLayer.getBites(), 8 + cakeLayer.getSize() / 2.0, currentHeight + cakeLayer.getHeight(), 8 + cakeLayer.getSize() / 2.0));
                case EAST -> VoxelShapes.union(shape, Block.createCuboidShape(8 - cakeLayer.getSize() / 2.0, currentHeight, 8 - cakeLayer.getSize() / 2.0, 8 + cakeLayer.getSize() / 2.0, currentHeight + cakeLayer.getHeight(), 8 + cakeLayer.getSize() / 2.0 - cakeLayer.getBites()));
            };
            currentHeight += cakeLayer.getHeight();
        }
        return shape;
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove("parts");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
