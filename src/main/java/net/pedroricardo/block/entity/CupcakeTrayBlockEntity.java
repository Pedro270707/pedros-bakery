package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PBSounds;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.CupcakeTrayBatter;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import org.jetbrains.annotations.Nullable;

public class CupcakeTrayBlockEntity extends BlockEntity implements ItemComponentProvider {
    private CupcakeTrayBatter batter;

    public CupcakeTrayBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CUPCAKE_TRAY.get(), pos, state);
        this.batter = CupcakeTrayBatter.getEmpty();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.getBatter().toNbt(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.batter = CupcakeTrayBatter.fromNbt(nbt);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CupcakeTrayBlockEntity blockEntity) {
        if (world.getBlockState(pos.below()).is(PBTags.Blocks.BAKES_CAKE)) {
            blockEntity.getBatter().stream().forEach(batter -> {
                if (batter.isEmpty()) return;
                batter.bakeTick(world, pos, state);
                if (batter.getBakeTime() == PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
                    world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), PBSounds.BAKING_TRAY_DONE.get(), SoundSource.BLOCKS, 1.25f, 1.0f, true);
                }
            });
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
            if (!world.isClientSide()) {
                PBHelpers.update(blockEntity, (ServerLevel) world);
            }
        }
    }

    public CupcakeTrayBatter getBatter() {
        return this.batter;
    }

    public void setBatter(CupcakeTrayBatter batter) {
        this.batter = batter;
        this.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void addComponents(ItemStack stack) {
        PBHelpers.set(stack, PBComponentTypes.CUPCAKE_TRAY_BATTER.get(), this.getBatter());
    }

    public void readFrom(ItemStack stack) {
        this.setBatter(PBHelpers.getOrDefault(stack, PBComponentTypes.CUPCAKE_TRAY_BATTER.get(), CupcakeTrayBatter.getEmpty()));
    }
}
