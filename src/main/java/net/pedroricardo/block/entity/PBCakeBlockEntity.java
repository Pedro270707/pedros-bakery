package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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

public class PBCakeBlockEntity extends BlockEntity implements MultipartBlockEntity, ItemComponentProvider {
    private List<CakeBatter<FullBatterSizeContainer>> batterList = Lists.newArrayList();
    private List<BlockPos> parts = Lists.newArrayList();

    public PBCakeBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CAKE.get(), pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ListTag list = new ListTag();
        for (CakeBatter<FullBatterSizeContainer> layer : this.batterList) {
            list.add(layer.toNbt(new CompoundTag(), CakeBatter.FULL_CODEC));
        }
        nbt.put("batter", list);
        nbt.put("parts", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.parts).result().orElse(new ListTag()));
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.readCakeNbt(nbt);
        this.parts = Lists.newArrayList(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.get("parts")).result().orElse(Lists.newArrayList()).iterator());
    }

    protected void readCakeNbt(CompoundTag nbt) {
        this.batterList = Lists.newArrayList(CakeBatter.listFrom(nbt).iterator());
    }

    public static void tick(Level world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        blockEntity.getBatterList().removeIf(CakeBatter::isEmpty);
        if (world.isClientSide()) return;
        if (blockEntity.getBatterList().isEmpty()) {
            blockEntity.removeAllParts(world);
            world.removeBlock(pos, false);
            world.gameEvent(null, GameEvent.BLOCK_DESTROY, pos);
            PBHelpers.update((ServerLevel) world, pos, blockEntity);
        } else {
            blockEntity.getBatterList().forEach(batter -> CakeBatter.tick(batter, blockEntity.getBatterList(), world, pos, state, blockEntity));
            blockEntity.setChanged();
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
    public void updateParts(Level world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof MultipartBlock<?, ?, ?> block)) return;
        VoxelShape shape = block.getFullShape(state, world, pos, CollisionContext.empty());
        if (shape.isEmpty()) return;
        AABB box = shape.bounds().move(pos);
        box = new AABB(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ), Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
        this.removeAllParts(world);
        for (int x = (int)box.minX; x < box.maxX; x++) {
            for (int y = (int)box.minY; y < box.maxY; y++) {
                for (int z = (int)box.minZ; z < box.maxZ; z++) {
                    BlockPos partPos = new BlockPos(x, y, z);
                    if (!world.isInWorldBounds(partPos) || Shapes.join(shape, Shapes.block().move(partPos.getX() - pos.getX(), partPos.getY() - pos.getY(), partPos.getZ() - pos.getZ()), BooleanOp.AND).isEmpty()) continue;
                    BlockState partState = world.getBlockState(partPos);
                    if (partState.canBeReplaced() && !partState.isSolidRender(world, partPos) && !partPos.equals(pos)) {
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
        return toShape(this.getBatterList(), this.getBlockState(), this.getLevel(), this.getBlockPos());
    }

    public static VoxelShape toShape(List<CakeBatter<FullBatterSizeContainer>> batterList, BlockState state, Level world, BlockPos pos) {
        VoxelShape shape = Shapes.empty();
        float currentHeight = 0;
        for (CakeBatter<FullBatterSizeContainer> batter : batterList) {
            VoxelShape batterShape = batter.getShape(state, world, pos, CollisionContext.empty());
            shape = Shapes.or(shape, batterShape.move(0.0, currentHeight, 0.0));
            currentHeight += (float) batterShape.max(Direction.Axis.Y);
        }
        return shape;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void readFrom(ItemStack stack) {
        this.getBatterList().clear();
        this.getBatterList().addAll(PBHelpers.getOrDefault(stack, PBComponentTypes.BATTER_LIST.get(), List.of()).stream().map(CakeBatter::copy).collect(Collectors.toCollection(Lists::newArrayList)));
    }

    @Override
    public void addComponents(ItemStack stack) {
        PBHelpers.set(stack, PBComponentTypes.BATTER_LIST.get(), this.getBatterList());
    }
}
