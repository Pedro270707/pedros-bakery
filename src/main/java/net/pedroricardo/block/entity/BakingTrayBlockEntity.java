package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PBSounds;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.BakingTrayBlock;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BakingTrayBlockEntity extends MultipartBlockEntity<BakingTrayBlockEntity> {
    private int size = PedrosBakery.CONFIG.bakingTrayDefaultSize.get();
    private int height = PedrosBakery.CONFIG.bakingTrayDefaultHeight.get();
    private CakeBatter<HeightOnlyBatterSizeContainer> cakeBatter = CakeBatter.getHeightOnlyEmpty();

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
        if (!this.isMainPart()) return;
        nbt.putInt("size", this.size);
        nbt.putInt("height", this.height);
        nbt.put("batter", this.getCakeBatter().toNbt(new NbtCompound(), CakeBatter.WITH_HEIGHT_CODEC));
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (!this.isMainPart()) return;
        if (nbt.contains("size", NbtElement.INT_TYPE)) {
            this.size = nbt.getInt("size");
        }
        if (nbt.contains("height", NbtElement.INT_TYPE)) {
            this.height = nbt.getInt("height");
        }
        if (nbt.contains("batter", NbtElement.COMPOUND_TYPE)) {
            this.cakeBatter = CakeBatter.fromNbt(nbt.getCompound("batter"), CakeBatter.WITH_HEIGHT_CODEC, CakeBatter.getHeightOnlyEmpty());
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BakingTrayBlockEntity blockEntity) {
        if (!blockEntity.isMainPart()) return;
        if (world.getBlockState(pos.down()).isIn(PBTags.Blocks.BAKES_CAKE) && !blockEntity.getCakeBatter().isEmpty()) {
            blockEntity.getCakeBatter().bakeTick(world, pos, state);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            if (!world.isClient()) {
                PBHelpers.update(blockEntity, (ServerWorld) world);
            }
            if (blockEntity.getCakeBatter().getBakeTime() == PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), PBSounds.BAKING_TRAY_DONE, SoundCategory.BLOCKS, 1.25f, 1.0f, true);
            }
        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(PBComponentTypes.HEIGHT_ONLY_BATTER, this.getCakeBatter().copy());
        componentMapBuilder.add(PBComponentTypes.SIZE, this.getSize());
        componentMapBuilder.add(PBComponentTypes.HEIGHT, this.getHeight());
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.cakeBatter = components.getOrDefault(PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty()).copy();
        this.size = components.getOrDefault(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        this.height = components.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove("batter");
        nbt.remove("size");
        nbt.remove("height");
    }

    public CakeBatter<HeightOnlyBatterSizeContainer> getCakeBatter() {
        return this.getMainPart().cakeBatter;
    }

    public void setCakeBatter(@NotNull CakeBatter<HeightOnlyBatterSizeContainer> cakeBatter) {
        BakingTrayBlockEntity main = this.getMainPart();
        main.cakeBatter = cakeBatter;
        if (main.cakeBatter.getSizeContainer().getHeight() > this.getHeight()) {
            main.cakeBatter.getSizeContainer().setHeight(this.getHeight());
        }
        main.markDirty();
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.asItem());
        stack.set(PBComponentTypes.HEIGHT_ONLY_BATTER, this.getCakeBatter().copy());
        stack.set(PBComponentTypes.SIZE, this.getSize());
        stack.set(PBComponentTypes.HEIGHT, this.getHeight());
        return stack;
    }

    public int getSize() {
        return this.getMainPart().size;
    }

    public void setSize(int size) {
        BakingTrayBlockEntity main = this.getMainPart();
        main.size = size;
        main.markDirty();
    }

    public int getHeight() {
        return this.getMainPart().height;
    }

    public void setHeight(int height) {
        BakingTrayBlockEntity main = this.getMainPart();
        main.height = height;
        main.markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
