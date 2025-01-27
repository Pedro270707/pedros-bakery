package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
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
import net.pedroricardo.block.extras.CupcakeTrayBatter;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

public class CupcakeTrayBlockEntity extends BlockEntity {
    private CupcakeTrayBatter batter;

    public CupcakeTrayBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CUPCAKE_TRAY, pos, state);
        this.batter = CupcakeTrayBatter.getEmpty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        this.getBatter().toNbt(nbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.batter = CupcakeTrayBatter.fromNbt(nbt);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CupcakeTrayBlockEntity blockEntity) {
        if (world.getBlockState(pos.down()).isIn(PBTags.Blocks.BAKES_CAKE)) {
            blockEntity.getBatter().stream().forEach(batter -> {
                if (batter.isEmpty()) return;
                batter.bakeTick(world, pos, state);
                if (batter.getBakeTime() == PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
                    world.playSound(pos.getX(), pos.getY(), pos.getZ(), PBSounds.BAKING_TRAY_DONE, SoundCategory.BLOCKS, 1.25f, 1.0f, true);
                }
            });
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            if (!world.isClient()) {
                PBHelpers.update(blockEntity, (ServerWorld) world);
            }
        }
    }

    public CupcakeTrayBatter getBatter() {
        return this.batter;
    }

    public void setBatter(CupcakeTrayBatter batter) {
        this.batter = batter;
        this.markDirty();
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove("batter");
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(PBComponentTypes.CUPCAKE_TRAY_BATTER, this.getBatter());
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.batter = components.getOrDefault(PBComponentTypes.CUPCAKE_TRAY_BATTER, CupcakeTrayBatter.getEmpty());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
