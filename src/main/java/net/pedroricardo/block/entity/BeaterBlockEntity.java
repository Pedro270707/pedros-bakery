package net.pedroricardo.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.BeaterBlock;
import net.pedroricardo.block.extras.beater.Liquid;
import net.pedroricardo.item.recipes.MixingPatternEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeaterBlockEntity extends BlockEntity implements Clearable {
    private int poweredTicks;
    private int mixTime;
    private boolean powered;
    ArrayList<ItemStack> items = new ArrayList<>();
    @Nullable Liquid liquid;

    public BeaterBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.BEATER.get(), pos, state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!this.getItems().isEmpty()) {
            nbt.put("items", ItemStack.CODEC.listOf().xmap(list -> list.stream().filter(stack -> !stack.isEmpty()).toList(), Function.identity()).encodeStart(NbtOps.INSTANCE, this.getItems()).get().orThrow());
            nbt.putInt("mix_time", this.mixTime);
        }
        if (this.liquid != null) {
            this.liquid.toNbt(nbt);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        Optional<Pair<List<ItemStack>, Tag>> optional = ItemStack.CODEC.listOf().decode(NbtOps.INSTANCE, nbt.getList("items", Tag.TAG_COMPOUND)).result();
        this.items = optional.map(pair -> new ArrayList<>(pair.getFirst())).orElseGet(ArrayList::new);

        if (!this.getItems().isEmpty() && nbt.contains("mix_time", Tag.TAG_INT)) {
            this.mixTime = nbt.getInt("mix_time");
        }
        this.liquid = Liquid.fromNbt(nbt).orElse(null);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, BeaterBlockEntity blockEntity) {
        if (blockEntity.getItems().isEmpty()) {
            blockEntity.mixTime = 0;
        }
        if (state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED)) {
            blockEntity.powered = true;
            ++blockEntity.poweredTicks;
            if (!blockEntity.getItems().isEmpty()) {
                ++blockEntity.mixTime;
            }
        } else {
            blockEntity.powered = false;
        }
        if (blockEntity.updateLiquid() && blockEntity.tryToMix(world, state, pos, blockEntity)) {
            blockEntity.mixTime = 0;
        }
        if (!world.isClientSide()) {
            PBHelpers.update((ServerLevel) world, pos, blockEntity);
        }
    }

    private boolean tryToMix(Level world, BlockState state, BlockPos pos, BeaterBlockEntity beater) {
        if (beater.getMixTime() <= 200) return false;
        Optional<MixingPatternEntry> optional = PedrosBakery.MIXING_PATTERN_MANAGER.getFirstMatch(beater.getItems(), beater.getLiquid());
        if (optional.isEmpty()) return false;
        beater.setItems(List.of());
        beater.setLiquid(optional.get().entry().getResult());
        return true;
    }

    public float getPoweredTicks(float tickDelta) {
        if (this.powered) {
            return (float)this.poweredTicks + tickDelta;
        }
        return this.poweredTicks;
    }

    public ArrayList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        this.mixTime = 0;
        if (this.getLevel() != null) {
            if (!this.getLevel().isClientSide()) PBHelpers.update(this, (ServerLevel) this.getLevel());
        } else {
            this.setChanged();
        }
    }

    public void addItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            List<ItemStack> items = this.getItems();
            items.add(stack);
            this.setItems(items);
        }
    }

    public @Nullable Liquid getLiquid() {
        return this.liquid;
    }

    public void setLiquid(@Nullable Liquid liquid) {
        this.liquid = liquid;
        if (this.getLevel() != null) {
            if (!this.getLevel().isClientSide()) PBHelpers.update(this, (ServerLevel) this.getLevel());
        } else {
            this.setChanged();
        }
    }

    /**
     * Updates the liquid state, syncing the block property and the {@code liquid} field.
     * @return whether there is liquid or not
     */
    public boolean updateLiquid() {
        if (this.getBlockState().hasProperty(BeaterBlock.HAS_LIQUID)) {
            if (!this.getBlockState().getValue(BeaterBlock.HAS_LIQUID) || this.getLiquid() == null) {
                this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(BeaterBlock.HAS_LIQUID, false));
                this.setLiquid(null);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the block has liquid on both the block property and {@code liquid} field without updating
     * @return whether there is liquid or not
     */
    public boolean hasLiquid() {
        return this.getBlockState().hasProperty(BeaterBlock.HAS_LIQUID) && this.getBlockState().getValue(BeaterBlock.HAS_LIQUID) && this.getLiquid() != null;
    }

    public int getMixTime() {
        return this.mixTime;
    }

    @Override
    public void clearContent() {
        this.setItems(List.of());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
