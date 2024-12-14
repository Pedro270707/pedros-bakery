package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PBSounds;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingTrayBlockEntity extends BlockEntity implements MultipartBlockEntity {
    private int size = PedrosBakery.CONFIG.bakingTrayDefaultSize();
    private int height = PedrosBakery.CONFIG.bakingTrayDefaultHeight();
    private CakeBatter<HeightOnlyBatterSizeContainer> cakeBatter = CakeBatter.getHeightOnlyEmpty();
    private List<BlockPos> parts = Lists.newArrayList();

    public BakingTrayBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.BAKING_TRAY, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("size", this.size);
        nbt.putInt("height", this.height);
        nbt.put("batter", this.getCakeBatter().toNbt(new NbtCompound(), CakeBatter.WITH_HEIGHT_CODEC));
        nbt.put("parts", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.parts).result().orElse(new NbtList()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("size", NbtElement.INT_TYPE)) {
            this.size = nbt.getInt("size");
        }
        if (nbt.contains("height", NbtElement.INT_TYPE)) {
            this.height = nbt.getInt("height");
        }
        if (nbt.contains("batter", NbtElement.COMPOUND_TYPE)) {
            this.cakeBatter = CakeBatter.fromNbt(nbt.getCompound("batter"), CakeBatter.WITH_HEIGHT_CODEC, CakeBatter.getHeightOnlyEmpty());
        }
        this.parts = Lists.newArrayList(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.get("parts")).result().orElse(Lists.newArrayList()).iterator());
    }

    public static void tick(World world, BlockPos pos, BlockState state, BakingTrayBlockEntity blockEntity) {
        if (world.getBlockState(pos.down()).isIn(PBTags.Blocks.BAKES_CAKE) && !blockEntity.getCakeBatter().isEmpty()) {
            blockEntity.getCakeBatter().bakeTick(world, pos, state);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            PBHelpers.updateListeners(blockEntity);
            if (blockEntity.getCakeBatter().getBakeTime() == PedrosBakery.CONFIG.ticksUntilBaked()) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), PBSounds.BAKING_TRAY_DONE, SoundCategory.BLOCKS, 1.25f, 1.0f, true);
            }
        }
        if (!world.isClient()) {
            blockEntity.updateParts(world, pos, state);
        }
    }

    public CakeBatter<HeightOnlyBatterSizeContainer> getCakeBatter() {
        return this.cakeBatter;
    }

    public void setCakeBatter(@NotNull CakeBatter<HeightOnlyBatterSizeContainer> cakeBatter) {
        this.cakeBatter = cakeBatter;
        if (this.cakeBatter.getSizeContainer().getHeight() > this.getHeight()) {
            this.cakeBatter.getSizeContainer().setHeight(this.getHeight());
        }
        this.markDirty();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        this.markDirty();
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
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

    @Override
    public void setStackNbt(ItemStack stack) {
        super.setStackNbt(stack);
        PBHelpers.set(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, this.getCakeBatter());
        PBHelpers.set(stack, PBComponentTypes.SIZE, this.getSize());
        PBHelpers.set(stack, PBComponentTypes.HEIGHT, this.getHeight());
    }

    public void readFrom(ItemStack stack) {
        this.setCakeBatter(PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty()).copy());
        this.setSize(PBHelpers.getOrDefault(stack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize()));
        this.setHeight(PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()));
    }
}
