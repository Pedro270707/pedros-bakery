package net.pedroricardo.block.extras.features;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

public class PlayerHeadCakeFeature extends CakeFeature {
    @Nullable
    public ProfileComponent getProfileComponent(CakeBatter<?> batter) {
        if (!this.getNbt(batter).contains("profile", NbtElement.COMPOUND_TYPE)) {
            return null;
        }
        return ProfileComponent.CODEC.parse(NbtOps.INSTANCE, this.getNbt(batter).getCompound("profile")).result().orElse(null);
    }

    public void setProfileComponent(CakeBatter<?> batter, @Nullable ProfileComponent component) {
        NbtCompound nbt = this.getNbt(batter);
        if (component == null) {
            nbt.remove("profile");
        } else {
            NbtCompound profileNbt = (NbtCompound) ProfileComponent.CODEC.encodeStart(NbtOps.INSTANCE, component).result().orElse(new NbtCompound());
            nbt.put("profile", profileNbt);
        }
        this.writeNbt(batter, nbt);
    }

    @Override
    public void onPlaced(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> batter, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (!stack.isOf(Items.PLAYER_HEAD)) return;
        this.setProfileComponent(batter, stack.get(DataComponentTypes.PROFILE));
    }
}
