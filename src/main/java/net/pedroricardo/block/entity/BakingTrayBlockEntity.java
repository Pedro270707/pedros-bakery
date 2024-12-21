package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
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

public class BakingTrayBlockEntity extends BlockEntity implements MultipartBlockEntity, ItemComponentProvider {
    private int size = PedrosBakery.CONFIG.bakingTrayDefaultSize.get();
    private int height = PedrosBakery.CONFIG.bakingTrayDefaultHeight.get();
    private CakeBatter<HeightOnlyBatterSizeContainer> cakeBatter = CakeBatter.getHeightOnlyEmpty();
    private List<BlockPos> parts = Lists.newArrayList();

    public BakingTrayBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.BAKING_TRAY.get(), pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("size", this.size);
        nbt.putInt("height", this.height);
        nbt.put("batter", this.getCakeBatter().toNbt(new CompoundTag(), CakeBatter.WITH_HEIGHT_CODEC));
        nbt.put("parts", BlockPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.parts).result().orElse(new ListTag()));
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("size", Tag.TAG_INT)) {
            this.size = nbt.getInt("size");
        }
        if (nbt.contains("height", Tag.TAG_INT)) {
            this.height = nbt.getInt("height");
        }
        if (nbt.contains("batter", Tag.TAG_COMPOUND)) {
            this.cakeBatter = CakeBatter.fromNbt(nbt.getCompound("batter"), CakeBatter.WITH_HEIGHT_CODEC, CakeBatter.getHeightOnlyEmpty());
        }
        this.parts = Lists.newArrayList(BlockPos.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.get("parts")).result().orElse(Lists.newArrayList()).iterator());
    }

    public static void tick(Level world, BlockPos pos, BlockState state, BakingTrayBlockEntity blockEntity) {
        if (world.getBlockState(pos.below()).is(PBTags.Blocks.BAKES_CAKE) && !blockEntity.getCakeBatter().isEmpty()) {
            blockEntity.getCakeBatter().bakeTick(world, pos, state);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            if (!world.isClientSide()) {
                PBHelpers.update(blockEntity, (ServerLevel) world);
            }
            if (blockEntity.getCakeBatter().getBakeTime() == PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
                world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), PBSounds.BAKING_TRAY_DONE.get(), SoundSource.BLOCKS, 1.25f, 1.0f, true);
            }
        }
        if (!world.isClientSide()) {
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
        this.setChanged();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        this.setChanged();
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
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

    public void readFrom(ItemStack stack) {
        this.setCakeBatter(PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty()).copy());
        this.setSize(PBHelpers.getOrDefault(stack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get()));
        this.setHeight(PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()));
    }

    @Override
    public void addComponents(ItemStack stack) {
        PBHelpers.set(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, this.getCakeBatter());
        PBHelpers.set(stack, PBComponentTypes.SIZE, this.getSize());
        PBHelpers.set(stack, PBComponentTypes.HEIGHT, this.getHeight());
    }
}
