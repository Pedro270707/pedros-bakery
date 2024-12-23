package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PBSounds;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PieDataComponent;
import org.jetbrains.annotations.Nullable;

public class PieBlockEntity extends BlockEntity implements ItemComponentProvider {
    private int layers = 0;
    private int slices = 0;
    private int topBakeTime = 0;
    private ItemStack fillingItem = ItemStack.EMPTY;
    private int bottomBakeTime = 0;

    public PieBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.PIE.get(), pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.layers = nbt.getInt("layers");
        this.slices = nbt.getInt("slices");
        this.topBakeTime = nbt.getInt("top_bake_time");
        this.fillingItem = ItemStack.of(nbt.getCompound("filling_item"));
        this.bottomBakeTime = nbt.getInt("bottom_bake_time");
        if (this.isEmpty()) {
            this.setBottomBakeTime(0);
            this.setLayers(0);
            this.setSlices(0);
            this.setFillingItem(ItemStack.EMPTY);
        }
        if (this.getLayers() < 2 || this.getFillingItem().isEmpty()) {
            this.setLayers(Math.min(this.getLayers(), 1));
        }
        if (this.getLayers() < 3) {
            this.setTopBakeTime(0);
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("layers", this.layers);
        nbt.putInt("slices", this.slices);
        if (this.getLayers() >= 1) {
            nbt.putInt("bottom_bake_time", this.bottomBakeTime);
        }
        if (this.getLayers() >= 2 && !this.fillingItem.isEmpty()) {
            nbt.put("filling_item", this.fillingItem.save(new CompoundTag()));
        }
        if (this.getLayers() >= 3) {
            nbt.putInt("top_bake_time", this.topBakeTime);
        }
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

    public int getLayers() {
        return this.layers;
    }

    public void setLayers(int layers) {
        this.layers = Mth.clamp(layers, 0, 3);
    }

    public int getSlices() {
        return this.slices;
    }

    public void setSlices(int slices) {
        this.slices = Mth.clamp(slices, 0, 4);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, PieBlockEntity blockEntity) {
        if (world.getBlockState(pos.below()).is(PBTags.Blocks.BAKES_CAKE)) {
            boolean isEmpty = blockEntity.isEmpty();
            blockEntity.setBottomBakeTime(isEmpty ? 0 : blockEntity.getBottomBakeTime() + 1);
            blockEntity.setTopBakeTime(isEmpty || blockEntity.getLayers() < 3 ? 0 : blockEntity.getTopBakeTime() + 1);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            if (!world.isClientSide()) {
                PBHelpers.update(blockEntity, (ServerLevel) world);
            }
            if (blockEntity.getTopBakeTime() == PedrosBakery.CONFIG.ticksUntilPieBaked.get() || blockEntity.getBottomBakeTime() == PedrosBakery.CONFIG.ticksUntilPieBaked.get()) {
                world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), PBSounds.PIE_DONE.get(), SoundSource.BLOCKS, 1.25f, 1.0f, true);
            }
        }
    }

    public boolean isEmpty() {
        if (this.getSlices() == 0) return true;
        return this.getLayers() == 0;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void readFrom(ItemStack stack) {
        PieDataComponent pieData = PBHelpers.getOrDefault(stack, PBComponentTypes.PIE_DATA.get(), PieDataComponent.EMPTY);
        this.setLayers(pieData.layers());
        this.setBottomBakeTime(pieData.bottomBakeTime());
        this.setFillingItem(pieData.filling());
        this.setTopBakeTime(pieData.topBakeTime());
        this.setSlices(pieData.slices());
    }

    @Override
    public void addComponents(ItemStack stack) {
        PBHelpers.set(stack, PBComponentTypes.PIE_DATA.get(), new PieDataComponent(this.getLayers(), this.getBottomBakeTime(), this.getFillingItem(), this.getTopBakeTime(), this.getSlices()));
    }
}
