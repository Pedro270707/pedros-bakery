package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
import net.pedroricardo.block.PieBlock;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

public class PieBlockEntity extends BlockEntity {
    private int topBakeTime = 0;
    private ItemStack fillingItem = ItemStack.EMPTY;
    private int bottomBakeTime = 0;

    public PieBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.PIE, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.topBakeTime = nbt.getInt("top_bake_time");
        this.fillingItem = ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound("item"));
        this.bottomBakeTime = nbt.getInt("bottom_bake_time");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("top_bake_time", this.topBakeTime);
        if (!this.fillingItem.isEmpty()) {
            nbt.put("item", this.fillingItem.encode(registryLookup));
        }
        nbt.putInt("bottom_bake_time", this.bottomBakeTime);
    }

    public ItemStack getFillingItem() {
        return this.fillingItem;
    }

    public void setFillingItem(ItemStack fillingItem) {
        this.fillingItem = fillingItem;
    }

    public int getTopBakeTime() {
        return this.topBakeTime;
    }

    public int getBottomBakeTime() {
        return this.bottomBakeTime;
    }

    public void setTopBakeTime(int topBakeTime) {
        this.topBakeTime = topBakeTime;
    }

    public void setBottomBakeTime(int bottomBakeTime) {
        this.bottomBakeTime = bottomBakeTime;
    }

    public static void tick(World world, BlockPos pos, BlockState state, PieBlockEntity blockEntity) {
        if (world.getBlockState(pos.down()).isIn(PBTags.Blocks.BAKES_CAKE)) {
            blockEntity.setBottomBakeTime(world.getBlockState(pos).getOrEmpty(PieBlock.BOTTOM).orElse(false) ? blockEntity.getBottomBakeTime() + 1 : 0);
            blockEntity.setTopBakeTime(world.getBlockState(pos).getOrEmpty(PieBlock.TOP).orElse(false) ? blockEntity.getTopBakeTime() + 1 : 0);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            if (!world.isClient()) {
                PBHelpers.update(blockEntity, (ServerWorld) world);
            }
            if (blockEntity.getTopBakeTime() == PedrosBakery.CONFIG.ticksUntilPieBaked() || blockEntity.getBottomBakeTime() == PedrosBakery.CONFIG.ticksUntilPieBaked()) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), PBSounds.PIE_DONE, SoundCategory.BLOCKS, 1.25f, 1.0f, true);
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
