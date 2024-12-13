package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.BeaterBlock;
import net.pedroricardo.block.extras.beater.Liquid;
import org.jetbrains.annotations.Nullable;

public class BeaterBlockEntity extends BlockEntity implements Clearable {
    private int poweredTicks;
    private int mixTime;
    private boolean powered;

    private record BeaterInventory(BeaterBlockEntity parent) implements Inventory {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return this.parent.item.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return slot == 0 ? this.parent.item : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            if (slot == 0) {
                return this.parent.item.split(amount);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot) {
            if (slot == 0) {
                ItemStack stack = this.parent.item;
                this.parent.item = ItemStack.EMPTY;
                return stack;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public void markDirty() {
            this.parent.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return false;
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public void clear() {
        }
    }
    private final Inventory inventory = new BeaterInventory(this);
    ItemStack item = ItemStack.EMPTY;
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
        if (!this.item.isEmpty()) {
            nbt.put("item", this.item.encode(registryLookup));
            nbt.putInt("mix_time", this.mixTime);
        }
        if (this.liquid != null) {
            this.liquid.toNbt(nbt);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.item = ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound("item"));
        if (!this.item.isEmpty() && nbt.contains("mix_time", NbtElement.INT_TYPE)) {
            this.mixTime = nbt.getInt("mix_time");
        }
        this.liquid = Liquid.fromNbt(nbt).orElse(null);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BeaterBlockEntity blockEntity) {
        if (blockEntity.getItem().isEmpty()) {
            blockEntity.mixTime = 0;
        }
        if (state.contains(Properties.POWERED) && state.get(Properties.POWERED)) {
            blockEntity.powered = true;
            ++blockEntity.poweredTicks;
            if (!blockEntity.getItem().isEmpty()) {
                ++blockEntity.mixTime;
            }
        } else {
            blockEntity.powered = false;
        }
        if (blockEntity.updateLiquid() && blockEntity.getLiquid().onMix(world, state, pos, blockEntity)) {
            blockEntity.mixTime = 0;
        }
        PBHelpers.updateListeners(world, pos, state, blockEntity);
    }

    public float getPoweredTicks(float tickDelta) {
        if (this.powered) {
            return (float)this.poweredTicks + tickDelta;
        }
        return this.poweredTicks;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack stack) {
        this.item = stack;
        this.mixTime = 0;
        PBHelpers.updateListeners(this);
    }

    public @Nullable Liquid getLiquid() {
        return this.liquid;
    }

    public void setLiquid(@Nullable Liquid liquid) {
        this.liquid = liquid;
        PBHelpers.updateListeners(this);
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
        this.setItem(ItemStack.EMPTY);
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
