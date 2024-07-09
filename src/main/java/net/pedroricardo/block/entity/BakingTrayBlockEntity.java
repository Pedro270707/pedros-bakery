package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
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
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.helpers.CakeBatter;
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
    private CakeBatter cakeBatter = CakeBatter.getEmpty();
    private List<BlockPos> parts = Lists.newArrayList();

    public BakingTrayBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.BAKING_TRAY, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("size", this.size);
        nbt.putInt("height", this.height);
        nbt.put("batter", this.getCakeBatter().toNbt(new NbtCompound()));
        nbt.put("parts", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.parts).result().orElse(new NbtList()));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("size", NbtElement.INT_TYPE)) {
            this.size = nbt.getInt("size");
        }
        if (nbt.contains("height", NbtElement.INT_TYPE)) {
            this.height = nbt.getInt("height");
        }
        if (nbt.contains("batter", NbtElement.COMPOUND_TYPE)) {
            this.cakeBatter = CakeBatter.fromNbt(nbt.getCompound("batter"));
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

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(PBComponentTypes.BATTER, this.getCakeBatter());
        componentMapBuilder.add(PBComponentTypes.SIZE, this.getSize());
        componentMapBuilder.add(PBComponentTypes.HEIGHT, this.getHeight());
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.cakeBatter = components.getOrDefault(PBComponentTypes.BATTER, CakeBatter.getEmpty());
        this.size = components.getOrDefault(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize());
        this.height = components.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight());
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove("batter");
        nbt.remove("size");
        nbt.remove("height");
        nbt.remove("parts");
    }

    public CakeBatter getCakeBatter() {
        return this.cakeBatter;
    }

    public void setCakeBatter(@NotNull CakeBatter cakeBatter) {
        this.cakeBatter = cakeBatter;
        if (this.cakeBatter.getHeight() > this.getHeight()) {
            this.cakeBatter.withHeight(this.getHeight());
        }
        this.cakeBatter.withSize(this.getSize());
        this.markDirty();
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.asItem());
        stack.set(PBComponentTypes.BATTER, this.getCakeBatter());
        stack.set(PBComponentTypes.SIZE, this.getSize());
        stack.set(PBComponentTypes.HEIGHT, this.getHeight());
        return stack;
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
}
