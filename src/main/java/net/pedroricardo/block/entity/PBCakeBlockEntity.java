package net.pedroricardo.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.helpers.CakeLayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PBCakeBlockEntity extends BlockEntity {
    private List<CakeLayer> layers = new ArrayList<>();

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
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.readCakeNbt(nbt);
    }

    protected void readCakeNbt(NbtCompound nbt) {
        this.layers = CakeLayer.listFrom(nbt);
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
    }

    public List<CakeLayer> getLayers() {
        return this.layers;
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
