package net.pedroricardo.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        super(PBBlockEntities.BEATER, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.getItems().isEmpty()) {
            nbt.put("items", ItemStack.CODEC.listOf().xmap(list -> list.stream().filter(stack -> !stack.isEmpty()).toList(), Function.identity()).encodeStart(NbtOps.INSTANCE, this.getItems()).getOrThrow());
            nbt.putInt("mix_time", this.mixTime);
        }
        if (this.liquid != null) {
            this.liquid.toNbt(nbt);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        Optional<Pair<List<ItemStack>, NbtElement>> optional = ItemStack.CODEC.listOf().decode(NbtOps.INSTANCE, nbt.getList("items", NbtElement.COMPOUND_TYPE)).result();
        this.items = optional.map(pair -> new ArrayList<>(pair.getFirst())).orElseGet(ArrayList::new);

        if (!this.getItems().isEmpty() && nbt.contains("mix_time", NbtElement.INT_TYPE)) {
            this.mixTime = nbt.getInt("mix_time");
        }
        this.liquid = Liquid.fromNbt(nbt).orElse(null);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BeaterBlockEntity blockEntity) {
        if (blockEntity.getItems().isEmpty()) {
            blockEntity.mixTime = 0;
        }
        if (state.contains(Properties.POWERED) && state.get(Properties.POWERED)) {
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
        if (!world.isClient()) {
            PBHelpers.update((ServerWorld) world, pos, blockEntity);
        }
    }

    private boolean tryToMix(World world, BlockState state, BlockPos pos, BeaterBlockEntity beater) {
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
        if (this.getWorld() != null) {
            if (!this.getWorld().isClient()) PBHelpers.update(this, (ServerWorld) this.getWorld());
        } else {
            this.markDirty();
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
        if (this.getWorld() != null) {
            if (!this.getWorld().isClient()) PBHelpers.update(this, (ServerWorld) this.getWorld());
        } else {
            this.markDirty();
        }
    }

    /**
     * Updates the liquid state, syncing the block property and the {@code liquid} field.
     * @return whether there is liquid or not
     */
    public boolean updateLiquid() {
        if (this.getCachedState().contains(BeaterBlock.HAS_LIQUID)) {
            if (!this.getCachedState().get(BeaterBlock.HAS_LIQUID) || this.getLiquid() == null) {
                this.getWorld().setBlockState(this.getPos(), this.getCachedState().with(BeaterBlock.HAS_LIQUID, false));
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
        return this.getCachedState().contains(BeaterBlock.HAS_LIQUID) && this.getCachedState().get(BeaterBlock.HAS_LIQUID) && this.getLiquid() != null;
    }

    public int getMixTime() {
        return this.mixTime;
    }

    @Override
    public void clear() {
        this.setItems(List.of());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        if (this.hasWorld()) {
            componentMapBuilder.add(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(BeaterBlock.HAS_LIQUID, this.getWorld().getBlockState(this.getPos()).get(BeaterBlock.HAS_LIQUID)));
        }
    }
}
