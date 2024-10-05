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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.BeaterBlock;
import net.pedroricardo.block.extras.CakeFlavor;
import net.pedroricardo.block.extras.CakeFlavors;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.CakeTops;
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
    @Nullable CakeTop top = null;
    @Nullable CakeFlavor flavor = null;

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
        if (this.top != null) {
            Identifier topId = CakeTops.REGISTRY.getId(this.top);
            if (topId != null) {
                nbt.putString("top", topId.toString());
            }
        }
        if (this.flavor != null) {
            nbt.putString("flavor", CakeFlavors.REGISTRY.getId(this.flavor).toString());
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.item = ItemStack.fromNbtOrEmpty(registryLookup, nbt.getCompound("item"));
        if (!this.item.isEmpty() && nbt.contains("mix_time", NbtElement.INT_TYPE)) {
            this.mixTime = nbt.getInt("mix_time");
        }
        if (nbt.contains("top", NbtElement.STRING_TYPE)) {
            this.top = CakeTops.REGISTRY.getOrEmpty(Identifier.of(nbt.getString("top"))).orElse(null);
        }
        if (nbt.contains("flavor", NbtElement.STRING_TYPE)) {
            this.flavor = CakeFlavors.REGISTRY.getOrEmpty(Identifier.of(nbt.getString("flavor"))).orElse(null);
        }
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
        if (state.contains(BeaterBlock.LIQUID) && state.get(BeaterBlock.LIQUID).onMix(world, state, pos, blockEntity)) blockEntity.mixTime = 0;
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

    @Nullable
    public CakeTop getTop() {
        return this.top;
    }

    public void setTop(@Nullable CakeTop top) {
        this.top = top;
        PBHelpers.updateListeners(this);
    }

    @Nullable
    public CakeFlavor getFlavor() {
        return this.flavor;
    }

    public void setFlavor(@Nullable CakeFlavor flavor) {
        this.flavor = flavor;
        PBHelpers.updateListeners(this);
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
            componentMapBuilder.add(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(BeaterBlock.LIQUID, this.getWorld().getBlockState(this.getPos()).get(BeaterBlock.LIQUID)));
        }
    }
}
