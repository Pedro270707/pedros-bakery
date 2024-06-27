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
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.MultipartBlock;
import net.pedroricardo.block.helpers.CakeLayer;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class PBCakeBlockEntity extends BlockEntity {
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
            world.removeBlock(pos, false);
            world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
            PBHelpers.updateListeners(world, pos, state, blockEntity);
        } else {
            blockEntity.getLayers().forEach(layer -> layer.tick(world, pos, state, blockEntity));
        }
        VoxelShape shape = state.getBlock() instanceof MultipartBlock ? ((MultipartBlock)state.getBlock()).getFullShape(state, world, pos, ShapeContext.absent()) : blockEntity.toShape(state.getOrEmpty(Properties.HORIZONTAL_FACING).orElse(Direction.NORTH));
        if (shape.isEmpty()) return;
        Box box = shape.getBoundingBox().offset(pos);
        box = new Box(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ), Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
        blockEntity.parts.clear();
        for (int x = (int)box.minX; x < box.maxX; x++) {
            for (int y = (int)box.minY; y < box.maxY; y++) {
                for (int z = (int)box.minZ; z < box.maxZ; z++) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    if (!world.isInBuildLimit(pos1) || VoxelShapes.combineAndSimplify(shape, VoxelShapes.fullCube().offset(pos1.getX() - pos.getX(), pos1.getY() - pos.getY(), pos1.getZ() - pos.getZ()), BooleanBiFunction.AND).isEmpty()) continue;
                    BlockState state1 = world.getBlockState(pos1);
                    if ((state1.isReplaceable() && !state1.isSolidBlock(world, pos1)) && !pos1.equals(pos)) {
                        if (!state1.isOf(PBBlocks.CAKE_PART)) {
                            BlockState partState = PBBlocks.CAKE_PART.getDefaultState();
                            PBCakePartBlockEntity partBlockEntity = new PBCakePartBlockEntity(pos1, partState, pos);
                            world.setBlockState(pos1, partState);
                            world.addBlockEntity(partBlockEntity);
                        }
                        blockEntity.parts.add(pos1);
                    }
                }
            }
        }
    }

    public List<CakeLayer> getLayers() {
        return this.layers;
    }

    public List<BlockPos> getParts() {
        return this.parts.stream().map(BlockPos::toImmutable).toList();
    }

    public float getHeight() {
        return (float) this.layers.stream().mapToDouble(CakeLayer::getHeight).sum();
    }

    public VoxelShape toShape(Direction direction) {
        VoxelShape shape = VoxelShapes.empty();
        float currentHeight = 0;
        for (CakeLayer cakeLayer : this.getLayers()) {
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
